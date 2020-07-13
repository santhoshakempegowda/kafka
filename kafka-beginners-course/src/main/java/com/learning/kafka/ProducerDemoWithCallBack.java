package com.learning.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoWithCallBack {

	public static void main(String[] args) {
		
		final Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallBack.class);
		// TODO Auto-generated method stub
		String bootStrapServer = "127.0.0.1:9092";
		
		Properties p  = new Properties();
		p.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
		p.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		p.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(p);
		for(int i=0;i<20;i++) {
						
			StringBuilder sb = new StringBuilder();
			sb.append("please send").append(Integer.toString(i)).append("to diff partitions");
			
			ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic", sb.toString());
			
			producer.send(record,new Callback() {
				
				public void onCompletion(RecordMetadata metadata, Exception exception) {
					// TODO Auto-generated method stub
					if(null == exception) {
						logger.info("Recieved metadat : " +"\n" +
									"Topic : " + metadata.topic() +"\n" +
									"Partition : " + metadata.partition() +"\n" +
									"Offset : " + metadata.offset() + "\n" +
									"TimeStamp : " + metadata.timestamp());
					}else {
						logger.error("Error while producing ", exception);
					}
					
				}
			});
				
		}
		
		producer.flush();
		producer.close();
	}

}
