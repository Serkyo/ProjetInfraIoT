package com.laverie;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.UUID;

public abstract class AppareilIOT {
    public static String HOST;
    public static String EXCHANGE_NAME;
    public UUID uuid;

    public AppareilIOT() {
        uuid = UUID.randomUUID();
        AppareilIOT.chargementEnv();
    }

    public static void chargementEnv() {
        HOST = System.getenv("HOST");
        EXCHANGE_NAME = System.getenv("EXCHANGE_NAME");
    }
}
