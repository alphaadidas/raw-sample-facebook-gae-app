package com.sample.facebook.simple;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FacebookManager {

	private static  String APP_ID = "122311657949388";
	private static String APP_URL = "http://emeraldworks-azumio-test-one.appspot.com/simplefacebooksample";
	private static String APP_SECRET = "bdfd5557f74c625bb26a47bc6a47ddbb";

	private MyHttpClient httpClient = new MyHttpClient();
	
	public void loginRedirect(HttpServletResponse resp,HttpSession session  ) throws IOException{
		
		String state = UUID.randomUUID().toString();
		session.putValue("state", state);

		String myurl = URLEncoder.encode(APP_URL, "UTF-8");
		String dialogUrl = "https://www.facebook.com/dialog/oauth?client_id=" 
				+ APP_ID + "&redirect_uri=" +myurl+ "&state="
				+ state + "&scope=user_location,friends_location,read_friendlists";

		resp.getWriter().println("<script> top.location.href='" +dialogUrl+ "'</script>");
	}

	

	
	/**
	 * Call graphapi to exchange a code for an access token
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public String getAccessToken(String code ) throws Exception{

		
		Map<String,String> requestParams = new TreeMap<String, String>();
		requestParams.put("client_id", APP_ID);
		requestParams.put("redirect_uri",APP_URL);
		requestParams.put("client_secret", APP_SECRET);
		requestParams.put("code", code);
		
		String response = httpClient.httpRequest("https",
									"graph.facebook.com",
									"/oauth/access_t oken",
									requestParams);

		//Should look like this:
		// access_token=USER_ACCESS_TOKEN&expires=NUMBER_OF_SECONDS_UNTIL_TOKEN_EXPIRES
		Map<String,String> params = parseParams(response);

		if(!params.containsKey("access_token")){
			throw new Exception("Can't get access_token");
		}

		return params.get("access_token");
	}


	/**
	 * Convert a string to a map of keyvalue pairs
	 * @param raw
	 * @return
	 */
	protected Map<String,String> parseParams(String raw){

		Map<String,String> params = new TreeMap<String,String>();

		String[] pairs = raw.split("&");

		if(pairs !=null && pairs.length >0 ){
			for(String pair :pairs){
				String[] keyVal = pair.split("=");
				if(keyVal!=null && keyVal.length ==2){
					params.put(keyVal[0], keyVal[1]);
				}
			}			
		}


		return params;
	}

}
