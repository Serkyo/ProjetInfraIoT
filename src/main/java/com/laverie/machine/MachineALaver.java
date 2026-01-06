package com.laverie.machine;

import java.nio.charset.StandardCharsets;

import com.laverie.AppareilIOT;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MachineALaver extends AppareilIOT {
    public String id;

    public MachineALaver() {
        id = System.getenv("ID");
    }

    public static void main(String[] args) {
        MachineALaver machineALaver = new MachineALaver();
        machineALaver.emmeteur();
    }

    private void emmeteur() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        try {
            Connection connection = connectionFactory.newConnection();
            Channel newChannel = connection.createChannel();
            newChannel.exchangeDeclare(EXCHANGE_NAME, "topic");

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Sending one message from machine " + id);
                String routingKey = "laverie.machine." + id + ".status";
                double nombre = Math.random();
                newChannel.basicPublish(EXCHANGE_NAME, routingKey, null, String.valueOf(nombre > 0.5).getBytes(StandardCharsets.UTF_8));
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
