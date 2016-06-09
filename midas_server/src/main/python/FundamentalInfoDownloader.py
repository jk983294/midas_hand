# -*- coding: utf-8 -*-
import PropertiesReader
import tushare as ts


# code,代码
# name,名称
# industry,所属行业
# area,地区
# pe,市盈率
# outstanding,流通股本
# totals,总股本(万)
# totalAssets,总资产(万)
# liquidAssets,流动资产
# fixedAssets,固定资产
# reserved,公积金
# reservedPerShare,每股公积金
# eps,每股收益
# bvps,每股净资
# pb,市净率
# timeToMarket,上市日期
def get_basic_info(file_path):
    stock_basics = ts.get_stock_basics()
    stock_basics.to_csv(file_path, encoding='utf-8')
    print '\ndownload stock_basics finished\n'


def get_report_info(file_path, year, month):
    report_data = ts.get_report_data(year, month)
    report_data.to_csv(file_path + year + '_' + month + '.csv', encoding='utf-8')
    print 'download report_data finished\n'


if __name__ == '__main__':
    my_props = PropertiesReader.get_properties()
    get_basic_info(my_props['MktDataLoader.Fundamental.Class.stock_basics'])        # 沪深上市公司基本情况
    get_report_info(my_props['MktDataLoader.Fundamental.Class.stock_basics'])
    print 'download class info finished'

