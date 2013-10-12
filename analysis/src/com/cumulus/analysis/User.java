package com.cumulus.analysis;

import java.util.ArrayList;
public class User {
	
	private String name;
	private String homePage;
	private String facebookID;
	private int gender;
	private int age;
	private int relationship;
	private String religion;
	private String birthday; 
	private int timezone;
	private ArrayList<User> friendlist;
	private Location location;
	private String email;
	private ArrayList<Education> education;
	private ArrayList<Work> work;
	private ArrayList<String> sport;
	private ArrayList<String> language;
	private int friend_count;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHomePage() {
		return homePage;
	}
	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}
	public String getFacebookID() {
		return facebookID;
	}
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getRelationship() {
		return relationship;
	}
	public void setRelationship(int relationship) {
		this.relationship = relationship;
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getTimezone() {
		return timezone;
	}
	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}
	public ArrayList<User> getFriendlist() {
		return friendlist;
	}
	public void setFriendlist(ArrayList<User> friendlist) {
		this.friendlist = friendlist;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public ArrayList<Education> getEducation() {
		return education;
	}
	public void setEducation(ArrayList<Education> education) {
		this.education = education;
	}
	public ArrayList<Work> getWork() {
		return work;
	}
	public void setWork(ArrayList<Work> work) {
		this.work = work;
	}
	public ArrayList<String> getSport() {
		return sport;
	}
	public void setSport(ArrayList<String> sport) {
		this.sport = sport;
	}
	public ArrayList<String> getLanguage() {
		return language;
	}
	public void setLanguage(ArrayList<String> language) {
		this.language = language;
	}
	public int getFriend_count() {
		return friend_count;
	}
	public void setFriend_count(int friend_count) {
		this.friend_count = friend_count;
	}
	       
}

class Location 
{
	private String current_address;
	private String current_location;
	private String hometown_location;
	public String getCurrent_address() {
		return current_address;
	}
	public void setCurrent_address(String current_address) {
		this.current_address = current_address;
	}
	public String getCurrent_location() {
		return current_location;
	}
	public void setCurrent_location(String current_location) {
		this.current_location = current_location;
	}
	public String getHometown_location() {
		return hometown_location;
	}
	public void setHometown_location(String hometown_location) {
		this.hometown_location = hometown_location;
	}   
}

class Education{

	private ArrayList<String> e;

	public ArrayList<String> getE() {
		return e;
	}

	public void setE(ArrayList<String> e) {
		this.e = e;
	}
	
}

class Work{
	
	private String campany;
	private String startime;
	private String endtime;
	public String getCampany() {
		return campany;
	}
	public void setCampany(String campany) {
		this.campany = campany;
	}
	public String getStartime() {
		return startime;
	}
	public void setStartime(String startime) {
		this.startime = startime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}		
	
}

