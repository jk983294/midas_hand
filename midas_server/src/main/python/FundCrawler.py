# -*- coding: utf-8 -*-
# crawl fund data using tushare and jisilu
import urllib2
import json
import pandas as pd
import PropertiesReader
import tushare as ts


def setup_token():
    ts.set_token('xxx')
    token = ts.get_token()
    print token


def get_fund_data(fund_code):
    fd = ts.Fund()
    return fd.FundNav(ticker=fund_code, beginDate='20150101', field='ticker,endDate,NAV,publishDate,ACCUM_NAV,ADJUST_NAV')


def get_fund_data_jisilu(fund_code):
    fd = ts.Fund()
    return fd.FundNav(ticker=fund_code, beginDate='20150101', field='ticker,endDate,NAV,publishDate,ACCUM_NAV,ADJUST_NAV')


def test_get_fund_data_jisilu(my_props):
    fund_code = '150274'
    df = get_fund_data(fund_code)
    df.to_csv(my_props['MktDataLoader.Fund.CrawlData.Path'] + fund_code + '.csv')


def test_get_fund_data(my_props):
    fund_code = '150274'
    df = get_fund_data(fund_code)
    df.to_csv(my_props['MktDataLoader.Fund.CrawlData.Path'] + fund_code + '.csv')

if __name__ == '__main__':
    fund_code = '150274'
    my_props = PropertiesReader.get_properties()
    test_get_fund_data(my_props)


