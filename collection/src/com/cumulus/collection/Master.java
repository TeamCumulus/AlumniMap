package com.cumulus.collection;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.Broadcast;
import akka.routing.ConsistentHashingRouter;

import static com.cumulus.collection.Message.*;

/**
 * Created with IntelliJ IDEA.
 * User: FateAKong
 * Date: 12/3/13
 * Time: 9:53 PM
 */
public class Master extends UntypedActor {
    private int nReach = 0;
    private int nInitDone = 0;
    private int nTermDone = 0;
    private final int NUM_WORKERS;
    private final int NUM_RESULTS;  // in unit of buffer size
    private final ActorRef ROUTER;

    public Master(int nWorkers, int nResults, int szBuffer, int nLevels, String path, String email, String password) {
        NUM_WORKERS = nWorkers;
        NUM_RESULTS = nResults;
        ROUTER = getContext().actorOf(Props.create(Worker.class, szBuffer, nLevels, path)
                .withRouter(new ConsistentHashingRouter(NUM_WORKERS).withHashMapper(new ConsistentHashingRouter.ConsistentHashMapper() {
                    @Override
                    public Object hashKey(Object message) {
                        return ((CrawlMsg) message).userID;
                    }
                })), "router");
        self().tell(new InitMsg(email, password), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof CrawlMsg) {
            ROUTER.tell(message, getSender());
        } else if (message instanceof InitMsg) {
            ROUTER.tell(new Broadcast(message), getSelf());
        } else if (message instanceof InitDoneMsg) {
            if (++nInitDone == NUM_WORKERS) {
                InitDoneMsg msgInitDone = (InitDoneMsg) message;
                ROUTER.tell(new CrawlMsg(msgInitDone.userID, msgInitDone.userHomepage, 0), getSelf());
            }
        } else if (message instanceof TerminateDoneMsg) {
            System.out.println(getSender().toString()+" Terminate Done");
            if (++nTermDone == NUM_WORKERS) {
                getContext().system().shutdown();
            }
        } else if (message instanceof BufferFullMsg) {
            if (++nReach == NUM_RESULTS) {
                System.out.println("Reach!!!");
//                getContext().system().shutdown();
                ROUTER.tell(new Broadcast(new TerminateMsg()), getSelf());
            }
        }
    }
}
