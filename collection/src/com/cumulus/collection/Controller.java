package com.cumulus.collection;

import au.com.bytecode.opencsv.CSVWriter;
import com.gargoylesoftware.htmlunit.WebClient;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;

public class Controller {
    private List<User> cur_lev;
    private List<User> next_lev;
    private Map<String, User> visited;
    private ExecutorService pool;
    final private WebClient client;
    private SeedUser seed;
    private Object lock;
    private int idx_lev;

    public Controller(int maxLevel, SeedUser seed) {
        visited = new HashMap<String, User>();
        idx_lev = maxLevel;
        pool = Executors.newCachedThreadPool();
        client = seed.getWebClient();
        this.seed = seed;
        lock = new Object();
        cur_lev = new LinkedList<User>();
        next_lev = new LinkedList<User>();
        seed.login();
    }

    public Controller start() {
        cur_lev.add(seed);
        visited.put(seed.getId(), seed);
        List<Task> tasks = new ArrayList<Task>();
        while (--idx_lev>=0) {
            for (User f : cur_lev) {
                tasks.add(new Task(f, next_lev, visited, client, lock, idx_lev==0));
            }
            try {
                if (tasks.size() > 0) {
                    pool.invokeAll(tasks);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tasks.clear();
            cur_lev = next_lev;
            next_lev = new LinkedList<User>();
        }

        if (seed.isLogin()) {
            seed.logout();
        }

        return this;
    }

    public void writeCSV(String path) {
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(path));
            csvWriter.writeNext("ID,Location,Gender,Relation,Degree,UF".split(","));
            for (User user: visited.values()) {
                csvWriter.writeNext(user.toCSVRow());
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
