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


# 业绩报告
# code,代码
# name,名称
# eps,每股收益
# eps_yoy,每股收益同比(%)
# bvps,每股净资产
# roe,净资产收益率(%)
# epcf,每股现金流量(元)
# net_profits,净利润(万元)
# profits_yoy,净利润同比(%)
# distrib,分配方案
# report_date,发布日期
def download_report_info(file_path, year, quarter):
    report_data = ts.get_report_data(year, quarter)
    if report_data is not None:
        report_data.to_csv(file_path + 'report_' + str(year) + '_' + str(quarter) + '.csv', encoding='utf-8')


# 盈利能力
# code,代码
# name,名称
# roe,净资产收益率(%)
# net_profit_ratio,净利率(%)
# gross_profit_rate,毛利率(%)
# net_profits,净利润(万元)
# eps,每股收益
# business_income,营业收入(百万元)
# bips,每股主营业务收入(元)
def download_profit_data(file_path, year, quarter):
    profit_data = ts.get_profit_data(year, quarter)
    if profit_data is not None:
        profit_data.to_csv(file_path + 'profit_' + str(year) + '_' + str(quarter) + '.csv', encoding='utf-8')


# 营运能力
# code,代码
# name,名称
# arturnover,应收账款周转率(次)
# arturndays,应收账款周转天数(天)
# inventory_turnover,存货周转率(次)
# inventory_days,存货周转天数(天)
# currentasset_turnover,流动资产周转率(次)
# currentasset_days,流动资产周转天数(天)
def download_operation_data(file_path, year, quarter):
    operation_data = ts.get_operation_data(year, quarter)
    if operation_data is not None:
        operation_data.to_csv(file_path + 'operation_' + str(year) + '_' + str(quarter) + '.csv', encoding='utf-8')


# 成长能力
# code,代码
# name,名称
# mbrg,主营业务收入增长率(%)
# nprg,净利润增长率(%)
# nav,净资产增长率
# targ,总资产增长率
# epsg,每股收益增长率
# seg,股东权益增长率
def download_growth_data(file_path, year, quarter):
    growth_data = ts.get_growth_data(year, quarter)
    if growth_data is not None:
        growth_data.to_csv(file_path + 'growth_' + str(year) + '_' + str(quarter) + '.csv', encoding='utf-8')


# 偿债能力
# code,代码
# name,名称
# currentratio,流动比率
# quickratio,速动比率
# cashratio,现金比率
# icratio,利息支付倍数
# sheqratio,股东权益比率
# adratio,股东权益增长率
def download_debtpaying_data(file_path, year, quarter):
    debtpaying_data = ts.get_debtpaying_data(year, quarter)
    if debtpaying_data is not None:
        debtpaying_data.to_csv(file_path + 'debtpaying_' + str(year) + '_' + str(quarter) + '.csv', encoding='utf-8')


# code,代码
# name,名称
# cf_sales,经营现金净流量对销售收入比率
# rateofreturn,资产的经营现金流量回报率
# cf_nm,经营现金净流量与净利润的比率
# cf_liabilities,经营现金净流量对负债比率
# cashflowratio,现金流量比率
def download_cashflow_data(file_path, year, quarter):
    cashflow_data = ts.get_cashflow_data(year, quarter)
    if cashflow_data is not None:
        cashflow_data.to_csv(file_path + 'cashflow_' + str(year) + '_' + str(quarter) + '.csv', encoding='utf-8')


def get_report_info(file_path, year_from, quarter_from, year_to, quarter_to):
    year = year_from
    quarter = quarter_from
    is_first_year = True
    while year <= year_to:
        if is_first_year:
            quarter = quarter_from
            is_first_year = False
        else:
            quarter = 1

        last_quarter = 4
        if year == year_to:
            last_quarter = quarter_to

        while quarter <= last_quarter:
            print '\ndownload reports: ', year, '-', quarter, '\n'
            download_report_info(file_path, year, quarter)
            download_profit_data(file_path, year, quarter)
            download_operation_data(file_path, year, quarter)
            download_growth_data(file_path, year, quarter)
            download_debtpaying_data(file_path, year, quarter)
            download_cashflow_data(file_path, year, quarter)
            quarter += 1
        year += 1
    print 'download report_data finished\n'


if __name__ == '__main__':
    my_props = PropertiesReader.get_properties()
    get_basic_info(my_props['MktDataLoader.Fundamental.Class.stock_basics'])        # 沪深上市公司基本情况
    get_report_info(my_props['MktDataLoader.Fundamental.reports'], 1993, 1, 2015, 4)
    print 'download class info finished'

