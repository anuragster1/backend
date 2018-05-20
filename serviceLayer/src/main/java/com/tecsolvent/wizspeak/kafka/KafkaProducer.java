package com.tecsolvent.wizspeak.kafka;

import kafka.producer.KeyedMessage;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.util.Properties;

import org.apache.log4j.Logger;
import scala.collection.Seq;



/**
 * Created by jaison on 2/4/16.
 */
public class KafkaProducer {

	public static Logger logger = Logger.getLogger(KafkaProducer.class);

	public static void main(String [] args) {
		Properties prop = new Properties();
		prop.put("metadata.broker.list", "localhost:9092");
		prop.put("serializer.class","kafka.serializer.StringEncoder");
		//prop.put("partitioner.class", "example.producer.SimplePartitioner");

		logger.info("inside kafka producer");
		ProducerConfig producerConfig = new ProducerConfig(prop);
		Producer<String,String> producer = new <String,String>Producer(producerConfig);
		String topic = "test";
		KeyedMessage<String,String> message = new <String,String>KeyedMessage(topic, "Hello Test message");
		try {
			producer.send(message);
		}catch (Exception e){

			logger.info("error in sending message "+e);
		}
		producer.close();
	}
}
