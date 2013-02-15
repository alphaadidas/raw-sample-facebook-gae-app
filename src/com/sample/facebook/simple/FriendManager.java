package com.sample.facebook.simple;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sample.facebook.simple.model.Friend;
import com.sample.facebook.simple.model.Location;
import com.sample.facebook.simple.model.LocationDetail;
import com.sample.facebook.simple.model.NamePairScore;
import com.sample.facebook.simple.model.NameScore;

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

	/**
	 * Calculate the number of characters in common amongst the names.
	 * Returns a list of name-pairs, sorted by the common count (descending)
	 * 
	 * 
	 * Use bitmasking on integers to calculate the overlap between two strings.
	 * (  aba  =>  11000000 ..  , cdb => 0111000000 ... )
	 * 
	 * @param names
	 * @return
	 */
	public List<NamePairScore> commonCharacterFriends(List<Friend> names){

		List<NameScore> nameScores = new ArrayList<NameScore>();
		
		for(Friend name : names){
			NameScore score = new NameScore();
			score.setName(name.getName());
			score.setMask(calculateStringBitmask(name.getName().toLowerCase()));
			nameScores.add(score);
		}
				
		List<NamePairScore> pairs = new ArrayList<NamePairScore>();
		
		for(int i=0 ; i<nameScores.size(); i++){
			
			for(int j=0; j<i; j++){
				if(j==i) {continue;}
				
				NamePairScore pair = new NamePairScore();
				pair.setFirst(nameScores.get(i).getName());
				pair.setSecond(nameScores.get(j).getName());
				pair.setOverlapCount( countMaskOverlap(nameScores.get(i).getMask(),nameScores.get(j).getMask()));
				pairs.add(pair);
				
			}			
		}
		
		//reverse sort
		Collections.sort(pairs,new Comparator<NamePairScore>() {

			@Override
			public int compare(NamePairScore o1, NamePairScore o2) {
				
				if ( o1.getOverlapCount() == o2.getOverlapCount()){
					return 0;
				}else if(o1.getOverlapCount() > o2.getOverlapCount()){
					return -1;
				}else{
					return 1;
				}
			}
		});
		
		return pairs;
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

		//wish subfields for supported in 'location'
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
	
	
	
	
	protected int calculateStringBitmask(String input){
		
		int valueMask = 0;
		for(char ch : input.toCharArray()){
			int idx = maskIdx(ch);
			if(idx >=0 && idx <=25){
				valueMask = valueMask | (1 << idx);
			}
		}
				
		return valueMask;
	}

	protected int maskIdx(char ch){
		return ch - 'a';		
	}
	
	protected int countMaskOverlap(int left, int right){

		int cross = left & right;

		int count = 0;
		while(cross >0)
		{
			count += cross & 1;
			cross >>= 1;
		}

		return count;
	}

}
