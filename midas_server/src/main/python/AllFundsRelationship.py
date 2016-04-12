# -*- coding: utf-8 -*-
# crawl all fund A, fund B, mother fund info
# https://www.jisilu.cn/data/sfnew/fundb_list/
import urllib2
import json
import pandas as pd
import PropertiesReader


def json_data_get():
    url = 'https://www.jisilu.cn/data/sfnew/fundb_list/'
    response = urllib2.urlopen(url)
    resp = response.read()
    return json.loads(resp)


def parse_data(data):
    fundb_base_fund_id = []
    funda_id = []
    fundb_id = []
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
        fundb_base_fund_id.append(row_data['fundb_base_fund_id'].encode("utf-8"))
        funda_id.append(row_data['funda_id'].encode("utf-8"))
        fundb_id.append(row_data['fundb_id'].encode("utf-8"))
        coupon_descr_s.append(row_data['coupon_descr_s'].encode("utf-8"))
        fundb_nav_dt.append(row_data['fundb_nav_dt'].encode("utf-8"))
        fundb_discount_rt.append(row_data['fundb_discount_rt'].encode("utf-8"))
        fundb_price_leverage_rt.append(row_data['fundb_price_leverage_rt'].encode("utf-8"))
        fundb_capital_rasising_rt.append(row_data['fundb_capital_rasising_rt'].encode("utf-8"))
        fundb_lower_recalc_rt.append(row_data['fundb_lower_recalc_rt'].encode("utf-8"))
        fundb_base_est_dis_rt.append(row_data['fundb_base_est_dis_rt'].encode("utf-8"))
        abrate.append(row_data['abrate'].encode("utf-8"))
        fundb_base_price.append(row_data['fundb_base_price'].encode("utf-8"))
        fundb_upper_recalc_rt.append(row_data['fundb_upper_recalc_rt'].encode("utf-8"))
        funda_current_price.append(row_data['funda_current_price'].encode("utf-8"))
        fundb_current_price.append(row_data['fundb_current_price'].encode("utf-8"))
        fundb_value.append(row_data['fundb_value'].encode("utf-8"))

    d = {
        'fundb_base_fund_id': fundb_base_fund_id,
        'funda_id': funda_id,
        'fundb_id': fundb_id,
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
    my_props = PropertiesReader.get_properties()
    df = parse_data(json_data_get())
    df.to_csv(my_props['MktDataLoader.Fund.AllFundsRelationship.Path'])
