package com.zbw.aop.db;

public class DbContexHolder {

    public enum DbType{
        SLAVE,MASTER
    }

    public static final ThreadLocal<DbType> contexHolder = new ThreadLocal<>();

    // set
    public static void setDbType(DbType type){
        if(contexHolder == null){
            throw new NullPointerException();
        }
        contexHolder.set(type);
    }

    // get
    public static DbType getDbType(){
        return contexHolder.get() == null ? DbType.SLAVE : contexHolder.get();
    }

    // clear
    public void clearContexHolder(){
        contexHolder.remove();
    }


}
