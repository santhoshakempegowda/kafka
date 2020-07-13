package com.learning.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("Hello world !!");
		String bootstrspServers = "127.0.0.1:9092";
		
		//create producer properties
		Properties p = new Properties();
		p.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrspServers);
		p.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		p.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		/* DEFAULT PRODUCER CONFIG VALUES
		[main] INFO org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values: 
			acks = 1
			batch.size = 16384
			bootstrap.servers = [127.0.0.1:9092]
			buffer.memory = 33554432
			client.dns.lookup = default
			client.id = producer-1
			compression.type = none
			connections.max.idle.ms = 540000
			delivery.timeout.ms = 120000
			enable.idempotence = false
			interceptor.classes = []
			key.serializer = class org.apache.kafka.common.serialization.StringSerializer
			linger.ms = 0
			max.block.ms = 60000
			max.in.flight.requests.per.connection = 5
			max.request.size = 1048576
			metadata.max.age.ms = 300000
			metadata.max.idle.ms = 300000
			metric.reporters = []
			metrics.num.samples = 2
			metrics.recording.level = INFO
			metrics.sample.window.ms = 30000
			partitioner.class = class org.apache.kafka.clients.producer.internals.DefaultPartitioner
			receive.buffer.bytes = 32768
			reconnect.backoff.max.ms = 1000
			reconnect.backoff.ms = 50
			request.timeout.ms = 30000
			retries = 2147483647
			retry.backoff.ms = 100
			sasl.client.callback.handler.class = null
			sasl.jaas.config = null
			sasl.kerberos.kinit.cmd = /usr/bin/kinit
			sasl.kerberos.min.time.before.relogin = 60000
			sasl.kerberos.service.name = null
			sasl.kerberos.ticket.renew.jitter = 0.05
			sasl.kerberos.ticket.renew.window.factor = 0.8
			sasl.login.callback.handler.class = null
			sasl.login.class = null
			sasl.login.refresh.buffer.seconds = 300
			sasl.login.refresh.min.period.seconds = 60
			sasl.login.refresh.window.factor = 0.8
			sasl.login.refresh.window.jitter = 0.05
			sasl.mechanism = GSSAPI
			security.protocol = PLAINTEXT
			security.providers = null
			send.buffer.bytes = 131072
			ssl.cipher.suites = null
			ssl.enabled.protocols = [TLSv1.2]
			ssl.endpoint.identification.algorithm = https
			ssl.key.password = null
			ssl.keymanager.algorithm = SunX509
			ssl.keystore.location = null
			ssl.keystore.password = null
			ssl.keystore.type = JKS
			ssl.protocol = TLSv1.2
			ssl.provider = null
			ssl.secure.random.implementation = null
			ssl.trustmanager.algorithm = PKIX
			ssl.truststore.location = null
			ssl.truststore.password = null
			ssl.truststore.type = JKS
			transaction.timeout.ms = 60000
			transactional.id = null
			value.serializer = class org.apache.kafka.common.serialization.StringSerializer
			**/
		//create safe producer
		p.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		//below three properties will be set by default if we don't set them
		p.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		p.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(Integer.MAX_VALUE));
		p.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
		
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
