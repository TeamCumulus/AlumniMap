package com.cumulus.collection;

import akka.actor.*;
import akka.japi.Creator;

public class Main {
    public static void main(String[] args) {
        if (args.length!=7) {
            System.out.println("Usage: nWorkers, nResults, szBuffer, nLevels, path, email, password");
            return;
        }
        int nWorkers = Integer.parseInt(args[0]);                   // number of workers spawned by master to perform crawling
        int nResults = Integer.parseInt(args[1]);                   // number of full buffers written out before termination signal sent
        int szBuffer = Integer.parseInt(args[2]);                   // size of buffer each worker holds to cache retrieved users
        int nLevels = Integer.parseInt(args[3]);                    // number of levels worker should never go beyond
        String path = args[4].endsWith("/")?args[6]:args[6]+'/';    // output directory path
        String email = args[5];                                     // seed user email
        String password = args[6];                                  // seed user password
        ActorSystem system = ActorSystem.create("AlumniMapSystem");
        system.actorOf(Props.create(Master.class, nWorkers, nResults, szBuffer, nLevels, path, email, password), "master");
    }
}
