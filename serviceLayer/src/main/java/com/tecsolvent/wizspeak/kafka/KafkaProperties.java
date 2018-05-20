package com.tecsolvent.wizspeak.kafka;


public interface KafkaProperties
{
	final static String zkConnect = "127.0.0.1:2181";
	final static  String groupId = "group1";
	final static String topic = "test";
	final static String kafkaServerURL = "localhost";
	final static int kafkaServerPort = 9092;
	final static int kafkaProducerBufferSize = 64*1024;
	final static int connectionTimeOut = 100000;
	final static int reconnectInterval = 10000;
	final static String topic2 = "test";
	final static String topic3 = "topic3";
	final static String clientId = "SimpleConsumerDemoClient";
}
