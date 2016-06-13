package com.rendering.test.queuingconstumer;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class Sender {

	public static void main(String[] args) {
		String queueName = "Sample";
		Sender sender = new Sender();
		sender.init(true, queueName,10);
		sender.sendMessage(queueName,10);

	}
	
	public void init(boolean durable,String queueName,int QueueSize){
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			rabbitMQConnection = factory.newConnection();
			rabbitMQChannel = rabbitMQConnection.createChannel();
			// durable true means queue wont be lost if rabbitmq restarts
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("x-max-length", QueueSize);
			rabbitMQChannel.queueDeclare(queueName, durable, false, false,
					args);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String queueName,int QueueSize){
		String message = null;
		for(int i = 1 ; i <= 20 ; ){
			message = "Message : " + i;
			//System.out.println("Message : " + i);
			try {
				if(rabbitMQChannel.queueDeclarePassive(queueName).getMessageCount()<QueueSize){
					rabbitMQChannel.basicPublish("", queueName, MessageProperties.TEXT_PLAIN, message.getBytes());
					System.out.println("Message set : " + message);
					i++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			System.out.println(rabbitMQChannel.queueDeclarePassive(queueName).getMessageCount());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				rabbitMQChannel.close();
				rabbitMQConnection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static Channel rabbitMQChannel = null;
	private static Connection rabbitMQConnection = null;
	private static Properties producerProperties = null;
	private static String postURIString = null;
	private static URI postURI = null;
	private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	private static int producerWaitTime;

}
