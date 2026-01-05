package com.laverie;

import io.github.cdimascio.dotenv.Dotenv;

public abstract class AppareilIOT implements Runnable {
    public static String HOST;

    public static void chargementEnv() {
        Dotenv dotenv = Dotenv.load();

        HOST = dotenv.get("HOST");
    }

    public void recuperation() {

    }
}
