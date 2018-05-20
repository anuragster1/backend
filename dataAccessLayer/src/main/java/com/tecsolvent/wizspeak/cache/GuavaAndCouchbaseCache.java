package com.tecsolvent.wizspeak.cache;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.spy.memcached.internal.BulkFuture;
import net.spy.memcached.internal.GetFuture;
import net.spy.memcached.ops.ArrayOperationQueueFactory;
import net.spy.memcached.transcoders.WhalinTranscoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.annotation.ExceptionProxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sunil.kata
 * @since 31/01/16
 */
public class GuavaAndCouchbaseCache {
	private static Log logger = LogFactory.getLog(GuavaAndCouchbaseCache.class);

	private String host;
	private String port;
	private String username;
	private String password;
	private String bucket;
	private long operationTimeout;
	private long queueMaxBlockTimeout;
	private int compressionThreshHold;
	private int queueSize;
	private boolean enableGuavaCache;
	private boolean enableCouchBaseClientLogging = false;
	private int maxClientSize = 19;
	private long couchBaseDefaultExpiry;
	private long guavaCacheDefaultExpiry;
	private Cache<String, Object> cache;
	private static ObjectMapper objectMapper = new ObjectMapper();
	private List<CouchbaseClient> clientList = new ArrayList<CouchbaseClient>();

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void setOperationTimeout(long operationTimeout) {
		this.operationTimeout = operationTimeout;
	}

	public void setQueueMaxBlockTimeout(long queueMaxBlockTimeout) {
		this.queueMaxBlockTimeout = queueMaxBlockTimeout;
	}

