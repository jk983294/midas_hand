# -*- coding: utf-8 -*-
import PropertiesReader
import tushare as ts


def get_industry_info(file_path):
    industry_info = ts.get_industry_classified()
    industry_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload industry info finished\n'


def get_concept_info(file_path):
    concept_info = ts.get_concept_classified()
    concept_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload concept info finished\n'


def get_area_info(file_path):
    area_info = ts.get_concept_classified()
    area_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload area info finished\n'


def get_sme_info(file_path):
    sme_info = ts.get_sme_classified()
    sme_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload sme info finished\n'


def get_gem_info(file_path):
    gem_info = ts.get_gem_classified()
    gem_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload gem info finished\n'


def get_st_info(file_path):
    st_info = ts.get_st_classified()
    st_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload st info finished\n'


def get_hs300s_info(file_path):
    hs300s_info = ts.get_hs300s()
    hs300s_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload hs300s info finished\n'


def get_sz50s_info(file_path):
    sz50s_info = ts.get_sz50s()
    sz50s_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload sz50s info finished\n'


def get_zz500s_info(file_path):
    zz500s_info = ts.get_zz500s()
    zz500s_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload zz500s info finished\n'


def get_terminated_info(file_path):
    terminated_info = ts.get_terminated()
    terminated_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload terminated info finished\n'


def get_suspended_info(file_path):
    suspended_info = ts.get_suspended()
    suspended_info.to_csv(file_path, encoding='utf-8')
    print '\ndownload suspended info finished\n'


if __name__ == '__main__':
    my_props = PropertiesReader.get_properties()
    get_industry_info(my_props['MktDataLoader.Fundamental.Class.Industry'])
    get_concept_info(my_props['MktDataLoader.Fundamental.Class.Concept'])
    get_area_info(my_props['MktDataLoader.Fundamental.Class.Area'])
    get_sme_info(my_props['MktDataLoader.Fundamental.Class.SME'])                   # 中小板分类
    get_gem_info(my_props['MktDataLoader.Fundamental.Class.GEM'])                   # 创业板分类
    get_st_info(my_props['MktDataLoader.Fundamental.Class.ST'])                     # 风险警示板分类
    get_hs300s_info(my_props['MktDataLoader.Fundamental.Class.Hs300s'])             # 沪深300成份及权重
    get_sz50s_info(my_props['MktDataLoader.Fundamental.Class.Sz50s'])               # 上证50成份股
    get_zz500s_info(my_props['MktDataLoader.Fundamental.Class.Zz500s'])             # 中证500成份股 终止上市股票列表
    get_terminated_info(my_props['MktDataLoader.Fundamental.Class.terminated'])     # 终止上市股票列表
    get_suspended_info(my_props['MktDataLoader.Fundamental.Class.suspended'])       # 暂停上市股票列表
    print 'download class info finished'

