package com.victor.design.creational.singleton;

public class SingleObject4 {
    // lazy init
    private static SingleObject4 uniqueInstance;
    private SingleObject4() {
    }

    // double null check
    public static SingleObject4 getInstance() {
        if (uniqueInstance == null) {
            synchronized (SingleObject4.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new SingleObject4();
                }
            }
        }
        return uniqueInstance;
    }

    public void showMessage(){
        System.out.println("Hello World!");
    }
}