	public void setCompressionThreshHold(int compressionThreshHold) {
		this.compressionThreshHold = compressionThreshHold;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public void setEnableGuavaCache(boolean enableGuavaCache) {
		this.enableGuavaCache = enableGuavaCache;
	}

	public void setEnableCouchBaseClientLogging(boolean enableCouchBaseClientLogging) {
		this.enableCouchBaseClientLogging = enableCouchBaseClientLogging;
	}

	public void setCouchBaseDefaultExpiry(long couchBaseDefaultExpiry) {
		this.couchBaseDefaultExpiry = couchBaseDefaultExpiry;
	}

	public void setGuavaCacheDefaultExpiry(long guavaCacheDefaultExpiry) {
		this.guavaCacheDefaultExpiry = guavaCacheDefaultExpiry;
	}

	/**
	 * init function to start cache and set clients
	 * TODO: should throw specific exception
	 */
	public void init() throws Exception{
		List<URI> uris = Lists.newArrayList();
		for (String h : host.split(",")) {
			String uri = h + ":" + port;
			uris.add(URI.create("http://" + uri + "/pools"));
		}
		logger.info("Cache Server URIs " + uris);

		// creating guvava cache for local capability
		cache = CacheBuilder.newBuilder().maximumSize(2000000).expireAfterWrite(guavaCacheDefaultExpiry,
				TimeUnit.SECONDS).build();
		logger.info("Info:: local caching enable and hence created guava default cache.");

		if (enableCouchBaseClientLogging) {
			setUpCouchBaseLogging();
		}

		CouchbaseConnectionFactoryBuilder couchbaseConnectionFactoryBuilder = new CouchbaseConnectionFactoryBuilder();
		couchbaseConnectionFactoryBuilder.setOpTimeout(operationTimeout);
		couchbaseConnectionFactoryBuilder.setOpQueueMaxBlockTime(queueMaxBlockTimeout);
		couchbaseConnectionFactoryBuilder.setOpQueueFactory(new ArrayOperationQueueFactory(queueSize));
		couchbaseConnectionFactoryBuilder.setReadOpQueueFactory(new ArrayOperationQueueFactory(queueSize));
		couchbaseConnectionFactoryBuilder.setWriteOpQueueFactory(new ArrayOperationQueueFactory(queueSize));

		WhalinTranscoder transCoder = new WhalinTranscoder();
		transCoder.setCompressionThreshold(compressionThreshHold);
		couchbaseConnectionFactoryBuilder.setTranscoder(transCoder);

		try {
			CouchbaseConnectionFactory couchbaseConnectionFactory = couchbaseConnectionFactoryBuilder.buildCouchbaseConnection(
					uris, bucket, username, password);

			//initialize couchBase clients
			for (int i = 0; i < maxClientSize; i++) {
				clientList.add(new CouchbaseClient(couchbaseConnectionFactory));
			}
		} catch (IOException e) {
			throw new Exception(e);
		}
	}

	/**
    function to put object as byte (serialization) in cache
    */
	public void putObjectAsByteInCache(String key, Object value) throws Exception {
		putObjectAsByteInCache(key, value, couchBaseDefaultExpiry);
	}

	/*
    function to put object as byte (serialization) in cache with given ttl
    */
	public void putObjectAsByteInCache(String key, Object value, long ttlInSec) throws Exception {
		try {
			byte[] byteValue = objectMapper.writeValueAsBytes(value);
			getClient().set(key, (int) ttlInSec, byteValue);
			putInLocalCache(key, value);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/*
    function to put object map as bytes (serialization) in cache with couchBaseDefaultExpiry
    */
	public void putObjectMapAsByteInCache(Map<String, Object> objs) throws Exception {
		putObjectMapAsByteInCache(objs, couchBaseDefaultExpiry);
	}

	/*
    function to put object map as bytes (serialization) in cache with given ttl
    */
	public void putObjectMapAsByteInCache(Map<String, Object> objs, long ttlInSec) throws Exception {
		for (Map.Entry<String, Object> entry : objs.entrySet()) {
			try {
				byte[] value = objectMapper.writeValueAsBytes(entry.getValue());
				getClient().set(entry.getKey(), (int) ttlInSec, value);
				putInLocalCache(entry.getKey(), entry.getValue());
			} catch (Exception e) {
				throw new Exception(e);
			}
		}
	}

	/*
    function to get deserialize object from cache for a given key
    */
	public Object getObjectFromCache(String key, Class clazz) throws Exception {
		GetFuture<Object> future = null;
		Object result = null;
		try {
			if (enableGuavaCache) {
				Object value = cache.getIfPresent(key);
				if (value != null) {
					return value;
				}
			}

			future = getClient().asyncGet(key);
			Object value = future.get(operationTimeout, TimeUnit.MILLISECONDS);
			if (value != null) {
				result = objectMapper.readValue((byte[]) value, clazz);
				putInLocalCache(key, result);
			}

		} catch (Exception e) {
			if (future != null) {
				future.cancel(false);
			}
			throw new Exception(e);
		}
		return result;
	}

	/*
    function to get map of deserialize object for list of keys
    */
	public Map<String, Object> getObjectMapFromCache(Collection<String> keys, Class clazz) throws Exception {
		List<String> cacheMissKeys = Lists.newArrayList();
		Map<String, Object> cacheResponse = Maps.newLinkedHashMap();

		if (enableGuavaCache) {
			for (String key : keys) {
				Object value = cache.getIfPresent(key);
				if (value != null) {
					cacheResponse.put(key, value);
				} else {
					cacheMissKeys.add(key);
				}
			}
		} else {
			cacheMissKeys.addAll(keys);
		}

		if (!cacheMissKeys.isEmpty()) {
			BulkFuture<Map<String, Object>> bulkFuture = getClient().asyncGetBulk(cacheMissKeys);
			try {
				Map<String, Object> couchBaseResponse = bulkFuture.get(this.operationTimeout, TimeUnit.MILLISECONDS);
				Map<String, Object> deserializeResponse = new HashMap<String, Object>();
				for (String key : couchBaseResponse.keySet()) {
					Object object = objectMapper.readValue((byte[]) couchBaseResponse.get(key), clazz);
					putInLocalCache(key, object);
					deserializeResponse.put(key, object);
				}
				cacheResponse.putAll(deserializeResponse);
			} catch (Exception e) {
				bulkFuture.cancel(false);
				throw new Exception(e);
			}
		}
		return cacheResponse;
	}

	/*
    function to delete objects from cache for given list of keys
    */
	public void removeMultipleObjectFromCache(Collection<String> keys) throws Exception {
		for (String key : keys) {
			try {
				getClient().delete(key);
				if (enableGuavaCache) {
					cache.invalidate(key);
				}
			} catch (Exception e) {
				throw new Exception(e);
			}
		}
	}

	/*
    function to shutdown all cache clients
    */
	public void shutdown() throws Exception {
		try {
			logger.warn("Shutting Down CouchBase Client");
			for (CouchbaseClient client : clientList) {
				client.shutdown();
			}
			cache = null;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/*
    function to set up couchbase logging
    */
	private void setUpCouchBaseLogging() {
		Properties systemProperties = System.getProperties();
		systemProperties.put("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
		System.setProperties(systemProperties);
	}

	/*
    function to put value in guava cache
    */
	private void putInLocalCache(String key, Object value) {
		if (enableGuavaCache) {
			cache.put(key, value);
		}
	}

	/*
    function to select clients randomly
    */
	private CouchbaseClient getClient() {
		return clientList.get((int) Thread.currentThread().getId() % maxClientSize);
	}

	/*
    function to Track the length of the object in cache to detect for Couchbase degradation, Couchbase does not play well with very large objects
    */
	private int getValueLength(Object obj) throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutputStream oStream = new ObjectOutputStream(bStream);

		oStream.writeObject(obj);
		oStream.flush();

		byte[] value = bStream.toByteArray();
		bStream.close();

		return value.length;
	}
}
