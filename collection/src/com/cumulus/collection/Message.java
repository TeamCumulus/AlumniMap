package com.cumulus.collection;

/**
 * Created with IntelliJ IDEA.
 * User: FateAKong
 * Date: 12/3/13
 * Time: 10:24 PM
 */
public class Message {
    public static class CrawlMsg {
        public final String userID;
        public final String userHomepage;
        public final int iLev;

        public CrawlMsg(String userID, String userHomepage, int iLev) {
            this.userID = userID;
            this.userHomepage = userHomepage;
            this.iLev = iLev;
        }
    }

    public static class InitMsg {
        public final String email;
        public final String password;

        public InitMsg(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class InitDoneMsg {
        public final String userID;
        public final String userHomepage;

        public InitDoneMsg(String userID, String userHomepage) {
            this.userID = userID;
            this.userHomepage = userHomepage;
        }
    }

    public static class TerminateMsg {
    }

    public static class TerminateDoneMsg {
    }

    public static class BufferFullMsg {
    }
}
