# -*- coding:utf-8 -*-
import PropertiesReader
import jsonpickle
from datetime import date
import requests
import time
import logging


class BondManager:
    base_path = ''
    national_debt_path = ''
    base_url = 'http://yield.chinabond.com.cn/'
    national_debt_url = base_url + 'cbweb-mn/yc/downYearBzqx'

    def __init__(self, base_path):
        self.base_path = base_path
        self.national_debt_path = base_path + 'national_debt/'

    def download_bond_excel(self, year):
        try:
            r = requests.get(self.national_debt_url, stream=True,
                             params={
                                  "year": year
                              })
            if r.status_code == requests.codes.ok:
                output = open(self.national_debt_path + str(year) + '_national_debt.xls', 'wb')
                output.write(r.content)
                output.close()
        except (IOError, RuntimeError):
            logging.exception('save national debt excel file failed')
            return False
        return True


if __name__ == '__main__':
    jsonpickle.set_encoder_options('simplejson', sort_keys=True, indent=4)
    my_props = PropertiesReader.get_properties()
    bond_path = my_props['MktDataLoader.Bond']
    national_debt_path = my_props['MktDataLoader.Bond.national.debt']
    log_path = bond_path + "log/log_" + time.strftime("%Y%m%d_%H_%M_%S", time.localtime()) + ".txt"
    logging.basicConfig(filename=log_path, level=logging.INFO)
    manager = BondManager(bond_path)
    cmd_string = 'download_national_debt'
    if cmd_string == 'download_national_debt':
        manager.download_bond_excel(date.today().year)
    print 'download class info finished'
