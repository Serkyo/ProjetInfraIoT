package com.laverie.client;

import java.nio.charset.StandardCharsets;

import com.laverie.AppareilIOT;
import com.laverie.admin.TelAdmin;
import com.laverie.utils.DatabaseManager;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Client extends AppareilIOT {

    public Client() {}

    public static void main(String[] args) {
        Client client = new Client();
        try {
            // client.receiveData();
            client.toggleMachineALaver(client, System.getenv("MACHINE3_ID"), "on");
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    // Gestion de la machine à laver
    private void toggleMachineALaver(Client self, String id, String action) {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        try {
            Connection connection = connectionFactory.newConnection();
            Channel newChannel = connection.createChannel();
            newChannel.exchangeDeclare(EXCHANGE_NAME, "topic");

            System.out.println("Sending one message to machine " + id);
            String routingKey = "laverie.machine." + id + ".toggle";
            newChannel.basicPublish(EXCHANGE_NAME, routingKey, null, action.getBytes(StandardCharsets.UTF_8));
            DatabaseManager.getLogMachines();
            System.out.println("DÉMARRAGE DE LA MACHINE N°" + id);
            self.receiveData(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // Réception des logs des machines cibles
    private void receiveData(String id) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(AppareilIOT.HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(AppareilIOT.EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, AppareilIOT.EXCHANGE_NAME, "laverie.machine." + id + ".fini");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback;
        deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            try {
                Thread.sleep(2000);
                // DatabaseManager.getLogMachines();
                System.out.println("Votre machine n°" + id + " est terminée !");
            } catch (InterruptedException ex) {
                System.getLogger(TelAdmin.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

}
