package com.cumulus.collection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CrawlingTask implements Runnable {
	
	private Friend friend;
	private LinkedList<Friend> insertQueue;
	private Set<Friend> friendsSet;
	private WebClient webClient; //WebClient is not thread-safe
	private ArrayList<ArrayList<Friend>> friendsGraph;
	private Object lock;
	
	public CrawlingTask(Friend friend, LinkedList<Friend> queue, Set<Friend> set, WebClient webClient, ArrayList<ArrayList<Friend>> friendsGraph, Object lock){
		
		this.friend = friend;
		this.insertQueue = queue;
		this.friendsSet = set;
		this.webClient = webClient;
		this.friendsGraph = friendsGraph;
		this.lock = lock;
	}
	
	@SuppressWarnings("finally")
	public HtmlPage getPageofFriends(HtmlPage homePage){
		
		ArrayList<HtmlAnchor> anchors =new ArrayList<HtmlAnchor>(homePage.getAnchors());
		String friendsPageURL = null;
		for (HtmlAnchor anchor: anchors){
			if ((anchor.getAttribute("class").equals("seeAll") || anchor.getAttribute("class").equals("link rfloat"))&& anchor.getHrefAttribute().contains("friends")){
				friendsPageURL = anchor.getHrefAttribute();
				break;
			}		
		}
		
		if (friendsPageURL == null){
			System.out.println("friendsPageURL null");
			return null; 
		}
		
		HtmlPage pageofFriends = null;
		
		    // use webclient as lock makes the crawl sequential!!!
			try {
				synchronized(webClient){
					pageofFriends = webClient.getPage(friendsPageURL);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally{
				return pageofFriends;
			}
	}

	@SuppressWarnings("unchecked")
	public void run(){
		try {
			
			ArrayList<Friend> myFriendsList = new ArrayList<Friend>();
			myFriendsList.add(friend);
			System.out.println("fetching " + friend.getName() + "...");
			HtmlPage homePage = null;
			synchronized(webClient){
				homePage = webClient.getPage(friend.getHomePage());
			}
			HtmlPage pageofFriends = getPageofFriends(homePage);
			
			if (pageofFriends == null){
				int qstmark = friend.getHomePage().lastIndexOf('?');
				String friendsURL;
				if(qstmark!=-1)
					friendsURL = friend.getHomePage().substring(0, qstmark) + "/friends";
				else friendsURL = friend.getHomePage();
				synchronized(webClient){
					pageofFriends = webClient.getPage(friendsURL);
				}
			}
			
			ArrayList<DomNode> list = new ArrayList<DomNode>();
			list = (ArrayList<DomNode>)pageofFriends.getByXPath("//div[@class='fsl fwb fcb']");
			HtmlAnchor friendAnchor = null;
			Friend myFriend = null;
			System.out.println( friend.getName() + " has " + list.size() + " friends fetched!");
			
			for (DomNode d : list){
				//System.out.println(d.asXml());
				friendAnchor = (HtmlAnchor) d.getByXPath("./a").get(0);
				myFriend = new Friend(friendAnchor.asText(), friendAnchor.getHrefAttribute());
				myFriendsList.add(myFriend);
				
				synchronized(lock){
				if (!friendsSet.contains(myFriend)){
					insertQueue.add(myFriend);
					friendsSet.add(myFriend);
				}
				}

			}
			synchronized(friendsGraph){
				friendsGraph.add(myFriendsList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
