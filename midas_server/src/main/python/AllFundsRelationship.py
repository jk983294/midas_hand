# -*- coding: utf-8 -*-
# crawl all fund A, fund B, mother fund info
# https://www.jisilu.cn/data/sfnew/fundb_list/
import urllib2
import json
import pandas as pd
import PropertiesReader
import MidasUtil as util


def parse_data(data):
    fundb_base_fund_id = []
    funda_id = []
    funda_name = []
    fundb_id = []
    fundb_name = []
    maturity_dt = []
    coupon_descr_s = []             # 利率规则
    fundb_nav_dt = []
    fundb_discount_rt = []          # 溢价率
    fundb_price_leverage_rt = []    # 杠杆率
    fundb_capital_rasising_rt = []  # 融资成本
    fundb_lower_recalc_rt = []      # 下折母基跌幅
    fundb_upper_recalc_rt = []      # 上折母基涨幅
    fundb_base_est_dis_rt = []      # 整体溢价率
    abrate = []                     # a/b 份额比
    fundb_base_price = []           # 母基净值
    funda_current_price = []        # a基现价
    fundb_current_price = []        # b基现价
    fundb_value = []                # b基净值
    rows = data['rows']
    for row in rows:
        row_data = row['cell']
        fundb_base_fund_id.append(util.json_object_to_convert(row_data['fundb_base_fund_id']))
        funda_id.append(util.json_object_to_convert(row_data['funda_id']))
        funda_name.append(util.json_object_to_convert(row_data['funda_name']))
        fundb_id.append(util.json_object_to_convert(row_data['fundb_id']))
        fundb_name.append(util.json_object_to_convert(row_data['fundb_name']))
        maturity_dt.append(util.json_object_to_convert(row_data['maturity_dt']))
        coupon_descr_s.append(util.json_object_to_convert(row_data['coupon_descr_s']))
        fundb_nav_dt.append(util.json_object_to_convert(row_data['fundb_nav_dt']))
        fundb_discount_rt.append(util.json_object_to_convert(row_data['fundb_discount_rt']))
        fundb_price_leverage_rt.append(util.json_object_to_convert(row_data['fundb_price_leverage_rt']))
        fundb_capital_rasising_rt.append(util.json_object_to_convert(row_data['fundb_capital_rasising_rt']))
        fundb_lower_recalc_rt.append(util.json_object_to_convert(row_data['fundb_lower_recalc_rt']))
        fundb_base_est_dis_rt.append(util.json_object_to_convert(row_data['fundb_base_est_dis_rt']))
        abrate.append(util.json_object_to_convert(row_data['abrate']))
        fundb_base_price.append(util.json_object_to_convert(row_data['fundb_base_price']))
        fundb_upper_recalc_rt.append(util.json_object_to_convert(row_data['fundb_upper_recalc_rt']))
        funda_current_price.append(util.json_object_to_convert(row_data['funda_current_price']))
        fundb_current_price.append(util.json_object_to_convert(row_data['fundb_current_price']))
        fundb_value.append(util.json_object_to_convert(row_data['fundb_value']))

    d = {
        'fundb_base_fund_id': fundb_base_fund_id,
        'funda_id': funda_id,
        'fundb_id': fundb_id,
        'funda_name': funda_name,
        'fundb_name': fundb_name,
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
    return pd.DataFrame(d)


if __name__ == '__main__':
    url = 'https://www.jisilu.cn/data/sfnew/fundb_list/'
    my_props = PropertiesReader.get_properties()
    df = parse_data(util.json_data_get(url))
    df.to_csv(my_props['MktDataLoader.Fund.AllFundsRelationship.Path'])
