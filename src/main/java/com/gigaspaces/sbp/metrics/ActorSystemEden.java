package com.gigaspaces.sbp.metrics;

import akka.actor.ActorSystem;
import akka.dispatch.ExecutionContexts;
import org.springframework.stereotype.Component;
import scala.concurrent.ExecutionContext;

import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 12/11/14
 * Time: 2:32 PM
 */
@Component
public class ActorSystemEden {

//    private static final int THREAD_POOL_SIZE = 6;

//    private final ExecutionContext context
//            = ExecutionContexts.fromExecutorService(Executors.newFixedThreadPool(THREAD_POOL_SIZE));

    //    public ExecutionContext getContext(){
//        return context;
//    }

    private final ActorSystem actorSystem = ActorSystem.create("mySystem");

    public ActorSystem getSystem(){
        return actorSystem;
    }

}
