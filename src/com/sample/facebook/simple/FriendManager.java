package com.sample.facebook.simple;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class FriendManager {

	private MyHttpClient httpClient = new MyHttpClient();

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

		String listJson = httpClient.httpRequest("https",
				"graph.facebook.com",
				"/fql",requestParams);

		List<Friend> friends = parseFriendList(listJson);

		return friends;
	}

	public List<Friend> commonCharacterFriends(List<Friend> all){

		//iterate over list .. keep tally of global character count, and friend name count.

		Map<Character,CharacterCount> characterCount  = new TreeMap<Character, CharacterCount>();

		for(Friend friend : all){
			for(int i=0; i< friend.getName().length(); i++){				
				String name = friend.getName().toLowerCase();				
				Character ch = name.charAt(i);

				if(characterCount.containsKey(ch)){
					CharacterCount cc = characterCount.get(ch);
					cc.incrementCount();
				}else{
					characterCount.put(ch, new CharacterCount(ch));
				}				
			}			
		}

		List<CharacterCount> chars = new ArrayList<CharacterCount>(characterCount.values());
		Collections.sort(chars, new Comparator<CharacterCount>() {
			@Override
			public int compare(CharacterCount o1, CharacterCount o2) {				
				return o1.getCount().compareTo(o2.getCount());
			}
		});


		return null;
	}

	public List<Friend> getAllFriends(String accessToken) throws Exception {

		Map<String,String> requestParams = new TreeMap<String, String>();

		requestParams.put("q", "SELECT name FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) ");
		requestParams.put("access_token", accessToken);

		String listJson = httpClient.httpRequest("https",
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
	
	protected List<Friend> getAllFriendLocation(String accessToken) throws Exception{
		
		Map<String,String> requestParams = new TreeMap<String, String>();

		requestParams.put("fields", "location,friends.fields(location,name)");
		requestParams.put("access_token", accessToken);

		String listJson = httpClient.httpRequest("https",
				"graph.facebook.com",
				"/me",requestParams);
		
		List<Friend> friends = parseGraphApiFriendList(listJson,accessToken);

		return friends;
	}
	
	protected List<Friend> getToFriendsByDistance(List<Friend> friends, final int closeOrFar) throws Exception{

		Collections.sort(friends, new Comparator<Friend>() {

			@Override
			public int compare(Friend o1, Friend o2) {				
				return o1.getDistanceFromMe().compareTo(o2.getDistanceFromMe()) * closeOrFar;
			}
		});
		
		if(friends.size()>=10){
			return friends.subList(0, 10);
		}else{
			return friends;
		}
	}

	/**
	 * Parse the graphApi's json response
	 * And populate all the 'friend' objects with lat/lon and distance, can sort later.
	 * granted this will be slow for alot of friends.  but who has millions of friends?
	 * 
	 * @param raw
	 * @return
	 * @throws Exception 
	 */
	protected List<Friend> parseGraphApiFriendList(String raw,String accessToken) throws Exception{

		//if location exist.. look it up. and update friend
		JsonParser parser = new JsonParser();
		JsonElement topLevelObj = parser.parse(raw);

		if(topLevelObj== null){
			//log or throw exception, since a specific format is expected
			return null;
		}

		if(!topLevelObj.isJsonObject()){
			//log or throw exception, since a specific format is expected
			return null;
		}		

		
		if(!topLevelObj.getAsJsonObject().has("location")){
			return null;
		}
		JsonObject myLocation = topLevelObj.getAsJsonObject().get("location").getAsJsonObject();
		
		List<Friend> locatedFriends = new ArrayList<Friend>();

		Location home = getLocation(myLocation.get("id").getAsString(),accessToken);

		//NPE, if there are no friends.. hmm
		JsonObject friendsObj = topLevelObj.getAsJsonObject().get("friends").getAsJsonObject();
		JsonArray friends = friendsObj.get("data").getAsJsonArray();

		if(friends ==null || friends.size() ==1){
			return null;
		}

		for(int i=0 ; i < friends.size(); i++){
			//create friend, and fetch data.
			JsonObject friendElement = friends.get(i).getAsJsonObject();
			Friend friend = new Friend();

			if(friendElement.has("name")){
				friend.setName(friendElement.get("name").getAsString());				
			}
			if(friendElement.has("location")){

				JsonObject loc = friendElement.get("location").getAsJsonObject();
				if(loc.has("id")){
					String locId = loc.get("id").getAsString();
					if(locId!=null && locId.trim().length()>0){
						Location fLoc = getLocation(locId, accessToken);
						friend.setLocation(fLoc);
						friend.setDistanceFromMe(calculateDistanceBetween(fLoc, home));						
						locatedFriends.add(friend);
					}
				}
				
			}
			
		}

		return locatedFriends;
	}


	protected Location getLocation(String id, String accessToken) throws Exception{

		Map<String,String> requestParams = new TreeMap<String, String>();
		requestParams.put("access_token", accessToken);

		String locationJson = httpClient.httpRequest("https",
				"graph.facebook.com",
				"/"+id,requestParams);
		
		
		Gson gson = new Gson();
		LocationDetail detail = gson.fromJson(locationJson, LocationDetail.class);
		
		if(detail == null){
			return null;
		}
		
		Location location = detail.getLocation();
		
		return location;
	}

	/**
	 * calculate the distance from here to there.
	 * 
	 * 
	 * @param there
	 * @param here
	 */
	protected Double calculateDistanceBetween(Location there, Location here){

		if(there == null || here == null){
			return Double.MAX_VALUE;
		}
		float dist = distFrom(here.getLatitude().floatValue(), here.getLongitude().floatValue(), there.getLatitude().floatValue(), there.getLongitude().floatValue());
		Double distance = new Double(dist);		
		return distance;
	}

	public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
		
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return new Float(dist * meterConversion).floatValue();
	}
	
	
	

}
