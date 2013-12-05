package com.cumulus.collection;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import au.com.bytecode.opencsv.CSVWriter;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.cumulus.collection.Message.*;

/**
 * Created with IntelliJ IDEA.
 * User: FateAKong
 * Date: 12/3/13
 * Time: 12:00 PM
 */
public class Worker extends UntypedActor {
    private Set<String> visited;
    private List<User> buffer;
    private Seed seed;
    private ActorRef master;
    private final int BUFFER_SIZE;
    private final int MAX_LEV;
    private final String PATH;
    private final WebClient CLIENT;


    public Worker(int szBuffer, int nLevels, String path) {
        visited = new HashSet<String>();
        buffer = new ArrayList<User>();
        CLIENT = new WebClient();
        BUFFER_SIZE = szBuffer;
        MAX_LEV = nLevels;
        PATH = path + this.hashCode();
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof CrawlMsg) {
            CrawlMsg msgCrawl = (CrawlMsg) message;
            if (!visited.contains(msgCrawl.userID)) {
                visited.add(msgCrawl.userID);
                if (buffer.size() == BUFFER_SIZE) {
                    System.out.println(getSelf().toString() + " writing");
                    writeRows();
                    master.tell(new BufferFullMsg(), getSelf());
                }
                User user = new User(msgCrawl.userID, msgCrawl.userHomepage);
                buffer.add(user);

                fetchUserInfo(user);
                if (msgCrawl.iLev < MAX_LEV) {
                    fetchUserFriends(user, msgCrawl.iLev + 1);
                }

            }
        } else if (message instanceof InitMsg) {
            InitMsg msgInit = (InitMsg) message;
            master = getSender();
            seed = new Seed(msgInit.email, msgInit.password);
            seed.login(CLIENT);
            master.tell(new InitDoneMsg(seed.getName(), seed.getHomepage()), getSelf());
        } else if (message instanceof TerminateMsg) {
            System.out.println(getSelf().toString() + " Terminating");
            writeRows();
            seed.logout();
            CLIENT.closeAllWindows();
            master.tell(new TerminateDoneMsg(), getSelf());
        } else {
            unhandled(message);
        }
    }

    private void fetchUserInfo(User user) {
        // grab user account info
        HtmlPage pageAbout = null;
        try {
            pageAbout = CLIENT.getPage(user.getTab("about"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pageAbout != null) {
            Document docAbout = Jsoup.parse(pageAbout.getWebResponse().getContentAsString());
            // fetch work and education experiences
            Element elemPageletExp = getPagelet(docAbout, "eduwork");
            if (elemPageletExp != null) {
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
            if (elemPageletLoc != null) {
                Elements elemsCurCity = elemPageletLoc.select("div#current_city");
                Elements elemsHometown = elemPageletLoc.select("div#hometown");
                if (elemsCurCity.size() > 0)
                    user.addInfo("Current City", elemsCurCity.get(0).select("div.fsl.fwb.fcb").get(0).getElementsByTag("a").get(0).text());
                if (elemsHometown.size() > 0)
                    user.addInfo("Hometown", elemsHometown.get(0).select("div.fsl.fwb.fcb").get(0).getElementsByTag("a").get(0).text());
            }
            // fetch basic info
            Element elemPageletBasic = getPagelet(docAbout, "basic");
            if (elemPageletBasic != null) {
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
            System.out.println("user fetched: " + user);
        }
    }

    private void fetchUserFriends(User user, int iLevFriends) {
        HtmlPage pageFriends = null;
        String urlFriends = user.getTab("friends");
        try {
            pageFriends = CLIENT.getPage(urlFriends);
        } catch (IOException e) {
            e.printStackTrace();
        }
                        /*// TODO scroll down to get more loaded
                        client.setAjaxController(new NicelyResynchronizingAjaxController());
                        ScriptResult sr = pageFriends.executeJavaScript("window.scrollBy(0, document.body.scrollHeight);");
                        client.waitForBackgroundJavaScript(3000);
                        pageFriends = (HtmlPage)sr.getNewPage();*/
//                        System.out.println("push friends at " + urlFriends + " done...");

        if (pageFriends != null) {
            // use JSoup to do CSS query instead of XPath query in HTMLUnit
            Document docFriends = Jsoup.parse(pageFriends.getWebResponse().getContentAsString());
            // retrieve real segment
            Element elemPageletFriends = getPagelet(docFriends, "timeline_app_collection");
            if (elemPageletFriends != null) {
                Elements friends = elemPageletFriends.select("div.fsl.fwb.fcb");
                for (Element friend : friends) {
                    friend = friend.getElementsByTag("a").get(0);
                    master.tell(new CrawlMsg(friend.text(), friend.attr("href"), iLevFriends), getSelf());
                }
                System.out.println(user.getName() + " has " + friends.size() + " friends fetched!");
            }
        }
    }

    private Element getPagelet(Document doc, String tag) {
        String html = doc.html();
        // kind of fuzzy search
        String strLookup = "\"content\":{\"pagelet_" + tag;
        int idxLookup = html.indexOf(strLookup);
        if (idxLookup != -1) {
            strLookup = "\":{\"container_id\":\"";
            idxLookup = html.indexOf(strLookup, idxLookup);
            if (idxLookup != -1) {
                Element ret = doc.select("code#" +
                        html.substring(idxLookup + strLookup.length(), doc.html().indexOf('\"', idxLookup + strLookup.length()))
                ).get(0);
                ret.html(ret.html().replace("<!--", "").replace("-->", ""));
                return ret;
            }
        }
        return null;
    }

    // "ID,Location,Gender,Relation,Age,Degree,UF,Hometown,Timezone,FriendCnt"
    private void writeRows() {
        CSVWriter csvWriter;
        try {
            csvWriter = new CSVWriter(new FileWriter(PATH, true));
            for (User user : buffer) {
                csvWriter.writeNext(user.toCSVRow());
            }
            buffer.clear();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
