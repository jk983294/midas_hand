# -*- coding:utf-8 -*-
import PropertiesReader
import urllib2
import json
import datetime
import requests
import time
import os
import urllib
import re
import codecs
import MidasUtil as util
import logging

# GET http://www.cninfo.com.cn/information/dividend/szsme002320.html
# GET http://www.cninfo.com.cn/information/issue/szsme002320.html
# POST http://www.cninfo.com.cn/cninfo-new/announcement/query?stock=002320&searchkey=&category=category_ndbg_szsh%3Bcategory_bndbg_szsh%3Bcategory_yjdbg_szsh%3Bcategory_sjdbg_szsh%3B&pageNum=1&pageSize=30&column=szse_sme&tabName=fulltext&sortName=&sortType=&limit=&seDate=


class StockData:
    stock_code = ''
    plate = ''
    ipo_file = None

    def __init__(self, stock_code):
        self.stock_code = stock_code


class CnInfoManager:
    base_path = ''
    base_url = 'http://www.cninfo.com.cn/'
    ipo_url = base_url + 'information/issue/szsme%s.html'
    dividend_url = base_url + 'information/dividend/szsme%s.html'
    announcement_url = base_url + 'information/dividend/szsme%s.html'
    stocks = {}         # stock_code -> StockData

    name = "cninfo"
    allowed_domains = ["cninfo.com.cn"]  
    start_urls = ["http://www.cninfo.com.cn/cninfo-new/announcement/show"]
    stockCodeSumNum = 2
    homePage = r"http://www.cninfo.com.cn"
    savedStockSumNum = 0

    def __init__(self, base_path):
        self.base_path = base_path
        self.collect_disk_data()
        log_path = self.base_path + "log/log_" + time.strftime("%Y%m%d_%H%M%S", time.localtime()) + ".txt"
        logging.basicConfig(filename=log_path, level=logging.DEBUG)
        logging.info('start log...')

    def collect_data_from_stock_dir(self, stock_code):
        stock_data = StockData(stock_code)
        folder = self.base_path + stock_code
        if not os.path.exists(folder):
            os.mkdir(folder)
        return stock_data

    def collect_disk_data(self):
        for code in util.get_all_stock_codes():
            if code is not None and not code.startswith('IDX'):
                stock_code = code[2:]
                self.stocks[stock_code] = self.collect_data_from_stock_dir(stock_code)

    def download_price_zip(self, store_location, download_url):
        # pdfPath = self.base_path + '/' + file_name + '.pdf'
        # realURL = self.homePage + "/" + download_url
        # print pdfPath, realURL
        try:
            r = requests.post(download_url,
                              # headers={
                              #     'Accept-encoding': 'gzip, deflate',
                              #     'Host': 'www.cninfo.com.cn',
                              #     'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
                              #     'Content-Type': 'multipart/form-data',
                              #     'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36',
                              #     'Accept-Language': 'zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4'
                              # },
                              # data={
                              #     "market": "sz",
                              #     "type": "hq",
                              #     "code": "000001",
                              #     "minYear": "2005",
                              #     "maxYear": "2016",
                              #     "hq_code": "000001",
                              #     "orgid": "gssz0000001"
                              # },
                              files={
                                  "market": (None, "sz"),
                                  "type": (None, "hq"),
                                  "code": (None, "000001"),
                                  "minYear": (None, "2005"),
                                  "maxYear": (None, "2016"),
                                  "hq_code": (None, "000001"),
                                  "orgid": (None, "gssz0000001")
                              }
                              )
            file_obj = open(store_location, 'w')
            file_obj.write(r.content)
            file_obj.close()
        except IOError:
            logging.error(store_location + ' save failed, with url ' + download_url)
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
            logging.error(store_location + ' save failed, with url ' + download_url)
            return False
        return True

if __name__ == '__main__':
    my_props = PropertiesReader.get_properties()
    manager = CnInfoManager(my_props['MktDataLoader.Fundamental.cninfo'])
    # manager.download_pdf('', '')
    manager.download_price_zip('F:/Data/MktData/fundamental/cninfo/000001/sz_hq_000001_2005_2016.zip', 'http://www.cninfo.com.cn/cninfo-new/data/download')
    print 'download class info finished'
