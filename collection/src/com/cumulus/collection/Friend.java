package com.cumulus.collection;

public class Friend {
	private String name;
    private String homePage;
		
    public void setName(String name) {
		this.name = name;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public Friend(String name, String homePage){
		this.name = name;
    	this.homePage = homePage;
    }
    
    public Friend() {
    	name = null;
    	homePage = null;
    }
		
    public String getName(){
    	return name;
    	}
		
    public String getHomePage(){
	    return homePage;
		}
    
    public int hashCode() {
    	return this.homePage.hashCode();
    }
    
    public boolean equals(Friend f){
    	if (this == f){
    		return true;
    	}
   		if (name.equals(f.getName()) && homePage.equals(f.getHomePage())){
    		return true;
    	}  		
    	return false;
    }
    
}
