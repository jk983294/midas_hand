# -*- coding:utf-8 -*-
import PropertiesReader
import urllib2
import json
import datetime
import time
import os
import urllib
import re
import codecs
import MidasUtil as util

# GET http://www.cninfo.com.cn/information/dividend/szsme002320.html
# GET http://www.cninfo.com.cn/information/issue/szsme002320.html
# POST http://www.cninfo.com.cn/cninfo-new/announcement/query?stock=002320&searchkey=&category=category_ndbg_szsh%3Bcategory_bndbg_szsh%3Bcategory_yjdbg_szsh%3Bcategory_sjdbg_szsh%3B&pageNum=1&pageSize=30&column=szse_sme&tabName=fulltext&sortName=&sortType=&limit=&seDate=


class StockData:
    stock_code = ''
    plate = ''

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
    financialFolder = r'E:\financialdata'
    savedInfoFile = financialFolder + '\\' + 'stockreportlist.json'
    savedStockSumNum = 0

    def __init__(self, base_path):
        self.base_path = base_path
        self.collect_disk_data()

    def collect_data_from_stock_dir(self, stock_code):
        stock_data = StockData(stock_code)
        folder = self.base_path + '\\' + stock_code
        if not os.path.exists(folder):
            os.mkdir(folder)
        return stock_data

    def collect_disk_data(self):
        for code in util.get_all_stock_codes():
            if code is not None and not code.startswith('IDX'):
                stock_code = code[2:]
                self.stocks[stock_code] = self.collect_data_from_stock_dir(stock_code)


if __name__ == '__main__':
    my_props = PropertiesReader.get_properties()
    manager = CnInfoManager(my_props['MktDataLoader.Fundamental.cninfo'])
    print 'download class info finished'
