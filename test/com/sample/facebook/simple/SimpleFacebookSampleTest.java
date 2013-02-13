package com.sample.facebook.simple;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SimpleFacebookSampleTest {

	private SimpleFacebookSampleServlet servlet;
	
	@Before
	public void before(){
		servlet = new SimpleFacebookSampleServlet();
	}
	
	@Test
	public void test() {
	
		Map<String, String> params =  servlet.parseParams("access_token=USER_ACCESS_TOKEN&expires=NUMBER_OF_SECONDS_UNTIL_TOKEN_EXPIRES");
		
		Assert.assertNotNull(params);
		Assert.assertTrue(params.containsKey("access_token"));
		
		
	}
	
	
	@Test
	public void parseFriendJsonTest(){
		String sampleList = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"name\": \"Romi Dames\", \n" +
                "      \"friend_count\": 4145\n" +
                "    }, \n" +
                "    {\n" +
                "      \"name\": \"Evan Flory-Barnes\", \n" +
                "      \"friend_count\": 3543\n" +
                "    }, \n" +
                "    {\n" +
                "      \"name\": \"Jon Reyes\", \n" +
                "      \"friend_count\": 1597\n" +
                "    }, \n" +
                "    {\n" +
                "      \"name\": \"Taylor Lugviel\", \n" +
                "      \"friend_count\": 1480\n" +
                "    }, \n" +
                "    {\n" +
                "      \"name\": \"Apryl Sakinah McDowell\", \n" +
                "      \"friend_count\": 1474\n" +
                "    }, \n" +
                "    {\n" +
                "      \"name\": \"Max 'Electronic' Van Kleek\", \n" +
                "      \"friend_count\": 1366\n" +
                "    }, \n" +
                "    {\n" +
                "      \"name\": \"Didi Bethurum\", \n" +
                "      \"friend_count\": 1360\n" +
                "    }, \n" +
                "    {\n" +
                "      \"name\": \"Benjamin Bethurum\", \n" +
                "      \"friend_count\": 1219\n" +
                "    }, \n" +
                "    {\n" +
                "      \"name\": \"Hailey Reifel\", \n" +
                "      \"friend_count\": 1167\n" +
                "    }, \n" +
                "    {\n" +
                "      \"name\": \"Soleil Kelley\", \n" +
                "      \"friend_count\": 1108\n" +
                "    }\n" +
                "  ]\n" +
                "}";
		
		List<Friend> friends = servlet.parseFriendList(sampleList);
		
		Assert.assertNotNull(friends);
		Assert.assertTrue(friends.size()>0);
		Assert.assertEquals(10, friends.size());
		Assert.assertEquals("Soleil Kelley",friends.get(9).getName());
		Assert.assertEquals("1108",friends.get(9).getFriendCount());
		
		
	}
	

	@Test
	public void  testRequest() throws Exception{
		Map<String,String> params = new TreeMap<String, String>();
		params.put("q","hello");
		
		
		String response = servlet.httpRequest("https","graph.facebook.com","/me",params);
		
		
//		String response = servlet.getAccessToken("AQA2QuaFLH4E2Hdwxd0CTgbsIPR2GAaNDi2hmAfCftdlJVfH25VGd0uZfgpu6gy4-fbPiXmys86RPxHjEeIIG7j5yjNgH_B9c_cR144Sd0ULwbx1BAn6BKqodEg-1h1spyRDnAbRo3XJFcuCgsz-lupmBCz6kT9J__CCOqYe1jNMaZmT6acG8jAvqxE_mA_VFE2bQtbYrFx01qIRvz2XZIgN#_=_");
//		
//		System.out.println("r:"+response);		
//		
	}
	

}
