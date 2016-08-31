# -*- coding: utf-8 -*-
import urllib2
import json
import os.path
import os
import pandas as pd
from PyPDF2.utils import PdfReadError
from pymongo import MongoClient
import jsonpickle
import datetime
import logging
import PyPDF2


def json_object_to_convert(obj):
    if obj:
        return obj.encode("utf-8")
    else:
        return obj


def json_data_get(url):
    response = urllib2.urlopen(url)
    resp = response.read()
    return json.loads(resp)


def get_dataframe_from_file(file_path):
    if os.path.isfile(file_path):
        return pd.read_csv(file_path)
    else:
        return None


def serialization_object(serialization_path, obj):
    f = open(serialization_path, 'w')
    f.write(jsonpickle.encode(obj).encode('utf-8'))
    f.close()


def deserialization_object(serialization_path):
    if os.path.exists(serialization_path):
        f = open(serialization_path, 'r')
        obj = jsonpickle.decode(f.read())
        f.close()
        return obj
    else:
        return None


def date_str2cob(date_str):
    date_time = datetime.datetime.strptime(date_str, '%Y-%m-%d')
    return int(date_time.strftime('%Y%m%d'))


def timestamp2date_str(timestamp):
    date_time = datetime.datetime.fromtimestamp(timestamp)
    return date_time.strftime('%Y-%m-%d')


def cob2date(cob):
    date_time = datetime.datetime.strptime(str(cob), '%Y%m%d')
    return date_time.strftime('%Y-%m-%d')


def cob2date_range_string(cob1, cob2):
    return "{cob1} ~ {cob2}".format(cob1=cob2date(cob1), cob2=cob2date(cob2))


def contains(source, pattern):
    return pattern in source


def array_contains(source, patterns):
    for pattern in patterns:
        if pattern in source:
            return True
    return False


def is_invalid_pdf(path):
    if not os.path.exists(path):
        logging.warn('not exist file : ' + path)
        return True
    try:
        fd = open(path, 'rb')
        PyPDF2.PdfFileReader(fd)
        fd.close()
        return False
    except (IOError, PdfReadError):
        logging.exception('invalid pdf : ' + path)
        return True


def get_all_stock_codes():
    client = MongoClient("mongodb://localhost:27017")
    db = client.prod
    names = db.StockMisc.find_one({"_id": "AllStockNames"})
    return names['stockNames']


def delete_file(file_path):
    try:
        if os.path.exists(file_path):
            os.remove(file_path)
            logging.warn('delete file : ' + file_path)
    except (IOError, RuntimeError):
        logging.exception('delete file failed : ' + file_path)

if __name__ == '__main__':
    print cob2date(20120923)
    print cob2date_range_string(20120923, 20160816)
    print timestamp2date_str(1430323200)
    print date_str2cob('2015-04-30')
    # print get_all_stock_codes()
    print 'test finished'

