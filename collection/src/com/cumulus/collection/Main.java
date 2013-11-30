package com.cumulus.collection;


public class Main {
    public static void main(String[] args) {
        int maxLevel = 3;
        long time = System.currentTimeMillis();
        new Controller(maxLevel, new SeedUser(args[0], args[1])).start().writeCSV(args[2]);
        time = System.currentTimeMillis() - time;
        System.out.println("crawling " + maxLevel + " depth uses time " + time + " milliseconds");
    }

}
