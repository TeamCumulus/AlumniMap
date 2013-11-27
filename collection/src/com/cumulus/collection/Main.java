package com.cumulus.collection;


public class Main {

/*    public void writeNodes() {
        PrintWriter outputStream = null;
        try {
            outputStream = new PrintWriter(new FileWriter(nodeFile));
            outputStream.println("Name,ID");
            for (User f : friendsSet) {
                outputStream.println(f.getName() + "," + f.getHomepage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public void writeEdges() {
        PrintWriter outputStream1 = null;
        try {
            outputStream1 = new PrintWriter(new FileWriter(edgeFile));
            outputStream1.println("Source;Target");
            String Name1 = null;
            String Name2 = null;
            for (int i = 0; i < friendsGraph.size(); i++) {
                Name1 = friendsGraph.get(i).get(0).getName();
                for (int j = 1; j < friendsGraph.get(i).size(); j++) {
                    Name2 = friendsGraph.get(i).get(j).getName();
                    outputStream1.println(Name1.replace(' ', '_') + ";" + Name2.replace(' ', '_'));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream1 != null) {
                outputStream1.close();
            }
        }
    }*/

    public static void main(String[] args) {
        SeedUser seed = new SeedUser("gx900117@gmail.com", "gx163300");
        int maxLevel = 3;
        String nodeFile = "fbNode.csv";
        String edgeFile = "fbEdge.csv";
        long startTime = System.currentTimeMillis();
        new Controller(maxLevel, seed).start();
        long usedTime = System.currentTimeMillis() - startTime;
//        main.writeResultToFile();
        System.out.println("crawling " + maxLevel + " depth uses time " + usedTime + " milliseconds");
    }

}
