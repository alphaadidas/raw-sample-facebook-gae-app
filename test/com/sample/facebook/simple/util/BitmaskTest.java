package com.sample.facebook.simple.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.sample.facebook.simple.FriendManager;
import com.sample.facebook.simple.model.Friend;
import com.sample.facebook.simple.model.NamePairScore;
import com.sample.facebook.simple.model.NameScore;

public class BitmaskTest {

	@Test
	public void test() {


		int first_second_third_and_fourth = (1 << 0) |(1 << 1) | (1 << 2) | (1 << 3);
		int first = (1 << 0);
		int first_and_fourth = (1 << 0) | (1 << 3);


		System.out.println(first_second_third_and_fourth);

		System.out.println(first);

		int overlap = countMaskOverlap(first_second_third_and_fourth, first_and_fourth);
		System.out.println(overlap);
		
		Assert.assertEquals(2, overlap);
		
	}


	private int countMaskOverlap(int left, int right){

		int cross = left & right;

		int count = 0;
		while(cross >0)
		{
			count += cross & 1;
			cross >>= 1;
		}

		return count;
	}

	@Test
	public void calculateCharacterBitmask(){

		String value = "glenz";
		
		System.out.println(calcMask(value));
	}
	
	private int calcMask(String value){
		
		int valueMask = 0;
		
		for(char ch : value.toCharArray()){
			valueMask = valueMask | (1 << maskIdx(ch)); 
		}
		return valueMask;
	}
	private int maskIdx(char ch){
		return ch - 'a';
	}
	
	
	@Test
	public void testCommon(){
		
		List<Friend> names = new ArrayList<Friend>();
		
		names.add(new Friend("bob"));
		names.add(new Friend("robert"));
		names.add(new Friend("john"));
		names.add(new Friend("andy"));
		names.add(new Friend("frank"));
		
		
		FriendManager fManager = new FriendManager();
		
		List<NamePairScore> pairs = fManager.commonCharacterFriends(names);
		
		for(NamePairScore pair: pairs){
			System.out.println("L:"+pair.getFirst() + " | R:"+pair.getSecond() + " = "+ pair.getOverlapCount());
		}
		
	}
	
	
}
