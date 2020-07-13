package com.learning.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoWithKeys {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		final Logger logger = LoggerFactory.getLogger(ProducerDemoWithKeys.class);
		// TODO Auto-generated method stub
		String bootStrapServer = "127.0.0.1:9092";
		
		Properties p  = new Properties();
		p.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
		p.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		p.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(p);
		for(int i=0;i<10;i++) {
						
			String topic = "first_topic";
			StringBuilder sb = new StringBuilder();
			sb.append("please send - ").append(Integer.toString(i)).append(" - to diff partitions");
			String key = "Id_"+i;
			
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, sb.toString());
			logger.info("Key --> "+key);
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
			}).get(); //get() will block the send to make it synchronous -- don't do this in production
				
		}
		
		producer.close();
		// Key --> Id_0 Partition : 2
		// Key --> Id_1 Partition : 1
		// Key --> Id_2 Partition : 2
		// Key --> Id_3 Partition : 0
		// Key --> Id_4 Partition : 1
		// Key --> Id_5 Partition : 2
		// Key --> Id_6 Partition : 0
		// Key --> Id_7 Partition : 2
		// Key --> Id_8 Partition : 1
		// Key --> Id_9 Partition : 0
		
	}

}
