package com.victor.md.config;

import com.victor.md.exception.KeyNotExistException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TestConfig {
    @Test
    public void testConfig() throws IOException, KeyNotExistException {
        MdConfig mdConfig = new MdConfig();

        mdConfig.load(this.getClass().getClassLoader().getResource("").getPath() + "md.cfg");

        String key = "publisher";
        assertEquals(mdConfig.queryString(key, "publisher_ip"), "127.0.0.1");
        assertEquals(mdConfig.queryString(key, "publisher_port"), "65023");
        assertEquals(mdConfig.queryString(key, "client_id"), "5");

        key = "subscribe";
        assertEquals(mdConfig.queryString(key, "exchange_key"), "CtpAll");
        assertEquals(mdConfig.queryString(key, "exchange_code"), "6");

        key = "book_cache";
        assertEquals(mdConfig.queryString(key, "shmName"), "ctp_cache");
        assertEquals(mdConfig.queryString(key, "symbol_data_file"), "/home/kun/Data/ctp/shm/ctp_symbol_data");
        assertEquals(mdConfig.queryInt(key, "num_products"), 600);

        key = "data_channel";
        assertEquals(mdConfig.exist(key, "shm_queue_size"), true); //not exist
        assertEquals(mdConfig.queryInt(key, "shm_queue_size"), 16777216);

        ArrayList<String> keys = new ArrayList<>();
        mdConfig.keys(keys);
        int count = 0;
        for (String myKey : keys) {
            ArrayList<String> myNames = new ArrayList<>();
            mdConfig.properties(myKey, myNames);
            count += myNames.size();
        }
        assertEquals(keys.size(), 4);
        assertEquals(count, 18);

        mdConfig.load("testkey", "boolean_testproperty1", "True");
        mdConfig.load("testkey", "boolean_testproperty2", "1");
        mdConfig.load("testkey", "boolean_testproperty3", "y");
        mdConfig.load("testkey", "boolean_testproperty4", "False");
        mdConfig.load("testkey", "boolean_testproperty5", "0");
        mdConfig.load("testkey", "boolean_testproperty6", "n");

        assertEquals(mdConfig.queryBool("testkey", "boolean_testproperty1"), true);
        assertEquals(mdConfig.queryBool("testkey", "boolean_testproperty2"), true);
        assertEquals(mdConfig.queryBool("testkey", "boolean_testproperty3"), true);
        assertEquals(mdConfig.queryBool("testkey", "boolean_testproperty4"), false);
        assertEquals(mdConfig.queryBool("testkey", "boolean_testproperty5"), false);
        assertEquals(mdConfig.queryBool("testkey", "boolean_testproperty6"), false);
    }
}
