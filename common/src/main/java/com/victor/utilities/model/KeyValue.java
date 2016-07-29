package com.victor.utilities.model;

import java.util.Map;

/**
 * used for key value pair
 */
public class KeyValue<K extends Comparable<K>, V>
        implements Map.Entry<K, V>, Comparable<KeyValue<K, V>>
{
    private K key;
    private V value;

    public KeyValue(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    public K getKey()
    {
        return this.key;
    }

    public V getValue()
    {
        return this.value;
    }

    public K setKey(K key)
    {
        return this.key = key;
    }

    public V setValue(V value)
    {
        return this.value = value;
    }

    @Override
    public int compareTo(KeyValue<K, V> o) {
        return key.compareTo(o.getKey());
    }
}