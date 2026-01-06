package com.laverie.machine;

import java.nio.charset.StandardCharsets;

import com.laverie.AppareilIOT;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MachineALaver extends AppareilIOT {
    public String id;

    public static void main(String[] args) {
        System.out.println("DEMARRAGE");
        MachineALaver machineALaver = new MachineALaver();
        System.out.println(HOST);
        System.out.println(EXCHANGE_NAME);
        machineALaver.receive();
        machineALaver.emmeteur();
    }

    private void receive() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(AppareilIOT.HOST);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
    
            channel.exchangeDeclare(AppareilIOT.EXCHANGE_NAME, "topic");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, AppareilIOT.EXCHANGE_NAME, "laverie.machine." + id +"");
    
            // System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
    
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String routingKey = delivery.getEnvelope().getRoutingKey();
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                // Exemple de vérification
                if (routingKey.equals("laverie.machine." + id + ".toggle")) {
                    System.out.println(" [x] Message accepté");
                    System.out.println("     RoutingKey : " + routingKey);
                    System.out.println("     Message    : " + message);

                    // traitement du message ici
                } else {
                    System.out.println(" [!] Message ignoré (routingKey = " + routingKey + ")");
                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
