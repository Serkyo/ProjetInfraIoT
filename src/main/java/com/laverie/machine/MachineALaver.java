package com.laverie.machine;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import com.laverie.AppareilIOT;
import com.laverie.utils.Cycles;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MachineALaver extends AppareilIOT {
    public String id;
    public String status;
    public double tempsRestant;
    public Cycles cycle;
    public boolean essorage;

    public MachineALaver() {
        super();
        id = System.getenv("ID");
        status = "off";
        tempsRestant = 0;
        cycle = null;
        essorage = false;
    }

    public static void main(String[] args) {
        MachineALaver machineALaver = new MachineALaver();
        System.out.println(HOST);
        System.out.println(EXCHANGE_NAME);
        machineALaver.receive();
        machineALaver.emmeteur();
    }

    private void onOff(String message) {
        switch (message) {
            case "on":
                status = "on";
                break;
            
            case "off":
                status = "off";
                break;
        
            default:
                status = "pause";
                break;
        }
    }

    private void receive() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(AppareilIOT.HOST);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
    
            channel.exchangeDeclare(AppareilIOT.EXCHANGE_NAME, "topic");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, AppareilIOT.EXCHANGE_NAME, "laverie.machine." + id + ".#");
    
            // System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
    
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String routingKey = delivery.getEnvelope().getRoutingKey();
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                // Exemple de vérification
                if (routingKey.equals("laverie.machine." + id + ".toggle")) {
                    onOff(message);
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
                newChannel.basicPublish(EXCHANGE_NAME, routingKey, null, status.getBytes(StandardCharsets.UTF_8));
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void passageSeconde() {
        tempsRestant -= 1;
        if (tempsRestant == 0) {
            status = "off";
        }
    }

    private void lancerMachine(Cycles cycle, boolean essorage) throws Exception {
        if (status != "off") {
            throw new Exception("Machine déjà lancée");
        }

        status = "on";
        tempsRestant = cycle.getTemps();

        if (essorage) {
            tempsRestant += 20 * 60;
        }
        this.cycle = cycle;
    }

    private float getConsoElec() {
        float min = cycle.getConsoElecMin();
        float max = cycle.getConsoElecMax();
        if (essorage && tempsRestant <= 20) {
            min += 2;
            max += 2;
        }
        return ThreadLocalRandom.current().nextFloat(cycle.getConsoElecMin(), cycle.getConsoElecMax());
    }

    private float getConsoEau() {
        if (essorage && tempsRestant <= 20) {
            return 0;
        }
        return ThreadLocalRandom.current().nextFloat(cycle.getConsoEauMin(), cycle.getConsoEauMax());
    }
}
