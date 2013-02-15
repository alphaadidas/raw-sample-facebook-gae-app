package com.sample.facebook.simple;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.sample.facebook.simple.model.Friend;

public class FriendManagerTest {

	private FriendManager manager;
	
	@Before
	public void before(){
		manager = new FriendManager();
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
		
		List<Friend> friends = manager.parseFriendList(sampleList);
		
		Assert.assertNotNull(friends);
		Assert.assertTrue(friends.size()>0);
		Assert.assertEquals(10, friends.size());
		Assert.assertEquals("Soleil Kelley",friends.get(9).getName());
		Assert.assertEquals("1108",friends.get(9).getFriendCount());
		
		
	}
	
	
	@Test
	public void commonCharacterTest(){
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
		
		List<Friend> all = manager.parseFriendList(sampleList);
			
		manager.commonCharacterFriends(all);
	}
	
	
	@Test
	public void friendLocationTest() throws Exception{
		
		String locations = "{\n" +
                "  \"location\": {\n" +
                "    \"id\": \"110843418940484\", \n" +
                "    \"name\": \"Seattle, Washington\"\n" +
                "  }, \n" +
                "  \"id\": \"762205821\", \n" +
                "  \"friends\": {\n" +
                "    \"data\": [\n" +
                "      {\n" +
                "        \"location\": {\n" +
                "          \"id\": \"106078429431815\", \n" +
                "          \"name\": \"London, United Kingdom\"\n" +
                "        }, \n" +
                "        \"name\": \"Max 'Electronic' Van Kleek\", \n" +
                "        \"id\": \"710816\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"location\": {\n" +
                "          \"id\": \"\", \n" +
                "          \"name\": null\n" +
                "        }, \n" +
                "        \"name\": \"Joel Hegg\", \n" +
                "        \"id\": \"1956349\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"location\": {\n" +
                "          \"id\": \"108659242498155\", \n" +
                "          \"name\": \"Chicago, Illinois\"\n" +
                "        }, \n" +
                "        \"name\": \"Amber Salley\", \n" +
                "        \"id\": \"2913055\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"name\": \"Mike Johnson\", \n" +
                "        \"id\": \"3320918\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"name\": \"Lamington Chau\", \n" +
                "        \"id\": \"10700420\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"name\": \"Amanda Patton\", \n" +
                "        \"id\": \"10702577\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"name\": \"Jannel Emery\", \n" +
                "        \"id\": \"10702728\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"location\": {\n" +
                "          \"id\": \"114952118516947\", \n" +
                "          \"name\": \"San Francisco, California\"\n" +
                "        }, \n" +
                "        \"name\": \"Adrien Treuille\", \n" +
                "        \"id\": \"10707321\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"location\": {\n" +
                "          \"id\": \"104022926303756\", \n" +
                "          \"name\": \"Palo Alto, California\"\n" +
                "        }, \n" +
                "        \"name\": \"Andrew Chen\", \n" +
                "        \"id\": \"10708153\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"location\": {\n" +
                "          \"id\": \"114952118516947\", \n" +
                "          \"name\": \"San Francisco, California\"\n" +
                "        }, \n" +
                "        \"name\": \"Josh Potter\", \n" +
                "        \"id\": \"10711940\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"name\": \"Michael Asavareungchai\", \n" +
                "        \"id\": \"10712791\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"location\": {\n" +
                "          \"id\": \"107620239267952\", \n" +
                "          \"name\": \"Edmonds, Washington\"\n" +
                "        }, \n" +
                "        \"name\": \"Tom Music\", \n" +
                "        \"id\": \"10714679\"\n" +
                "      }, \n" +
                "      {\n" +
                "        \"location\": {\n" +
                "          \"id\": \"112527582091753\", \n" +
                "          \"name\": \"Middletown, Rhode Island\"\n" +
                "        }, \n" +
                "        \"name\": \"Sarah Croskey\", \n" +
                "        \"id\": \"10722230\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
		
		List<Friend>  friends = manager.parseGraphApiFriendList(locations,"AAACEdEose0cBAPZBLXCrDrt88NyKpbVplGnaWWVOZCkoTKo8ssERID2I15V1hcZBTXQGZA9LYhJfn5s6Cyz1KgEnexxRBOiOPPlCFY8rZBAZDZD");
		
		Assert.assertNotNull(friends);
		
		
	}
}
