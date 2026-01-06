package com.laverie.utils;

public enum Cycles {
    COTON(40, 60 * 60, 10, 25, 0.75F, 1F),
    SYNTHETIQUE(30, 45 * 60, 4.0F, 20.0F, 0.0F, 4.0F),
    RAPIDE(30, 15 * 60, 10.0F, 40.0F, 2.0F, 8.0F),
    INTENSIF(60, 90 * 60, 15.0F, 100.0F, 0.5F, 10.0F),
    LAINE(20, 40 * 60, 2.0F, 15.0F, 1.0F, 12.0F);

    private final int temperature;
    private final int temps;
    private final float consoElecMin;
    private final float consoElecMax;
    private final float consoEauMin;
    private final float consoEauMax;

    Cycles(int temperature, int temps, float consoElecMin, float consoElecMax, float consoEauMin, float consoEauMax) {
        this.temperature = temperature;
        this.temps = temps;
        this.consoElecMin = consoElecMin;
        this.consoElecMax = consoElecMax;
        this.consoEauMin = consoEauMin;
        this.consoEauMax = consoEauMax;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getTemps() {
        return temps;
    }

    public float getConsoElecMin() {
        return consoElecMin;
    }

    public float getConsoElecMax() {
        return consoElecMax;
    }

    public float getConsoEauMin() {
        return consoEauMin;
    }

    public float getConsoEauMax() {
        return consoEauMax;
    }
}
