# -*- coding:utf-8 -*-
import PropertiesReader
import json
import jsonpickle
from datetime import date
import requests
import time
import os
import zipfile
from zipfile import BadZipfile
import StringIO
import MidasUtil as util
import logging
import sys

# GET http://www.cninfo.com.cn/information/dividend/szsme002320.html
# GET http://www.cninfo.com.cn/information/issue/szsme002320.html


class ReportMetadata(object):
    def __init__(self, announcement_id, announcement_title, announcement_time):
        self.announcementId = announcement_id
        self.announcementTitle = announcement_title
        self.announcementTime = announcement_time
        self.is_download = False
        self.is_valid = False


class ReportMetadataCategory(object):
    def __init__(self, category):
        self.category = category
        self.fromCob = None             # for report date track purpose
        self.toCob = None               # for report date track purpose
        self.report_metadata = {}       # { report_id : ReportMetadata}

    def update_effective_cob(self, from_cob, to_cob):
        has_changed = False
        if self.fromCob is None or self.toCob is None:
            self.fromCob = from_cob
            self.toCob = to_cob
            has_changed = True
        else:
            if self.fromCob > from_cob:
                self.fromCob = from_cob
                has_changed = True
            if self.toCob < to_cob:
                self.toCob = to_cob
                has_changed = True
        return has_changed

    def add_report_metadata(self, metadata):
        if metadata.announcementId not in self.report_metadata:
            self.report_metadata[metadata.announcementId] = metadata
            return True
        else:
            return False

    def is_report_metadata_need_download(self, from_cob, to_cob):
        if self.fromCob is None or self.toCob is None:
            return True
        elif self.fromCob <= from_cob and self.toCob >= to_cob:
            return False
        else:
            return True


class StockData(object):
    def __init__(self, stock_code):
        self.stock_code = stock_code
        self.exchange_name = self.get_exchange_name()
        self.plate = ''
        self.market = ''
        self.ipo_file = None
        self.orgId = ''
        self.report_category = {}       # {category : ReportMetadataCategory}

    def get_report_metadata(self, metadata_category):
        if metadata_category not in self.report_category:
            self.report_category[metadata_category] = ReportMetadataCategory(metadata_category)
        return self.report_category[metadata_category]

    def get_exchange_name(self):
        if self.stock_code.startswith('6'):
            return "sse"
        elif self.stock_code.startswith('000') or self.stock_code.startswith('001'):
            return "szse_main"
        elif self.stock_code.startswith('002'):
            return "szse_sme"
        elif self.stock_code.startswith('3'):
            return "szse_gem"

    # true means something updated
    def check_stock_metadata(self):
        if not self.orgId:
            try:
                r = requests.post("http://www.cninfo.com.cn/cninfo-new/data/query",
                                  data={"keyWord": self.stock_code}, timeout=45, stream=False,
                                  headers={'Connection': 'close'})
                if r.status_code == requests.codes.ok:
                    result = json.loads(r.content)
                    if len(result) > 0:
                        self.orgId = result[0]['orgId']
                        self.market = result[0]['market']
                        return True
                logging.error('download metadata failed for stock ' + self.stock_code)
                return False
            except (requests.exceptions.ConnectionError, requests.exceptions.RequestException):
                logging.exception('download metadata failed for stock ' + self.stock_code)
                return False
        return False


