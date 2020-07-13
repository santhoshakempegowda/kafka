package com.learning.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoWithAssignSeek {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Logger logger = LoggerFactory.getLogger(ConsumerDemoWithAssignSeek.class.getName());
		String bootstrapServers = "127.0.0.1:9092";
		String topic = "first_topic";
		//String groupId = "mine_fourth_group";
		
		//create consumer config
		Properties p = new Properties();
		p.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		p.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		p.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	//	p.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		p.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		//create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(p);
		
		//assign and seek are mostly used to replay data or fetch a specific message without using a group id.
		//Topic name and partition number is used to fetch messages
		
		
		//assign --> //assign is used instead of group Id or group name we use partition number of the topic to read from
		TopicPartition partitionToReadFrom = new TopicPartition(topic, 0);
		consumer.assign(Arrays.asList(partitionToReadFrom));
		
		//subscribe consumer to topic(s)
		//consumer.subscribe(Arrays.asList(topic));
		
		//seek --> used instead of subscribe to entire topic
		long offsetToReadFrom = 20L;
		consumer.seek(partitionToReadFrom, offsetToReadFrom);
		
		boolean keepreading = true;
		int numberOfMessagesToRead = 10;
		int numberOfMessagesReadSoFar = 0;
		
		//poll for new data
		while(keepreading) {
			ConsumerRecords<String, String> records = 	consumer.poll(Duration.ofMillis(100));
			
			for(ConsumerRecord<String, String> record: records){
				numberOfMessagesReadSoFar +=1;
				logger.info("Key : " + record.key() + " value : " + record.value());
				logger.info("Partition : " + record.partition()  + " offset : " + record.offset());
				if(numberOfMessagesReadSoFar == numberOfMessagesToRead) {
					keepreading = false;
					break;
				}
			}
		}
		
		
		
		
	}

}
