package com.laverie;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class MachineALaver extends AppareilIOT {
    public int numero;

    public MachineALaver(int numero) {
        this.numero = numero;
    }

    @Override
    public void run() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        try {
            Connection connection = connectionFactory.newConnection();
            Channel newChannel = connection.createChannel();
            newChannel.exchangeDeclare(EXCHANGE_NAME, "topic");

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Sending one message from machine #" + numero);
                String routingKey = "laverie.machine." + numero + ".status";
                double nombre = Math.random();
                newChannel.basicPublish(EXCHANGE_NAME, routingKey, null, String.valueOf(nombre > 0.5).getBytes(StandardCharsets.UTF_8));
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
