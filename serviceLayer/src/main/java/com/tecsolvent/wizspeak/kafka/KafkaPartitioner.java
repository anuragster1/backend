package com.tecsolvent.wizspeak.kafka;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Created by jaison on 2/4/16.
 */
public class KafkaPartitioner implements Partitioner {

	public void VerifiableProperties(VerifiableProperties props){


	}


	@Override
	public int partition(Object key, int a_numPartitions) {

		int partition = 0;
		String stringKey = (String) key;
		int offset = stringKey.lastIndexOf('.');
		if (offset > 0) {
			partition = Integer.parseInt( stringKey.substring(offset+1)) % a_numPartitions;
		}
		return partition;

	}
}
