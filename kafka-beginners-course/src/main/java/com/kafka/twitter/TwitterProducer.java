package com.kafka.twitter;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterProducer {
	
	Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());
	
	BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
	private String consumerKey = "kcoe554z83rqe6dQ3WNeNuMPS";
	private String consumerSecret = "cfGTEwy04hgNUolRgFMUZH3CG2p32aBwuZIcjA4eY4cjD4HS9j";
	private String token = "2456466366-B4Dhpy4kemiMBfCPppijI87RQvE4XBEaRoEQ3JS" ;
	private String secret = "2wWja2suZdtqYleqxUMwTSVcZ6dbvZfBvxblEBsGTfpWx";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hello");
		
		new TwitterProducer().run();
		

	}

	public void run() {
		
		logger.info("set up !!");
		//create a twitter client
		Client client = createTwitterClient();
		
		//attempt to establish the connection
		client.connect();
		
		//create a kafka producer
		KafkaProducer<String, String> producer = createKafkaProducer();
		
		//loop to send tweets to kafka
		while (!client.isDone()) {
			String msg = null;
			  try {
				   msg = msgQueue.poll(5L, TimeUnit.SECONDS);
			  } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				client.stop();
			  }
			  if(null!= msg) {
				  producer.send(new ProducerRecord<String, String>("twitter-tweets", null, msg), new Callback() {
					
					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if(null == exception) {
							
						}else {
							logger.info("something went wrong !!!!");
							exception.printStackTrace();
						}
						
					}
				});
				  logger.info(msg);
			  }
		}
		
		logger.info("application complete !!!");
	}
	
	public Client createTwitterClient() {
		/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		
		// Optional: set up some followings and track terms
		List<String> terms = Lists.newArrayList("covid");
		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
		
		ClientBuilder builder = new ClientBuilder()
				  .name("Hosebird-Client-01")                              // optional: mainly for the logs
				  .hosts(hosebirdHosts)
				  .authentication(hosebirdAuth)
				  .endpoint(hosebirdEndpoint)
				  .processor(new StringDelimitedProcessor(msgQueue));
				                          

				Client hosebirdClient = builder.build();
				return hosebirdClient;
	}
	
	public KafkaProducer<String, String> createKafkaProducer(){
		String bootstrspServers = "127.0.0.1:9092";
		Properties p = new Properties();
		p.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrspServers);
		p.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		p.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//create a producer
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(p);
		return producer;
	}
}
