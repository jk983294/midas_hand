# -*- coding: utf-8 -*-
# crawl fund data using tushare and jisilu
import urllib2
import json
import pandas as pd
import PropertiesReader
import tushare as ts
import MidasUtil as util


def setup_token():
    ts.set_token('xxx')
    token = ts.get_token()
    print token


def get_fund_data(fund_code):
    fd = ts.Fund()
    return fd.FundNav(ticker=fund_code, beginDate='20150101', field='ticker,endDate,NAV,publishDate,ACCUM_NAV,ADJUST_NAV')


def get_fund_data_jisilu(fund_code):
    url = 'https://www.jisilu.cn/jisiludata/StockFenJiDetail.php?qtype=hist&display=table&fund_id=' + fund_code
    df = parse_jisilu_data(util.json_data_get(url))
    fd = ts.Fund()
    return fd.FundNav(ticker=fund_code, beginDate='20150101', field='ticker,endDate,NAV,publishDate,ACCUM_NAV,ADJUST_NAV')


def parse_jisilu_data(data):
    price_dt = []
    a_price = []
    a_price_increase_rt = []        # A涨幅
    a_profit_rt = []                # A收益率
    a_amount = []                   # A份额（万份）
    a_amount_increase = []          # A新增份额（万份）
    a_amount_increase_rt = []       # A份额增长率
    a_discount_rt = []              # A折价率
    b_discount_rt = []              # B溢价率
    b_net_leverage_rt = []          # b基净值杠杆率
    b_price_leverage_rt = []        # b基价格杠杆率
    base_discount_rt = []           # 合并溢价
    net_value = []                  # 母基净值
    base_est_val = []               # 母基估值
    est_err = []                    # 母基估值误差
    rows = data['rows']
    for row in rows:
        row_data = row['cell']
        price_dt.append(util.json_object_to_convert(row_data['price_dt']))
        a_price.append(util.json_object_to_convert(row_data['a_price']))
        a_price_increase_rt.append(util.json_object_to_convert(row_data['a_price_increase_rt']))
        a_profit_rt.append(util.json_object_to_convert(row_data['a_profit_rt']))
        a_amount.append(util.json_object_to_convert(row_data['a_amount']))
        a_amount_increase.append(util.json_object_to_convert(row_data['a_amount_increase']))
        a_amount_increase_rt.append(util.json_object_to_convert(row_data['a_amount_increase_rt']))
        a_discount_rt.append(util.json_object_to_convert(row_data['a_discount_rt']))
        b_discount_rt.append(util.json_object_to_convert(row_data['b_discount_rt']))
        b_net_leverage_rt.append(util.json_object_to_convert(row_data['b_net_leverage_rt']))
        b_price_leverage_rt.append(util.json_object_to_convert(row_data['b_price_leverage_rt']))
        base_discount_rt.append(util.json_object_to_convert(row_data['base_discount_rt']))
        net_value.append(util.json_object_to_convert(row_data['net_value']))
        base_est_val.append(util.json_object_to_convert(row_data['base_est_val']))
        est_err.append(util.json_object_to_convert(row_data['est_err']))

    d = {
        'price_dt': price_dt,
        'a_price': a_price,
        'a_price_increase_rt': a_price_increase_rt,
        'a_profit_rt': a_profit_rt,
        'a_amount': a_amount,
        'maturity_dt': maturity_dt,
        'coupon_descr_s': coupon_descr_s,
        'fundb_nav_dt': fundb_nav_dt,
        'fundb_discount_rt': fundb_discount_rt,
        'fundb_price_leverage_rt': fundb_price_leverage_rt,
        'fundb_capital_rasising_rt': fundb_capital_rasising_rt,
        'fundb_lower_recalc_rt': fundb_lower_recalc_rt,
        'fundb_base_est_dis_rt': fundb_base_est_dis_rt,
        'abrate': abrate,
        'fundb_upper_recalc_rt': fundb_upper_recalc_rt,
        'funda_current_price': funda_current_price,
        'fundb_current_price': fundb_current_price,
        'fundb_value': fundb_value,
        'fundb_base_price': fundb_base_price}
    df = pd.DataFrame(d)
    df = df.set_index('fundb_base_fund_id')
    return df


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