class CnInfoManager:
    base_path = ''
    base_url = 'http://www.cninfo.com.cn/'
    ipo_url = base_url + 'information/issue/szsme%s.html'
    dividend_url = base_url + 'information/dividend/szsme%s.html'
    announcement_url = base_url + 'information/dividend/szsme%s.html'
    report_metadata_url = base_url + 'cninfo-new/announcement/query'
    price_url = base_url + 'cninfo-new/data/download'
    report_url = base_url + 'cninfo-new/disclosure/{exchange_name}/download/{id}'
    price_path_pattern = '{code}/price/{market}_hq_{code}_{year}.csv'
    report_path_pattern = u'{code}/reports/{id}_{cob}_{title}.pdf'
    report_categories = ["category_ndbg_szsh;", "category_bndbg_szsh;", "category_yjdbg_szsh;", "category_sjdbg_szsh;"]
    report_ignore_patterns = [u"摘要", u"H股", u"英文版", u"英文"]
    ipo_category = "category_scgkfx_szsh;"
    stocks = {}         # stock_code -> StockData
    current_stock = None
    current_category_metadata = None
    current_report_metadata = None
    allowed_domains = ["cninfo.com.cn"]
    minYear = '2000'
    maxYear = None
    fromYear = None
    toYear = None
    fromCob = None
    toCob = None
    forceDownload = False   # True means download regardless existence, False won't download when exist

    def __init__(self, base_path):
        self.base_path = base_path
        self.maxYear = str(date.today().year)
        self.deserialization_stock_data()
        self.do_cmd('collect_disk_data')

    def serialization_stock_data(self):
        util.serialization_object(self.base_path + 'stocks.json', self.stocks)

    def serialization_single_stock_data(self):
        logging.warn('save metadata for ' + self.current_stock.stock_code)
        util.serialization_object(self.base_path + self.current_stock.stock_code + '/metadata.json',
                                  self.current_stock)

    def deserialization_stock_data(self):
        obj = util.deserialization_object(self.base_path + 'stocks.json')
        if obj is not None:
            self.stocks = obj

    def collect_data_from_stock_dir(self, stock_code):
        folder = self.base_path + stock_code
        if not os.path.exists(folder):
            os.mkdir(folder)
        else:
            obj = util.deserialization_object(folder + '/metadata.json')
            if obj is not None:     # use this one as benchmark
                self.current_stock = obj
                self.stocks[stock_code] = self.current_stock

    def do_cmd(self, cmd_str):
        if cmd_str == 'delete_un_downloadable_reports' or cmd_str == 'fix_metadata':
            is_sure = raw_input("are you sure to continue? (y/n)\n")
            if is_sure != 'y':
                return
        all_stock_codes = util.get_all_stock_codes()
        for code in all_stock_codes:
            if code is not None and not code.startswith('IDX'):
                stock_code = code[2:]
                if stock_code not in self.stocks:
                    self.current_stock = StockData(stock_code)
                    self.stocks[stock_code] = self.current_stock
                else:
                    self.current_stock = self.stocks[stock_code]

                self.do_cmd_single(cmd_str, stock_code)

    def do_cmd_single(self, cmd_str, stock_code):
        if cmd_str == 'download_price_zip':
            self.download_price_zip()
        elif cmd_str == 'collect_disk_data':
            self.collect_data_from_stock_dir(stock_code)
        elif cmd_str == 'download_stock_metadata':
            self.download_stock_metadata()
        elif cmd_str == 'download_report_metadata':
            self.download_report_metadata()
        elif cmd_str == 'download_reports':
            self.download_reports()
        elif cmd_str == 'get_un_download_reports':
            self.get_un_download_reports()
        elif cmd_str == 'check_report_integrity':
            self.check_report_integrity()
        elif cmd_str == 'delete_un_downloadable_reports':
            self.delete_un_downloadable_reports()
        elif cmd_str == 'fix_metadata':
            self.fix_metadata()

    def delete_un_downloadable_reports(self):
        has_deleted = False
        for category in self.current_stock.report_category:
            self.current_category_metadata = self.current_stock.report_category[category]
            to_delete_report_ids = []
            for report_id in self.current_category_metadata.report_metadata:
                self.current_report_metadata = self.current_category_metadata.report_metadata[report_id]
                if not self.current_report_metadata.is_download:
                    to_delete_report_ids.append(report_id)

            if len(to_delete_report_ids) > 0:
                has_deleted = True
                for to_delete_report_id in to_delete_report_ids:
                    logging.warn(self.current_stock.stock_code + " delete report " +
                                 self.current_category_metadata.report_metadata[to_delete_report_id].announcementTitle)
                    del self.current_category_metadata.report_metadata[to_delete_report_id]

        if has_deleted:
            self.serialization_single_stock_data()

    def get_un_download_reports(self):
        for category in self.current_stock.report_category:
            self.current_category_metadata = self.current_stock.report_category[category]
            for report_id in self.current_category_metadata.report_metadata:
                self.current_report_metadata = self.current_category_metadata.report_metadata[report_id]
                date_str, target_path, target_url = self.get_report_path()
                if not self.current_report_metadata.is_download:
                    logging.warn("missing report " + target_path + " , " + target_url)

    def download_stock_metadata(self):
        if self.current_stock.check_stock_metadata():
            self.serialization_single_stock_data()

    def download_report_metadata(self):
        for category in self.report_categories:
            self.download_report_metadata_category(category)

    def download_report_metadata_category(self, category):
        has_new_report_metadata = False
        self.current_category_metadata = self.current_stock.get_report_metadata(category)
        if self.current_category_metadata.is_report_metadata_need_download(self.fromCob, self.toCob):
            try:
                page_num = 1
                while True:
                    r = requests.post(self.report_metadata_url,
                                      files={
                                          "stock": (None, self.current_stock.stock_code),
                                          "category": (None, category),
                                          "pageNum": (None, str(page_num)),
                                          "pageSize": (None, "30"),
                                          "column": (None, self.current_stock.exchange_name),
                                          "tabName": (None, "fulltext"),
                                          "seDate": (None, util.cob2date_range_string(self.fromCob, self.toCob)),
                                      }, timeout=45, stream=False,
                                      headers={'Connection': 'close'})
                    if r.status_code == requests.codes.ok:
                        result = json.loads(r.content)
                        reports = result['announcements']
                        for report in reports:
                            if not util.array_contains(report['announcementTitle'], self.report_ignore_patterns):
                                report_metadata = ReportMetadata(report['announcementId'],
                                                                 report['announcementTitle'],
                                                                 report['announcementTime'])
                                if self.current_category_metadata.add_report_metadata(report_metadata):
                                    has_new_report_metadata = True

                        if result['hasMore']:
                            page_num += 1
                        else:
                            break
                    else:
                        logging.error('download report metadata for ' + self.current_stock.stock_code)
                        return None
                if self.current_category_metadata.update_effective_cob(self.fromCob, self.toCob) \
                        or has_new_report_metadata:
                    self.serialization_single_stock_data()
            except (IOError, AttributeError, RuntimeError):
                logging.exception(self.current_stock.stock_code + ' save report metadata failed')

    def download_reports(self):
        dir_path = self.base_path + '/' + self.current_stock.stock_code + '/reports/'
        if not os.path.exists(dir_path):
            os.makedirs(dir_path)

        for category in self.current_stock.report_category:
            self.current_category_metadata = self.current_stock.report_category[category]
            has_new_report_downloaded = False
            for report_id in self.current_category_metadata.report_metadata:
                self.current_report_metadata = self.current_category_metadata.report_metadata[report_id]
                if not self.current_report_metadata.is_download:
                    if self.download_report():
                        has_new_report_downloaded = True
            if has_new_report_downloaded:
                self.serialization_single_stock_data()

    def download_report(self):
        date_str, target_path, target_url = self.get_report_path()
        if os.path.exists(target_path):
            self.current_report_metadata.is_download = True
            return True

        try:
            logging.warn("download " + target_path)
            r = requests.get(target_url, stream=True, params={"announceTime": date_str})
            with open(target_path, 'wb') as fd:
                for chunk in r.iter_content(chunk_size=16384):
                    fd.write(chunk)
            self.current_report_metadata.is_download = True
            return True
        except (IOError, RuntimeError):
            logging.exception(self.current_stock.stock_code + ' save report failed. ' +
                              self.current_report_metadata.announcementTitle)
            util.delete_file(target_path)
            return False

    def check_report_integrity(self):
        for category in self.current_stock.report_category:
            self.current_category_metadata = self.current_stock.report_category[category]
            has_integrity_checked = False
            to_delete_report_ids = []
            for report_id in self.current_category_metadata.report_metadata:
                self.current_report_metadata = self.current_category_metadata.report_metadata[report_id]
                date_str, target_path, target_url = self.get_report_path()
                if util.array_contains(self.current_report_metadata.announcementTitle, self.report_ignore_patterns):
                    if self.current_report_metadata.is_download:
                        util.delete_file(target_path)
                    to_delete_report_ids.append(report_id)
                elif self.current_report_metadata.is_download:
                    if not self.current_report_metadata.is_valid:
                        if util.is_invalid_pdf(target_path):
                            has_integrity_checked = True
                            self.current_report_metadata.is_download = False
                            self.current_report_metadata.is_valid = False
                            util.delete_file(target_path)
                        else:
                            has_integrity_checked = True
                            self.current_report_metadata.is_valid = True

            if len(to_delete_report_ids) > 0:
                has_integrity_checked = True
                for to_delete_report_id in to_delete_report_ids:
                    logging.warn(self.current_stock.stock_code + " delete report " +
                                 self.current_category_metadata.report_metadata[to_delete_report_id].announcementTitle)
                    del self.current_category_metadata.report_metadata[to_delete_report_id]

            if has_integrity_checked:
                self.serialization_single_stock_data()

    def get_report_path(self):
        date_str = util.timestamp2date_str(self.current_report_metadata.announcementTime / 1000)
        report_path = self.report_path_pattern.format(code=self.current_stock.stock_code,
                                                      id=self.current_report_metadata.announcementId,
                                                      cob=util.date_str2cob(date_str),
                                                      title=self.current_report_metadata.announcementTitle)
        target_path = self.base_path + report_path.replace("*", "")
        target_url = self.report_url.format(exchange_name=self.current_stock.exchange_name,
                                            id=self.current_report_metadata.announcementId)
        return date_str, target_path, target_url

    def set_current_stock(self, stock_code):
        if stock_code not in self.stocks:
            self.current_stock = None
        else:
            self.current_stock = self.stocks[stock_code]

    def download_ipo_report(self, stock_code):
        if stock_code not in self.stocks:
            self.current_stock = None
        else:
            self.current_stock = self.stocks[stock_code]

    def is_price_file_exist(self):
        target_path = self.base_path + self.price_path_pattern.format(market=self.current_stock.market,
                                                                      code=self.current_stock.stock_code,
                                                                      year=self.toYear)
        return os.path.exists(target_path)

    def download_price_zip(self):
        if not self.forceDownload and self.is_price_file_exist():
            # logging.warn(self.current_stock.stock_code + ' target year price file exist.')
            return True
        try:
            r = requests.post(self.price_url,
                              files={
                                  "market": (None, self.current_stock.market),
                                  "type": (None, "hq"),
                                  "code": (None, self.current_stock.stock_code),
                                  "minYear": (None, self.fromYear),
                                  "maxYear": (None, self.toYear),
                                  "orgid": (None, self.current_stock.orgId)
                              }, timeout=45, stream=False,
                              headers={'Connection': 'close'})
            if r.status_code == requests.codes.ok:
                z = zipfile.ZipFile(StringIO.StringIO(r.content))
                z.extractall(path=self.base_path + '/' + self.current_stock.stock_code + '/price/')
        except (IOError, RuntimeError, BadZipfile):
            logging.exception(self.current_stock.stock_code + ' save price zip file failed')
            return False
        return True

    def fix_metadata(self):
        for category in self.current_stock.report_category:
            self.current_category_metadata = self.current_stock.report_category[category]
            for report_id in self.current_category_metadata.report_metadata:
                self.current_report_metadata = self.current_category_metadata.report_metadata[report_id]
                self.current_report_metadata.is_valid = False

        self.serialization_single_stock_data()

        if self.current_stock.exchange_name is None:
            self.current_stock.exchange_name = self.current_stock.get_exchange_name()
            self.serialization_single_stock_data()

