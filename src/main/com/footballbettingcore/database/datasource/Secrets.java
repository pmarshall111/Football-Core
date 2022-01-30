package com.footballbettingcore.database.datasource;

public class Secrets {
    public final static String DB_URL = System.getenv("DB_URL");
    public final static String DB_NAME = System.getenv("DB_NAME");
    public final static String DB_USER = System.getenv("DB_USER");
    public final static String DB_PASS = System.getenv("DB_PASS");
}
