# -*- coding: utf-8 -*-
import urllib2
import json


def json_object_to_convert(obj):
    if obj:
        return obj.encode("utf-8")
    else:
        return obj


def json_data_get(url):
    response = urllib2.urlopen(url)
    resp = response.read()
    return json.loads(resp)