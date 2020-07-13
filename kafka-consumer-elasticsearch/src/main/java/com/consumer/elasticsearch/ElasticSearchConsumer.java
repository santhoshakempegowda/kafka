package com.consumer.elasticsearch;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchConsumer {
	
	
	static Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class.getName());
	

	public static void main(String[] args) throws IOException {
		
		RestHighLevelClient client = createClient();
		
		//create Kafka Consumer
		KafkaConsumer<String, String> consumer = createKafkaConsumer("twitter-tweets");
		
		
		
		while(true) {
			ConsumerRecords<String, String> records = 	consumer.poll(Duration.ofMillis(100));
			
			for(ConsumerRecord<String, String> record: records){
				
				String jsonString = record.value();
				IndexRequest indexRequest = new IndexRequest("twitter").source(jsonString, XContentType.JSON);
				
				
				IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
				String id = indexResponse.getId();
				logger.info("Id created automatically : "+id);
				
				
				
			}
		}
		
		//client.close();
		

	}
	
	private static RestHighLevelClient createClient() {
		
		String hostname = "kafka-course-poc-8470538609.ap-southeast-2.bonsaisearch.net";
		String username = "c766s9dc6d";
		String password = "i4z6malnnd";
		
		final CredentialsProvider credentialsProvider = new  BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		
		RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, 443, "https"))
										.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
												public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
												return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
											}
										});
		
		RestHighLevelClient client = new RestHighLevelClient(builder);
		return client;
	}
	
	public static KafkaConsumer<String, String> createKafkaConsumer(String topic){
		String bootstrapServers = "127.0.0.1:9092";
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
		
		//subscribe consumer to topic(s)
		consumer.subscribe(Arrays.asList(topic));
		
		return consumer;
		
	}

}