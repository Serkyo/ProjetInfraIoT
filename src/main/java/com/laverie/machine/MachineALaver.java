package com.laverie.machine;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import com.laverie.AppareilIOT;
import com.laverie.utils.Cycles;
import com.laverie.utils.DatabaseManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MachineALaver extends AppareilIOT {
    public String id;
    public String status;
    public int tempsRestant;
    public Cycles cycle;
    public boolean essorage;
    public Date dateDebut;
    public float consoElecTotale;
    public float consoEauTotale;

    public MachineALaver() {
        super();
        id = System.getenv("ID");
        reset();
        // ↓↓↓ Test ↓↓↓
//        try {
//            onOff("on");
//        } catch (Exception e) {
//            System.out.println("YA PROBLEME");
//        }
    }

    public static void main(String[] args) {
        MachineALaver machineALaver = new MachineALaver();
        machineALaver.receive();
        machineALaver.emmeteur();
    }

    private void onOff(String message) throws Exception {
        switch (message) {
            case "on":
                if (status != "pause") {
                    Cycles[] tousLesCycles = Cycles.values();
                    int randomIndex = ThreadLocalRandom.current().nextInt(tousLesCycles.length);
                    lancerMachine(tousLesCycles[randomIndex], Math.random() > 0.5);
                }
                else {
                    status = "on";
                }
                break;
            
            case "off":
                reset();
                break;
        
            default:
                status = "pause";
                break;
        }
        DatabaseManager.insererLogMachine(message, null, id);
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

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String routingKey = delivery.getEnvelope().getRoutingKey();
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                if (routingKey.equals("laverie.machine." + id + ".toggle")) {
                    try {
                        onOff(message);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
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
                String routingKey = "laverie.machine." + id;
                passageSeconde(newChannel, routingKey);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void passageSeconde(Channel channel, String routingKey) throws Exception {
        if (tempsRestant >= 0 && status.equals("on")) {
            tempsRestant -= 1;
            consoElecTotale += getConsoElec();
            consoEauTotale += getConsoEau();
            if (tempsRestant < 0) {
                DatabaseManager.insererHistoriqueMachine(id , cycle, cycle.getTemps(), consoElecTotale, consoEauTotale, dateDebut, new Date());
                onOff("off");
            }
            int tempsTotal = cycle.getTemps() + (essorage ? 20 : 0);
            int progression = ((tempsTotal - tempsRestant) / tempsTotal) * 100;
            channel.basicPublish(EXCHANGE_NAME, routingKey + ".status", null, String.valueOf(progression).getBytes(StandardCharsets.UTF_8));
        }
    }

    public void lancerMachine(Cycles cycle, boolean essorage) throws Exception {
        if (status == "on") {
            throw new Exception("Machine déjà lancée");
        }

        status = "on";
        this.cycle = cycle;
        this.dateDebut = new Date();
        tempsRestant = cycle.getTemps();

        if (essorage) {
            this.essorage = true;
            tempsRestant += 20;
        }
    }

    public float getConsoElec() {
        float min = cycle.getConsoElecMin() / 60;
        float max = cycle.getConsoElecMax() / 60;
        if (essorage && tempsRestant <= 20) {
            min += 2;
            max += 2;
        }
        return ThreadLocalRandom.current().nextFloat(cycle.getConsoElecMin(), cycle.getConsoElecMax());
    }

    public float getConsoEau() {
        if (essorage && tempsRestant <= 20) {
            return 0;
        }
        return ThreadLocalRandom.current().nextFloat(cycle.getConsoEauMin(), cycle.getConsoEauMax()) / 60;
    }

    private void reset() {
        status = "off";
        tempsRestant = 0;
        cycle = null;
        essorage = false;
        dateDebut = null;
        consoElecTotale = 0;
        consoEauTotale = 0;
    }
}
