package com.sample.facebook.simple;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;






@SuppressWarnings("serial")
public class SimpleFacebookSampleServlet extends HttpServlet {
	private static  String APP_ID = "122311657949388";
	private static String APP_URL = "http://emeraldworks-azumio-test-one.appspot.com/simplefacebooksample";
	private static String APP_SECRET = "bdfd5557f74c625bb26a47bc6a47ddbb";

	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new GsonFactory();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		HttpSession session = req.getSession(true);

		resp.setContentType("text/html");

		String code = req.getParameter("code");

		if(code ==null){
			//redirect..
			loginRedirect(resp, session);
			
		}else{
			//has code.. use it to load the list of friends
			String incomingState = req.getParameter("state");
			String state = (String)session.getValue("state");
			resp.getWriter().println("incoming state :" + incomingState +"<br/>");
			resp.getWriter().println("session state :" + state +"<br/>");
			
			//check for NPE ...
			if(state.trim().equals(incomingState.trim())){

				//Top list of friends

				try {
					String accessToken = (String) session.getValue("access_token");
					
					if(accessToken == null){
						accessToken = getAccessToken(code);
						session.putValue("access_token", accessToken);
					}
					
					List<Friend> friends = getTop10Friends(accessToken);
					
					resp.getWriter().println("<p>Top 10 Friends</p>");
					resp.getWriter().println("<ul>");
					for(Friend friend:friends){
						resp.getWriter().println("<li>"+friend.getName()+", count: "+friend.getFriendCount()+"</li>");
						
					}
					resp.getWriter().println("</ul>");
					
				} catch (Exception e) {
					resp.getWriter().println("BOOM");
					
					//TODO: handle error
					e.printStackTrace(resp.getWriter());
				}
			
			}else{
				resp.getWriter().println("Not equal");
				
			}

		}


	}

	public void loginRedirect(HttpServletResponse resp,HttpSession session  ) throws IOException{
	
		String state = UUID.randomUUID().toString();
		session.putValue("state", state);

		String myurl = URLEncoder.encode(APP_URL, "UTF-8");
		String dialogUrl = "https://www.facebook.com/dialog/oauth?client_id=" 
				+ APP_ID + "&redirect_uri=" +myurl+ "&state="
				+ state + "&scope=friends_location,read_friendlists";

		resp.getWriter().println("<script> top.location.href='" +dialogUrl+ "'</script>");
	}

	/**
	 * Make the call to graphapi's fql endpoint for top 10 friends, sorted by friend_count
	 * 
	 * 
	 * @param code
	 * @return
	 * @throws Exception 
	 */
	public List<Friend> getTop10Friends(String accessToken) throws Exception{

		Map<String,String> requestParams = new TreeMap<String, String>();
		
		requestParams.put("q", "SELECT name, friend_count FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) order by friend_count desc limit 0,10");
		requestParams.put("access_token", accessToken);

		String listJson = httpRequest("https",
										"graph.facebook.com",
										"/fql",requestParams);
		
		List<Friend> friends = parseFriendList(listJson);
				
		return friends;
	}

	protected List<Friend> parseFriendList(String rawString){

		JsonParser parser = new JsonParser();
		JsonElement topLevelObj = parser.parse(rawString);

		if(topLevelObj== null){
			//log or throw exception, since a specific format is expected
			return null;
		}

		if(!topLevelObj.isJsonObject()){
			//log or throw exception, since a specific format is expected
			return null;
		}		
		JsonElement  data = topLevelObj.getAsJsonObject().get("data");

		Gson gson = new Gson();
		Type listType = new TypeToken<List<Friend>>(){}.getType();
		List<Friend> friends =  gson.fromJson(data, listType);
		return friends;
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
		
		String response = httpRequest("https",
									"graph.facebook.com",
									"/oauth/access_token",
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


	/**
	 * 
	 * Utility method to call facebook with httpclient
	 * 
	 * @param rawUrl
	 * @return
	 * @throws Exception
	 */
	public String httpRequest(String scheme,String host, String path,Map<String,String> params) throws Exception{

		HttpRequestFactory requestFactory =
				HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest request) {
						request.setParser(new JsonObjectParser(JSON_FACTORY));
					}
				});

		GenericUrl genericUrl = new  GenericUrl();
		genericUrl.setScheme("https");
		genericUrl.setHost(host);		
		genericUrl.setRawPath(path);
		genericUrl.putAll(params);
		
		System.out.println(genericUrl.build());
		HttpRequest request = requestFactory.buildGetRequest(genericUrl);

		HttpResponse response = request.execute();

		if(response.getStatusCode() != 200){
			throw new Exception("something go boom!");
		}

		String body = response.parseAsString();

		return body;
	}

}
