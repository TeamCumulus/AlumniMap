package com.cumulus.collection;


public class Main {
    public static void main(String[] args) {
        int max_level = Integer.parseInt(args[3]);
        long time = System.currentTimeMillis();
        new Controller(max_level, new SeedUser(args[0], args[1])).start().writeCSV(args[2]);
        time = System.currentTimeMillis() - time;
        System.out.println("crawling " + max_level + " depth uses time " + time + " milliseconds");
    }
}