if __name__ == '__main__':
    jsonpickle.set_encoder_options('simplejson', sort_keys=True, indent=4)
    my_props = PropertiesReader.get_properties()
    cninfo_path = my_props['MktDataLoader.Fundamental.cninfo']
    log_path = cninfo_path + "log/log_" + time.strftime("%Y%m%d_%H_%M_%S", time.localtime()) + ".txt"
    if len(sys.argv) > 1:
        log_path = cninfo_path + "log/log_" + time.strftime("%Y%m%d_%H_%M_%S", time.localtime()) + sys.argv[1] + ".txt"
    logging.basicConfig(filename=log_path, level=logging.WARN)
    manager = CnInfoManager(cninfo_path)

    # single stock function debug purpose
    # manager.set_current_stock(u'000001')
    # manager.download_report_metadata()

    cmd_string = 'download_stock_metadata'
    if len(sys.argv) > 1:
        cmd_string = sys.argv[1]
        if cmd_string == 'download_price_zip':
            if len(sys.argv) == 4:
                manager.fromYear = sys.argv[2]
                manager.toYear = sys.argv[3]
            else:
                manager.fromYear = manager.maxYear
                manager.toYear = manager.maxYear
                manager.forceDownload = True
        elif cmd_string == 'download_report_metadata':
            manager.fromCob = int(sys.argv[2])
            manager.toCob = int(sys.argv[3])
    print "do command ", cmd_string
    print 'argument list:', str(sys.argv)
    manager.do_cmd(cmd_string)
    manager.serialization_stock_data()
    print cmd_string + ' finished'
