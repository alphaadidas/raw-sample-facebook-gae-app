package com.sample.facebook.simple;

import java.util.Map;

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

public class MyHttpClient {

	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new GsonFactory();

	
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
