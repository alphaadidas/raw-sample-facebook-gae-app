package com.sample.facebook.simple;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class FacebookManagerTest {

	private FacebookManager manager;
	
	@Before
	public void before(){
		manager = new FacebookManager();
	}
	
	@Test
	public void test() {
	
		Map<String, String> params =  manager.parseParams("access_token=USER_ACCESS_TOKEN&expires=NUMBER_OF_SECONDS_UNTIL_TOKEN_EXPIRES");
		
		Assert.assertNotNull(params);
		Assert.assertTrue(params.containsKey("access_token"));
		
		
	}
}
