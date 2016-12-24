package com.victor.midas.model.db.misc;


/**
 * generic
 */
public class MiscGenericObject <T> extends MiscBase {

    public T object;

    public MiscGenericObject() {
    }

    public MiscGenericObject(String miscName, T o) {
        super(miscName);
        this.object = o;
    }

    @Override
    public String toString() {
        return "MiscGenericObject{" +
                "object=" + object +
                '}';
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
