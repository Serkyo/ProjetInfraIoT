package com.laverie;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        AppareilIOT.chargementEnv(dotenv);

        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(dotenv.get("NOMBRE_D_APPAREILS")));

        executorService.execute(new MachineALaver(1));

        executorService.shutdown();
    }
}
