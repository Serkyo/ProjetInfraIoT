package com.laverie.admin;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.laverie.AppareilIOT;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


/**
 *
 * @author arthu
 */
public class TelAdmin extends AppareilIOT {

    private void receiveData() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(AppareilIOT.HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(AppareilIOT.EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, AppareilIOT.EXCHANGE_NAME, "laverie.#");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String routingKey = delivery.getEnvelope().getRoutingKey();
            String message = new String(delivery.getBody(), "UTF-8");

            String[] slicedRoutingKey = routingKey.split("\\.");

            if (slicedRoutingKey[1].equals("machine") && slicedRoutingKey[3].equals("status")) {
                System.out.println(" [x] Received '" + message + "' from machine à laver n°" + slicedRoutingKey[2]);
            }

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    private void toggleMachineALaver(int id, String action) {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        try {
            Connection connection = connectionFactory.newConnection();
            Channel newChannel = connection.createChannel();
            newChannel.exchangeDeclare(EXCHANGE_NAME, "topic");

            System.out.println("Sending one message to machine " + id);
            String routingKey = "laverie.machine." + id + ".toggle";
            newChannel.basicPublish(EXCHANGE_NAME, routingKey, null, action.getBytes(StandardCharsets.UTF_8));
            Thread.sleep(5000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {        
        System.out.println("DEMARRAGE");
        TelAdmin tel = new TelAdmin();
        System.out.println(HOST);
        System.out.println(EXCHANGE_NAME);
        try {
            tel.receiveData();
            tel.toggleMachineALaver(Integer.parseInt(System.getenv("MACHINE1_ID")), "on");
        } catch (Exception e) {
            System.out.println(e);
        }
    }




    // private void getStatus() {
    //     String message = "La machine " + id + "est ";

    //     switch (status) {
    //         case "on":
    //             message += "en cours de lavage.";
    //             break;
            
    //         case "off":
    //             message += "arrêtée.";
    //             break;
        
    //         default:
    //             message += "en pause.";
    //             break;
    //     }
    // }

}
