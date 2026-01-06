package com.laverie.client;

import com.laverie.AppareilIOT;

public class Client extends AppareilIOT {

    public Client() {}

    public static void main(String[] args) {
        System.out.println("DEMARRAGE");
        Client client = new Client();
        System.out.println(HOST);
        System.out.println(EXCHANGE_NAME);
        try {
            // tel.receiveData();
            // tel.toggleMachineALaver(Integer.parseInt(System.getenv("MACHINE1_ID")), "on");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
