package com.rendering.test.queuingconstumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.impl.AMQBasicProperties;

public class Reciver {

	public static void main(String[] args) {
		String queueName = "Sample";
		Reciver reciver = new Reciver();
	//	reciver.init(true, queueName);
	//	reciver.reciverMessage();
		for(int i = 1 ; i<=20 ; i++){
			reciver.init(true, queueName);
			System.out.println("i :" + i + " " + reciver.GetSingleMessage(queueName));
			try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//reciver.GetSingleMessage(queueName);

	}
	public void init(boolean durable,String queueName){
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			rabbitMQConnection = factory.newConnection();
			rabbitMQChannel = rabbitMQConnection.createChannel();
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("x-max-length", 10);
			rabbitMQChannel.queueDeclare(queueName, durable, false, false, args);
			rabbitMQChannel.basicQos(1);
			rabbitMQConsumer = new QueueingConsumer(rabbitMQChannel);
	//		rabbitMQChannel.basicConsume(queueName, false, rabbitMQConsumer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public void reciverMessage(){
		String message = null;
		QueueingConsumer.Delivery delivery;
		boolean willContinue = true;
		while (willContinue) {
			try {
				delivery = rabbitMQConsumer.nextDelivery();
				message = new String(delivery.getBody());
				System.out.println(" [x] Received '" + message + "'");
				System.out.println("\n\n Enter (Y/N) to continue.....");
				if("N".equalsIgnoreCase(scanner.next())){
					willContinue = false;
				}
				else {
					rabbitMQChannel.basicAck(arg0, arg1);
				}
			} catch (ShutdownSignalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConsumerCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	
	public String GetSingleMessage(String queueName){
		String message = null;
		boolean autoAck = false;
		try {
			GetResponse response = rabbitMQChannel.basicGet(queueName, autoAck);
			if(null!=response){
				AMQBasicProperties properties = response.getProps();
				message = new String(response.getBody());
				//System.out.println("Message : " + message);
				long deliverTag = response.getEnvelope().getDeliveryTag();
				rabbitMQChannel.basicAck(deliverTag, false);
				rabbitMQChannel.close();
				rabbitMQConnection.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
	
	private static Channel rabbitMQChannel = null;
	private static Connection rabbitMQConnection = null;
	private static Properties consumerProperties = null;
	private static QueueingConsumer rabbitMQConsumer = null;
	Scanner scanner = new Scanner(System.in);
}
