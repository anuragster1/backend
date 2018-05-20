package com.tecsolvent.wizspeak.controller;

import javax.annotation.Resource;

import com.tecsolvent.wizspeak.service.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author sunil.kata
 * @since 15/01/16
 */
@Controller
public class TestController {
	@Resource(name = "testService")
	TestService testService;

	// API to test service call
	@RequestMapping(value = "/testCacheCall", method = RequestMethod.GET)
	public
	@ResponseBody
	String testCacheCall() throws Exception {
		return testService.testCacheCall();
	}

	// API to test service call
	@RequestMapping(value = "/testMysqlCall", method = RequestMethod.GET)
	public
	@ResponseBody
	void testMysqlCall() throws Exception {
		testService.testMysqlCall();
	}

	// API to test service call
	@RequestMapping(value = "/testGetMysqlCall", method = RequestMethod.GET)
	public
	@ResponseBody
	String testGetMysqlCall() throws Exception {

		String json = testService.testGetMysqlCall().toString();



		return json;
	}

}
