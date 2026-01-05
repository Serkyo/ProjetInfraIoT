package com.laverie;

import io.github.cdimascio.dotenv.Dotenv;

public abstract class AppareilIOT implements Runnable {
    public static String HOST;
    public static String EXCHANGE_NAME;

    public static void chargementEnv(Dotenv instanceDotenv) {
        HOST = instanceDotenv.get("HOST");
        EXCHANGE_NAME = instanceDotenv.get("EXCHANGE_NAME");
    }
}
