package com.sample.facebook.simple;

public class CharacterCount {

	
	private Character character;
	private Integer count;
	
	
	public CharacterCount(Character c){
		character = c;
		count = 1;
	}
	
	public Character getCharacter() {
		return character;
	}
	public void setCharacter(Character character) {
		this.character = character;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	public void incrementCount(){
		this.count++;
	}
}
