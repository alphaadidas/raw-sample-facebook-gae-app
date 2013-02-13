package com.sample.facebook.simple;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MyHttpClientTest {

	private MyHttpClient client;
	
	@Before
	public void before(){
		client = new MyHttpClient();		
	}
	
		
	@Ignore
	@Test
	public void  testRequest() throws Exception{
		Map<String,String> params = new TreeMap<String, String>();
		params.put("q","hello");
				
		String response = client.httpRequest("https","graph.facebook.com","/me",params);
		
		
//		String response = servlet.getAccessToken("AQA2QuaFLH4E2Hdwxd0CTgbsIPR2GAaNDi2hmAfCftdlJVfH25VGd0uZfgpu6gy4-fbPiXmys86RPxHjEeIIG7j5yjNgH_B9c_cR144Sd0ULwbx1BAn6BKqodEg-1h1spyRDnAbRo3XJFcuCgsz-lupmBCz6kT9J__CCOqYe1jNMaZmT6acG8jAvqxE_mA_VFE2bQtbYrFx01qIRvz2XZIgN#_=_");
//		
//		System.out.println("r:"+response);		
//		
	}
	
}
