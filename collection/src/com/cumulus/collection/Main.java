package com.cumulus.collection;

import akka.actor.*;
import akka.japi.Creator;

public class Main {
    public static void main(String[] args) {
        final int max_level = Integer.parseInt(args[3]);
        final String email = args[0];
        final String password = args[1];
        long time = System.currentTimeMillis();
        ActorSystem system = ActorSystem.create("AlumniMapSystem");
        system.actorOf(Props.create(Master.class, 5, 3, email, password, 100, 5, "output/"), "master");
        time = System.currentTimeMillis() - time;
        System.out.println("crawling " + max_level + " depth uses time " + time + " milliseconds");
    }
}
