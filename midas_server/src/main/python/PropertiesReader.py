# -*- coding: utf-8 -*-
# read java properties file


def get_properties():
    file_path = '../resources/local.properties'
    my_properties = {}
    with open(file_path, 'r') as f:
        for line in f:
            line = line.rstrip()                # removes trailing whitespace and '\n' chars
            if "=" not in line: continue        # skips blanks and comments w/o =
            if line.startswith("#"): continue   # skips comments which contain =
            k, v = line.split("=", 1)
            my_properties[k.strip()] = v.strip()
    return my_properties

if __name__ == '__main__':
    my_props = get_properties()
    print my_props
