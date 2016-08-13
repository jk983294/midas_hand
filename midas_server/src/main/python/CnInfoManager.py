# -*- coding:utf-8 -*-
import PropertiesReader
import json
import jsonpickle
from datetime import date
import requests
import time
import os
import urllib
import zipfile
from zipfile import BadZipfile
import StringIO
import MidasUtil as util
import logging
import sys

# GET http://www.cninfo.com.cn/information/dividend/szsme002320.html
# GET http://www.cninfo.com.cn/information/issue/szsme002320.html
# POST http://www.cninfo.com.cn/cninfo-new/announcement/query?stock=002320&searchkey=&category=category_ndbg_szsh%3Bcategory_bndbg_szsh%3Bcategory_yjdbg_szsh%3Bcategory_sjdbg_szsh%3B&pageNum=1&pageSize=30&column=szse_sme&tabName=fulltext&sortName=&sortType=&limit=&seDate=


class StockData:
    stock_code = ''
    plate = ''
    market = ''
    ipo_file = None
    orgId = ''

    def __init__(self, stock_code):
        self.stock_code = stock_code

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
    price_url = base_url + 'cninfo-new/data/download'
    price_path_pattern = '{code}/price/{market}_hq_{code}_{year}.csv'
    stocks = {}         # stock_code -> StockData
    current_stock = None
    allowed_domains = ["cninfo.com.cn"]
    minYear = '2000'
    maxYear = None
    fromYear = None
    toYear = None
    forceDownload = False   # True means download regardless existence, False won't download when exist

    def __init__(self, base_path):
        self.base_path = base_path
        self.maxYear = str(date.today().year)
        self.deserialization_stock_data()
        self.do_cmd('collect_disk_data')

    def serialization_stock_data(self):
        util.serialization_object(self.base_path + 'stocks.json', self.stocks)

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
        all_stock_codes = util.get_all_stock_codes()
        for code in all_stock_codes:
            if code is not None and not code.startswith('IDX'):
                stock_code = code[2:]
                if stock_code not in self.stocks:
                    self.current_stock = StockData(stock_code)
                    self.stocks[stock_code] = self.current_stock
                else:
                    self.current_stock = self.stocks[stock_code]

                if cmd_str == 'download_price_zip':
                    manager.download_price_zip()
                elif cmd_str == 'collect_disk_data':
                    self.collect_data_from_stock_dir(stock_code)
                elif cmd_str == 'download_stock_metadata':
                    self.download_stock_metadata()

    def download_stock_metadata(self):
        if self.current_stock.check_stock_metadata():
            logging.info('save metadata for ' + self.current_stock.stock_code)
            util.serialization_object(self.base_path + self.current_stock.stock_code + '/metadata.json',
                                      self.current_stock)

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

    def download_reports(self, stock_code):
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
            logging.info(self.current_stock.stock_code + ' target year price file exist.')
            return True
        try:
            r = requests.post(self.price_url, stream=True,
                              files={
                                  "market": (None, self.current_stock.market),
                                  "type": (None, "hq"),
                                  "code": (None, self.current_stock.stock_code),
                                  "minYear": (None, self.fromYear),
                                  "maxYear": (None, self.toYear),
                                  "orgid": (None, self.current_stock.orgId)
                              })
            if r.status_code == requests.codes.ok:
                z = zipfile.ZipFile(StringIO.StringIO(r.content))
                z.extractall(path=self.base_path + '/' + self.current_stock.stock_code + '/price/')
        except (IOError, RuntimeError, BadZipfile):
            logging.exception(self.current_stock.stock_code + ' save price zip file failed')
            return False
        return True

    def download_pdf(self, store_location, download_url):
        # pdfPath = self.base_path + '/' + file_name + '.pdf'
        # realURL = self.homePage + "/" + download_url
        # print pdfPath, realURL
        try:
            if not os.path.exists(store_location):
                urllib.urlretrieve(download_url, store_location)
            else:
                logging.warning(store_location + ' is already exists, with url ' + download_url)
                return False
        except IOError:
            logging.exception(store_location + ' save failed, with url ' + download_url)
            return False
        return True

if __name__ == '__main__':
    jsonpickle.set_encoder_options('simplejson', sort_keys=True, indent=4)
    my_props = PropertiesReader.get_properties()
    cninfo_path = my_props['MktDataLoader.Fundamental.cninfo']
    log_path = cninfo_path + "log/log_" + time.strftime("%Y%m%d_%H_%M_%S", time.localtime()) + ".txt"
    logging.basicConfig(filename=log_path, level=logging.INFO)
    manager = CnInfoManager(cninfo_path)
    # manager.download_pdf('', '')
    # manager.set_current_stock(u'000001')

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
    print "do command ", cmd_string
    print 'argument list:', str(sys.argv)
    manager.do_cmd(cmd_string)
    manager.serialization_stock_data()
    print 'download class info finished'
