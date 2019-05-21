package com.yz.framework;

/**
 * @author yazhong.qi
 */
public class NameValue<N, V> {
    private N name;
    private V value;

    public NameValue() {

    }

    public NameValue(N k, V v) {
        this.setName(k);
        this.setValue(v);
    }

    public N getName() {
        return name;
    }

    public void setName(N name) {
        this.name = name;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
