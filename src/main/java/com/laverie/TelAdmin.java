package com.laverie;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


/**
 *
 * @author arthu
 */
public class TelAdmin extends AppareilIOT {

    public TelAdmin() {}

    public void receiveData() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(AppareilIOT.HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(AppareilIOT.EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, AppareilIOT.EXCHANGE_NAME, "laverie.#");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }

    public void emitAction() {
        
    }


    @Override
    public void run() {        
        System.out.println("Hello world !");
        try {
            receiveData();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
