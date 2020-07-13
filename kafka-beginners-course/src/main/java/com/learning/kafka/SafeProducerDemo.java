package com.learning.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class SafeProducerDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("Hello world !!");
		String bootstrspServers = "127.0.0.1:9092";
		
		//create producer properties
		Properties p = new Properties();
		p.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrspServers);
		p.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		p.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//create a producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(p);
		
		//create a producerRecored
		ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic", "hello world again!!");
		
		//send data -- Asynchronous process
		producer.send(record);
		
		//flush data
		producer.flush();
		
		//flush and close producer
		producer.close();
		

	}

}
