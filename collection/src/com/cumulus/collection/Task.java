package com.cumulus.collection;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.Callable;

public class Task implements Callable<Object> {

    private TaskThread task;

    public Object call() {
        task.run();
        return new Object();
    }

    public Task(User user, List<User> queue, Map<String, User> users, WebClient client, Object lock, boolean isLastLev) {
        this.task = new TaskThread(user, queue, users, client, lock, isLastLev);
    }

    private class TaskThread implements Runnable {

        private User user;
        private List<User> queue;
        private Map<String, User> users;
        private WebClient client;   // WebClient is not thread-safe
        private Object lock;
        private boolean isLastLev;

        public TaskThread(User user, List<User> queue, Map<String, User> users, WebClient client, Object lock, boolean isLastLev) {

            this.user = user;
            this.queue = queue;
            this.users = users;
            this.client = client;
            this.lock = lock;
            this.isLastLev = isLastLev;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            try {
                System.out.println("fetching " + user.getName() + "... ");

                // grab user account info
                HtmlPage pageAbout;
                synchronized (client) {
                    pageAbout = client.getPage(user.getTab("about"));
                }
                Document docAbout = Jsoup.parse(pageAbout.getWebResponse().getContentAsString());

                // fetch work and education experiences
                Element elemPageletExp = getPagelet(docAbout, "eduwork");
                if (elemPageletExp!=null) {
                    Elements exps = elemPageletExp.select("div.experienceContent");
                    for (Element exp : exps) {
                        Element expTitle = exp.select("div.experienceTitle").get(0).getElementsByTag("a").get(0);
                        Element expBody = exp.select("div.experienceBody").get(0);
                        ArrayList<String> details = new ArrayList<String>();
                        for (Element elemDetail : expBody.getElementsByTag("span")) {
                            details.add(elemDetail.text());
                        }
                        user.addExperience(expTitle.text(), expTitle.attr("href"), details);
                    }
                }

                // fetch places
                Element elemPageletLoc = getPagelet(docAbout, "hometown");
                Elements elemsCurCity = elemPageletLoc.select("div#current_city");
                Elements elemsHometown = elemPageletLoc.select("div#hometown");
                if (elemsCurCity.size() > 0)
                    user.addInfo("Current City", elemsCurCity.get(0).select("div.fsl.fwb.fcb").get(0).getElementsByTag("a").get(0).text());
                if (elemsHometown.size() > 0)
                    user.addInfo("Hometown", elemsHometown.get(0).select("div.fsl.fwb.fcb").get(0).getElementsByTag("a").get(0).text());

                // fetch basic info
                Element elemPageletBasic = getPagelet(docAbout, "basic");
                if (elemPageletBasic!=null) {
                    Element elemInfoTable = elemPageletBasic.getElementsByTag("table").get(0);
                    Elements elemsInfoKeys = elemInfoTable.getElementsByTag("th");
                    Elements elemsInfoVals = elemInfoTable.getElementsByTag("td");
                    int nInfoPairs = elemsInfoKeys.size();
                    if (nInfoPairs != elemsInfoVals.size()) {
                        System.out.println(user.getId() + ": info keys and vals do not match");
                    }
                    for (int i = 0; i < nInfoPairs; i++) {
                        user.addInfo(elemsInfoKeys.get(i).text(), elemsInfoVals.get(i).select("div>div").get(0).text());
                    }
                }
                System.out.println("save info done: " + user);

                if (!isLastLev) {
                    HtmlPage pageFriends;
                    String friendsURL = user.getTab("friends");
                    synchronized (client) {
                        pageFriends = client.getPage(friendsURL);
/*                      // TODO scroll down to get more loaded
                        client.setAjaxController(new NicelyResynchronizingAjaxController());
                        ScriptResult sr = pageFriends.executeJavaScript("window.scrollBy(0, document.body.scrollHeight);");
                        client.waitForBackgroundJavaScript(3000);
                        pageFriends = (HtmlPage)sr.getNewPage();*/
                    }
                    System.out.println("push friends at " + friendsURL + " done...");

                    // use JSoup to do CSS query instead of XPath query in HTMLUnit
                    Document docFriends = Jsoup.parse(pageFriends.getWebResponse().getContentAsString());
                    // retrieve real segment
                    Element elemPageletFriends = getPagelet(docFriends, "timeline_app_collection");
                    if (elemPageletFriends!=null) {
                        Elements friends = elemPageletFriends.select("div.fsl.fwb.fcb");
                        System.out.println(user.getName() + " has " + friends.size() + " friends fetched!");
                        for (Element friend : friends) {
                            friend = friend.getElementsByTag("a").get(0);
                            User myFriend = new User(friend.text(), friend.attr("href"));
                            synchronized (lock) {
                                if (!users.containsKey(myFriend.getId())) {
                                    queue.add(myFriend);
                                    users.put(myFriend.getId(), myFriend);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private Element getPagelet (Document doc, String tag) {
        String html = doc.html();
        // kind of fuzzy search
        String strLookup = "\"content\":{\"pagelet_" + tag;
        int idxLookup = html.indexOf(strLookup);
        if (idxLookup != -1) {
            strLookup = "\":{\"container_id\":\"";
            idxLookup = html.indexOf(strLookup, idxLookup);
            Element ret = doc.select("code#" +
                    html.substring(idxLookup + strLookup.length(), doc.html().indexOf('\"', idxLookup + strLookup.length()))
            ).get(0);
            ret.html(ret.html().replace("<!--", "").replace("-->", ""));
            return ret;
        }
        return null;
    }

}
