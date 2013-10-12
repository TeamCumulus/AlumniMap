package com.cumulus.collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class FBCrawlMain {

	/**
	 * @param args
	 */
	private String nodeFile;
	private String edgeFile;
	private Set<Friend> friendsSet;
	private ArrayList<ArrayList<Friend>> friendsGraph;
	
	private CrawlingController crawler;
	
	public FBCrawlMain(int maxLevel, Facebook seed, String nodeFile, String edgeFile) {
		this.nodeFile = nodeFile;
		this.edgeFile = edgeFile;
		this.friendsSet = new HashSet<Friend>();
		this.friendsGraph = new ArrayList<ArrayList<Friend>>();
		this.crawler = new CrawlingController(maxLevel, seed, friendsSet, friendsGraph);
	}
	
	public void crawl() {
		this.crawler.crawlingFriends();
	}
	
	public void writeResultToFile() {
		this.writeNodes();
		this.writeEdges();
	}
	
	public void writeNodes(){
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileWriter(nodeFile));
			outputStream.println("Id;Label");
			for (Friend f : friendsSet){
				outputStream.println(f.getName() + ";" + f.getHomePage());
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (outputStream != null){
				outputStream.close();
			}
		}
	}
	
	public void writeEdges(){
		PrintWriter outputStream1 = null;
		try {
			outputStream1 = new PrintWriter(new FileWriter(edgeFile));
			outputStream1.println("Source;Target");
			String Name1 = null;
			String Name2 = null;
			for (int i = 0; i < friendsGraph.size(); i++){
				Name1 = friendsGraph.get(i).get(0).getName();
				for (int j = 1; j < friendsGraph.get(i).size(); j++){
					Name2 = friendsGraph.get(i).get(j).getName();
					outputStream1.println(Name1.replace(' ', '_') + ";" + Name2.replace(' ', '_'));
					}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (outputStream1 != null){
				outputStream1.close();
			}
		}
		
	}
	public static void main(String[] args) {
		Facebook fb = new Facebook();
		int maxLevel = 3;
		String nodeFile = "fbNode.csv";
		String edgeFile = "fbEdge.csv";
		FBCrawlMain fbCrawlMain = new FBCrawlMain(maxLevel, fb, nodeFile, edgeFile);
		long startTime = System.currentTimeMillis();
		fbCrawlMain.crawl();
		long usedTime = System.currentTimeMillis() - startTime;
		fbCrawlMain.writeResultToFile();
		System.out.println("crawling " + maxLevel + " depth uses time " + usedTime + " millseconds");
	}

}
