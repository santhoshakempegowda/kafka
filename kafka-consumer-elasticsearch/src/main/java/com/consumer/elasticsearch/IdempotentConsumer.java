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
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;

public class IdempotentConsumer {
	
	
	static Logger logger = LoggerFactory.getLogger(IdempotentConsumer.class.getName());
	

	public static void main(String[] args) throws IOException {
		
		RestHighLevelClient client = createClient();
		
		//create Kafka Consumer
		KafkaConsumer<String, String> consumer = createKafkaConsumer("twitter-tweets");
		
		
		
		while(true) {
			ConsumerRecords<String, String> records = 	consumer.poll(Duration.ofMillis(100));
			
			for(ConsumerRecord<String, String> record: records){
				
				//craete a unique ID to make consumer Idempotent
				// 2 stratagies ==> 1.kafka generic id 2.application specific id
				
				//kafka generic id
				//String id = record.topic() + "_" + record.partition() + "_" + record.offset();
				
				//twitter specific id
				String id = extractIdFromTweet(record.value());
				
				String jsonString = record.value();
				IndexRequest indexRequest = new IndexRequest("twitter").
											id(id). //this is to make our consumer idempotent
											source(jsonString, XContentType.JSON);
				
				IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
				 id = indexResponse.getId();
				logger.info("Id created automatically : "+id);
				
				/*
				 * 	[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384905710374914
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384906989690880
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384907199414274
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384907727908866
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384907832754177
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384908679827457
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384910034767873
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384910227496960
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384910747631617
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384911330807812
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384912265932800
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384912119160832
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384912811335680
					[main] INFO com.consumer.elasticsearch.IdempotentConsumer - Id created automatically : 1278384913104932864
				 */
				
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
	
	private static void createIndex(RestHighLevelClient client) throws IOException {
		CreateIndexRequest request = new CreateIndexRequest("twitter");
		request.settings(Settings.builder() 
			    .put("index.number_of_shards", 3)
			    .put("index.number_of_replicas", 2)
			);
		
		//Elasticsearch built-in helpers to generate JSON content
		XContentBuilder builder = XContentFactory.jsonBuilder();
		builder.startObject();
		{
		    builder.startObject("properties");
		    {
		        builder.startObject("message");
		        {
		            builder.field("type", "text");
		        }
		        builder.endObject();
		    }
		    builder.endObject();
		}
		builder.endObject();
		request.mapping(builder);
		
		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
		
		//Indicates whether all of the nodes have acknowledged the request
		boolean acknowledged = createIndexResponse.isAcknowledged(); 
		//Indicates whether the requisite number of shard copies were started for each shard in the index before timing ou
		boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
		
		logger.info("acknowledged : " +acknowledged);
		logger.info("shardsAcknowledged : " + shardsAcknowledged);
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
	private static JsonParser jsonParser = new JsonParser();
	private static String extractIdFromTweet(String tweet) {
		return jsonParser.parse(tweet)
		.getAsJsonObject()
		.get("id_str")
		.getAsString();
	}

}
