package com.victor.utilities.datastructures;


class KeyValuePair<K extends Comparable<K>, V extends Comparable<V>> implements
        Comparable<KeyValuePair<K, V>> {
    K key;
    V value;

    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    // 此处可定义按照key或value的正序或逆序排序
    @Override
    public int compareTo(KeyValuePair<K, V> o) {
        return -value.compareTo(o.getValue());
    }

    /**
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "[" + key + "," + value + "]";
    }
}
