package com.learning.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetPartitionsListFromConsumer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Logger logger = LoggerFactory.getLogger(GetPartitionsListFromConsumer.class.getName());
		String bootstrapServers = "127.0.0.1:9092";
		String topic = "first_topic";
		String groupId = "mine_fourth_group";
		
		//create consumer config
		Properties p = new Properties();
		p.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		p.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		p.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		p.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		p.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		//create consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(p);
		List<PartitionInfo> partitionList = consumer.partitionsFor(topic);
		
		System.out.println("Number of partitiuons in" + topic + "is :: "+partitionList.size());
		
		
		partitionList.forEach(PartitionInfo -> System.out.println(PartitionInfo));
				
		
	}

}