package com.laverie.client;

import com.laverie.AppareilIOT;

public class Client extends AppareilIOT {

    public Client() {}

    public static void main(String[] args) {
        Client client = new Client();
        try {
            // tel.receiveData();
            // tel.toggleMachineALaver(Integer.parseInt(System.getenv("MACHINE1_ID")), "on");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
