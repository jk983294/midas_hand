# -*- coding: utf-8 -*-
# crawl fund data using jisilu
import pandas as pd
import requests
import PropertiesReader
import MidasUtil as util
import AllFundsRelationship as allFunds
import time
from random import randint


def get_fund_data_jisilu(fund_code):
    url = 'https://www.jisilu.cn/jisiludata/StockFenJiDetail.php?qtype=hist&display=table&fund_id=' + fund_code
    r = requests.get(url, timeout=45, stream=False, headers={
        'Accept-encoding': 'gzip',
        'Host': 'www.jisilu.cn',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36',
        'Accept-Language': 'zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4'
    })
    data = r.json()
    df = parse_jisilu_data(data)
    return df


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
        'a_amount_increase': a_amount_increase,
        'a_amount_increase_rt': a_amount_increase_rt,
        'a_discount_rt': a_discount_rt,
        'b_discount_rt': b_discount_rt,
        'b_net_leverage_rt': b_net_leverage_rt,
        'b_price_leverage_rt': b_price_leverage_rt,
        'base_discount_rt': base_discount_rt,
        'net_value': net_value,
        'base_est_val': base_est_val,
        'est_err': est_err
    }
    df = pd.DataFrame(d)
    df = df.set_index('price_dt')
    return df


def save_fund_data(df, file_path):
    if df is None:
        return

    old_df = util.get_dataframe_from_file(file_path)
    if old_df is not None:
        old_df = old_df.set_index('price_dt')
        result = df.combine_first(old_df)  # accept new data, but in case lose old data, use combine_first
        result.to_csv(file_path)
    else:
        df.to_csv(file_path)


def test_get_fund_data_jisilu(base_path, fund_code):
    df = get_fund_data_jisilu(fund_code)
    file_path = base_path + fund_code + '.csv'
    save_fund_data(df, file_path)


if __name__ == '__main__':
    #fund_code = '160638'
    my_props = PropertiesReader.get_properties()
    all_funds = allFunds.load_all_funds_file(my_props['MktDataLoader.Fund.AllFundsRelationship.Path'])
    base_path = my_props['MktDataLoader.Fund.CrawlData.Path']
    for index, row in all_funds.iterrows():
        print index
        test_get_fund_data_jisilu(base_path, str(index))
        time.sleep(randint(5, 25))
    print 'crawl data finished'

