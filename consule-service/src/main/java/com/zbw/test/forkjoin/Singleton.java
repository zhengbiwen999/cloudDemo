package com.zbw.test.forkjoin;


//1、finan类型 禁止被重写
public final class Singleton {
    //构造方法是私有的，避免外界通过构造方法创建实例。
    private Singleton() {
    }

    //对外暴露获取实例的唯一方法
    public static Singleton getInstance() {
        return Holder.SINGLETON;
    }

    //延迟加载
    private static class Holder {
        private static final Singleton SINGLETON = new Singleton();
    }
}
