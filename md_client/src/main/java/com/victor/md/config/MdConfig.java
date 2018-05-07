package com.victor.md.config;

import com.victor.md.exception.KeyNotExistException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

public class MdConfig {
    private HashMap<String, Properties> store;
    private BufferedReader br;

    public MdConfig() {
        this.store = new HashMap<>();
    }

    public void load(String key, String name, String value) {
        synchronized (this) {
            Properties it = store.get(key);
            if (it == null) {
                it = new Properties();
                store.put(key, it);
            }
            it.put(name, value);
        }
    }

    public void load(String cfg) throws IOException {
        InputStream is = new FileInputStream(cfg);
        InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
        br = new BufferedReader(isr);

        String next;
        String key = null;
        while ((next = br.readLine()) != null) {
            next = next.trim(); //trim the string first

            if (next.length() == 0) { //blank line
                continue;
            }

            if (next.charAt(0) == '#') { //comment line
                continue;
            }

            int b = next.indexOf('[') + 1;
            int e = next.indexOf(']');
            if (b != -1 && e != -1 && (b < e)) {
                key = next.substring(b, e).trim(); //extract key
                continue;
            }

            String[] nv = next.split("=");
            if (nv.length == 2) {
                nv[0] = nv[0].trim();
                nv[1] = nv[1].trim();

                if (key != null && !key.isEmpty() && !nv[0].isEmpty() && !nv[1].isEmpty()) {
                    load(key, nv[0], nv[1]);
                }
            }

        }

    }

    //only used by test, if want to call in production, need to be locked or synchronized
    public boolean exist(String key, String name) {
        if (store.containsKey(key)) {
            if (store.get(key).getProperty(name) != null)
                return true;
        }
        return false;
    }

    public String queryString(String key, String name) throws KeyNotExistException {
        synchronized (this) {
            String value = null;
            if (store.containsKey(key)) {
                value = store.get(key).getProperty(name);
                if (value == null) {
                    throw new KeyNotExistException("Under key " + key + ", name: " + name + " not exist");
                }
                return value;
            }
            throw new KeyNotExistException("Key: " + key + " not exist");
        }
    }

    private String queryStringNoThrow(String key, String name) {
        return store.get(key).getProperty(name);
    }

    public String queryString(String key, String name, String defval) throws KeyNotExistException {
        synchronized (this) {
            if (!exist(key, name))
                return defval;
            return queryStringNoThrow(key, name);
        }
    }

    public long queryLong(String key, String name) throws KeyNotExistException {
        synchronized (this) {
            return Long.parseLong(queryString(key, name));
        }
    }

    public long queryLong(String key, String name, long defval) {
        synchronized (this) {
            if (!exist(key, name))
                return defval;
            return Long.parseLong(queryStringNoThrow(key, name));
        }
    }

    public int queryInt(String key, String name) throws KeyNotExistException {
        synchronized (this) {
            return Integer.parseInt(queryString(key, name));
        }
    }

    public int queryInt(String key, String name, int defval) {
        synchronized (this) {
            if (!exist(key, name))
                return defval;
            return Integer.parseInt(queryStringNoThrow(key, name));
        }
    }

    public short queryShort(String key, String name) throws KeyNotExistException {
        synchronized (this) {
            return Short.parseShort(queryString(key, name));
        }
    }

    public short queryShort(String key, String name, short defval) {
        synchronized (this) {
            if (!exist(key, name))
                return defval;
            return Short.parseShort(queryStringNoThrow(key, name));
        }
    }

    public double queryDouble(String key, String name) throws KeyNotExistException {
        synchronized (this) {
            return Double.parseDouble(queryString(key, name));
        }
    }

    public double queryDouble(String key, String name, double defval) {
        synchronized (this) {
            if (!exist(key, name))
                return defval;
            return Double.parseDouble(queryStringNoThrow(key, name));
        }
    }

    private boolean parseBool(String s) {
        boolean value = false;
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("y") || s.equals("1"))
            value = true;
        else if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("n") || s.equals("0"))
            value = false;
        else {
        } //validation value
        return value;
    }

    public boolean queryBool(String key, String name) throws KeyNotExistException {
        synchronized (this) {
            return parseBool(queryString(key, name));
        }
    }

    public boolean queryBool(String key, String name, boolean defval) {
        synchronized (this) {
            if (!exist(key, name))
                return defval;
            return parseBool(queryStringNoThrow(key, name));
        }
    }

    public void properties(String key, ArrayList<String> names) {
        synchronized (this) {
            names.clear();
            Properties it = store.get(key);
            for (Entry<Object, Object> entry : it.entrySet()) {
                names.add(entry.getKey().toString());
            }
        }
    }

    public void keys(ArrayList<String> keys) {
        synchronized (this) {
            keys.clear();
            for (Entry<String, Properties> entry : store.entrySet()) {
                keys.add(entry.getKey());
            }
        }
    }

}
