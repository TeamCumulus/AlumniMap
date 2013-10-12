package com.cumulus.collection;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

public class CrawlingController {
	private LinkedList<Friend> taskQueue, friendsQueue1;
	private LinkedList<Friend> insertQueue, friendsQueue2;
	private int maxLevel;
	private Set<Friend> friendsSet;
	private ExecutorService threadPool;
	final private WebClient webClient;
	private ArrayList<ArrayList<Friend>> friendsGraph;
	private Facebook seedFriend;
	private Object lock;
	private int currentQueue;
	private int level;
	
	public CrawlingController(int maxLevel, Facebook seed, Set<Friend> friendSet,  
			ArrayList<ArrayList<Friend>> friendsGraph) {
		friendsQueue1 = new LinkedList<Friend>();
		friendsQueue2 = new LinkedList<Friend>();
		this.currentQueue = 0;
		this.level = 0;
		this.maxLevel = maxLevel;
		friendsSet = new HashSet<Friend>();
		threadPool = Executors.newCachedThreadPool();
		this.webClient = seed.getWebClient();
		friendsGraph = new ArrayList<ArrayList<Friend>>();
		this.seedFriend = seed;
		lock = new Object();
		seed.login();
	}
	
	public void crawlingFriends(){
		
//		CrawlingTask task1 = new CrawlingTask(seedFriend, friendsQueue1, friendsSet, webClient, friendsGraph, lock);
//		task1.run();
		System.out.println("crawling...");
		friendsQueue1.add(seedFriend);
		friendsSet.add(seedFriend);
		//showCollection(friendsQueue1);
		currentQueue = 0; 
		level = 1;
		ArrayList<Task> taskSet = new ArrayList<Task>(); 
		
		while (level <= maxLevel){
			taskSet.clear();
			if (currentQueue == 0){
				taskQueue = friendsQueue1;
				insertQueue = friendsQueue2;
			}else{
				taskQueue = friendsQueue2;
				insertQueue = friendsQueue1;
			}
			insertQueue.clear();
			for (Friend f : taskQueue){
				taskSet.add(new Task(new CrawlingTask(f, insertQueue, friendsSet, webClient, friendsGraph, lock)));
			}
			
			try {
				if(taskSet.size()>0)
					threadPool.invokeAll((Collection<Task>) taskSet);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			currentQueue = 1 -currentQueue;
			level++;
		}
		
		if(!seedFriend.isLogin()) {
			seedFriend.logout();
		}
	}
}
