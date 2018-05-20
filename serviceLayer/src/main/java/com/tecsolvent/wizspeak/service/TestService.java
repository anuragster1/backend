package com.tecsolvent.wizspeak.service;

import com.tecsolvent.wizspeak.TestDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import net.sf.json.JSONObject;

/**
 * @author sunil.kata
 * @since 15/01/16
 */
public class TestService {
	private TestDao testDao;
	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}

	public String testCacheCall() throws Exception{
		guavaAndCouchbaseCache.putObjectAsByteInCache("testKey", "cached value");
		return guavaAndCouchbaseCache.getObjectFromCache("testKey", String.class).toString();
	}

	public void testMysqlCall() throws Exception {
		testDao.testMysqlCall();
	}

	public void setTestDao(TestDao testDao) {
		this.testDao = testDao;
	}

	public JSONObject testGetMysqlCall() throws Exception {
		return testDao.testGetMysqlCall();
	}
}
