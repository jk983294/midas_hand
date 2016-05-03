# -*- coding: utf-8 -*-
# crawl stock index future data using jisilu
# https://www.jisilu.cn/data/index_future/if_hist_list/IH1605
# https://www.jisilu.cn/data/index_future/if_list/IF IC IH
import pandas as pd
import requests
from requests import RequestException
import PropertiesReader
import MidasUtil as util
import time
from random import randint


def get_data_from_jisilu(url):
    r = requests.get(url, timeout=45, stream=False, headers={
        'Accept-encoding': 'gzip',
        'Host': 'www.jisilu.cn',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36',
        'Accept-Language': 'zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4'
    })
    return r.json()


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


def get_contract_data_jisilu(contract_id):
    url = 'https://www.jisilu.cn/data/index_future/if_hist_list/' + contract_id
    json_data = get_data_from_jisilu(url)
    contract_ids = []
    rows = json_data['rows']
    for row in rows:
        contract_ids.append(util.json_object_to_convert(row['id']))
    return contract_ids


def get_contract_id_jisilu(prefix):
    url = 'https://www.jisilu.cn/data/index_future/if_list/' + prefix
    json_data = get_data_from_jisilu(url)
    contract_ids = []
    rows = json_data['rows']
    for row in rows:
        contract_ids.append(util.json_object_to_convert(row['id']))
    return contract_ids


if __name__ == '__main__':
    contract_prefix = ['IF', 'IH', 'IC']
    my_props = PropertiesReader.get_properties()
    base_path = my_props['MktDataLoader.StockIndexFuture.CrawlData.Path']
    contract_ids = []
    for prefix in contract_prefix:
        try:
            contract_ids.append(get_contract_id_jisilu(prefix))
            print 'finish get contract_ids: ', prefix
            time.sleep(randint(5, 25))
        except RequestException as inst:
            print inst
            print 'failed contract_ids ', prefix
    print 'crawl contract_ids finished'

    for contract_id in contract_ids:
        try:
            contract_ids.append(get_contract_id_jisilu(prefix))
            print 'finish get contract_data: ', contract_id
            time.sleep(randint(5, 25))
        except RequestException as inst:
            print inst
            print 'failed contract_data ', contract_id
    print 'crawl contract_data finished'

