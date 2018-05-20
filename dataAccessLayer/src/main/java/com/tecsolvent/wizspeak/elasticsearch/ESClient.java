package com.tecsolvent.wizspeak.elasticsearch;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.ArrayList;
import java.util.List;

public class ESClient {

	private TransportClient client;
	private static ESClient INSTANCE = null;

	private static final String CLUSTER_NAME = "cluster.name";
	private static final String CLIENT_TRANSPORT_SNIFF = "client.transport.sniff";
	private static final String CLIENT_TRANSPORT_NODES_SAMPLER_INTERVAL = "client.transport.nodes_sampler_interval";
	private static final String CLIENT_TRANSPORT_PING_TIMEOUT = "client.transport.ping_timeout";
	private static final String INDEX_MAPPER_DYNAMIC = "index.mapper.dynamic";
	private static final String ES_INDEX = "tutorial";
	public static final String MAPPING_JSON = "{\n" +
			"  \"mappings\": {\n" +
			"    \"_default_\": {\n" +
			"      \"dynamic_templates\": [\n" +
			"        {\n" +
			"          \"mapping\": {\n" +
			"            \"match\": \"*\",\n" +
			"            \"mapping\": {\n" +
			"              \"index\": \"not_analyzed\"\n" +
			"            }\n" +
			"          }\n" +
			"        }\n" +
			"      ]\n" +
			"    }\n" +
			"  }\n" +
			"}";

	private ESClient(){

		Settings settings = ImmutableSettings.settingsBuilder()
				.put(CLUSTER_NAME, "elasticsearch")
				.put(CLIENT_TRANSPORT_SNIFF, "true")
				.put(CLIENT_TRANSPORT_NODES_SAMPLER_INTERVAL,"60s")
				.put(CLIENT_TRANSPORT_PING_TIMEOUT, "60s")
				.put(INDEX_MAPPER_DYNAMIC, "true")
				.build();

		client = new TransportClient(settings);
		List<String> esClienthosts = new ArrayList<String>();

		esClienthosts.add("192.168.0.12");

		for(String host : esClienthosts){
			client.addTransportAddress(new InetSocketTransportAddress(host, 9200));
		}

		IndicesExistsResponse existsResponse = client.admin().indices().exists(new IndicesExistsRequest()).actionGet();
		if(!existsResponse.isExists()){
			CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(ES_INDEX);
			createIndexRequestBuilder.setSource(MAPPING_JSON);
			createIndexRequestBuilder.execute().actionGet();
		}
		//TODO: some loggers here
	}

	public static ESClient getInstance(){
		if(INSTANCE == null) {
			synchronized(ESClient.class) {
				if(INSTANCE == null) {
					INSTANCE = new ESClient();
				}
			}
		}
		return INSTANCE;
	}

	public void shutdown(){
		client.close();
	}

	public Client getClient() {
		return client;
	}
}
