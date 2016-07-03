# -*- coding:utf-8 -*-
import PropertiesReader
import urllib2
import json
import jsonpickle
import datetime
import requests
import time
import os
import urllib
import re
import codecs
import MidasUtil as util
import logging
from random import randint

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
                # time.sleep(randint(1, 15))
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
    stocks = {}         # stock_code -> StockData
    current_stock = None
    allowed_domains = ["cninfo.com.cn"]

    def __init__(self, base_path):
        self.base_path = base_path
        self.deserialization_stock_data()
        self.collect_disk_data()

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
        return util.deserialization_object(folder + '/metadata.json')

    def collect_disk_data(self):
        all_stock_codes = util.get_all_stock_codes()
        for code in all_stock_codes:
            if code is not None and not code.startswith('IDX'):
                stock_code = code[2:]
                obj = self.collect_data_from_stock_dir(stock_code)
                if obj is not None:
                    self.current_stock = obj
                    self.stocks[stock_code] = self.current_stock
                elif stock_code not in self.stocks:
                    self.current_stock = StockData(stock_code)
                    self.stocks[stock_code] = self.current_stock
                else:
                    self.current_stock = self.stocks[stock_code]
                if self.current_stock.check_stock_metadata():
                    logging.info('save metadata for ' + self.current_stock.stock_code)
                    util.serialization_object(self.base_path + self.current_stock.stock_code + '/metadata.json',
                                              self.current_stock)

    def set_current_stock(self, stock_code):
        if stock_code in self.stocks:
            self.current_stock = None
        else:
            self.current_stock = self.stocks[stock_code]

    def download_price_zip(self, store_location, download_url):
        # pdfPath = self.base_path + '/' + file_name + '.pdf'
        # realURL = self.homePage + "/" + download_url
        # print pdfPath, realURL
        try:
            r = requests.post(download_url,
                              files={
                                  "market": (None, self.current_stock.market),
                                  "type": (None, "hq"),
                                  "code": (None, self.current_stock.stock_code),
                                  "minYear": (None, "2000"),
                                  "maxYear": (None, "2016"),
                                  "orgid": (None, self.current_stock.orgId)
                              }
                              )
            file_obj = open(store_location, 'w')
            file_obj.write(r.content)
            file_obj.close()
        except IOError:
            logging.exception(store_location + ' save failed, with url ' + download_url)
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
    my_props = PropertiesReader.get_properties()
    cninfo_path = my_props['MktDataLoader.Fundamental.cninfo']
    log_path = cninfo_path + "log/log_" + time.strftime("%Y%m%d_%H_%M_%S", time.localtime()) + ".txt"
    logging.basicConfig(filename=log_path, level=logging.INFO)
    manager = CnInfoManager(cninfo_path)
    # manager.download_pdf('', '')
    manager.set_current_stock('000001')
    manager.download_price_zip('F:/Data/MktData/fundamental/cninfo/000001/sz_hq_000001_2005_2016.zip', 'http://www.cninfo.com.cn/cninfo-new/data/download')
    manager.serialization_stock_data()
    print 'download class info finished'
