package callcenter;

import callcenter.responser.Responser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class CallCenter {
    private static Logger logger = LoggerFactory.getLogger(CallCenter.class);


    private CallQueue callQueue;
    private ResponserPool responserPool;

    public void setPool(ResponserPool pool) {
        this.responserPool = pool;
    }
    public void setCallQueue(CallQueue callQueue) {
        this.callQueue = callQueue;
    }

    public void receive(Call call) {
        callQueue.add(call);
        logger.debug("Receive call {}", call.getId());
    }
    public int getCallQueueSize() {return callQueue.size();}
    public void register(Responser responser) {
        responserPool.register(responser);
    }

    private final ResultCallback resultCallback = new ResultCallback();

    private Router router;

    protected void route(Call call, Responser responser) {
        handle(call, responser, resultCallback);
    }

    public class ResultCallback {
        public void callback(Result result) {
            responserPool.register(result.getResponser()); //add responser back to pool
            if(result.isSuccess()) {
                onCallFinished(result);
            } else {
                if(result.getResponserLevel().isMax()) {
                    onCallFailed(result);
                }
                callQueue.addFirst(result.getCall()); //add call back to queue head
            }
        }
    }

    protected abstract void handle(Call call, Responser responser, ResultCallback resultCallback);

    protected abstract void onCallFinished(Result result);

    protected abstract void onCallFailed(Result result);


    // find the way(responser) for calls
    // if using more than one router, need to change the way to match call and resposnser
    class Router implements Runnable {

        @Override
        public void run() {
            //way 1 : end-less loop
            while (true) {
                Level[] levels = Level.values();
                for(int i=levels.length-1; i>=0; i--) { //TODO: match call and responser
                    Level callLevel = levels[i];

                    boolean hasCall = callQueue.hasNext(callLevel);
                    Optional<Level> responserLevel = hasCall? findResponserLevelByCallLevel(callLevel) : Optional.empty();
                    if(!hasCall || !responserLevel.isPresent()) {
                        continue;
                    }
                    do {
                        Call call = callQueue.poll(callLevel);
                        Responser responser = responserPool.poll(responserLevel.get());
                        route(call, responser);

                        //next call
                        hasCall = callQueue.hasNext(callLevel);
                        responserLevel = hasCall? findResponserLevelByCallLevel(callLevel) : Optional.empty();
                    } while (hasCall && responserLevel.isPresent());


                }

            }


            //way 2 : A emitter for a level-queue,
            // this thread will pause if there is no responser available
            // and wake up if there is responser available


        }


        private Optional<Level> findResponserLevelByCallLevel(Level lv) {
            boolean found = responserPool.hasFree(lv);
            if(found) return Optional.of(lv);
            do {
                lv = lv.escalate();
                found = responserPool.hasFree(lv);
            } while (!found && lv.getLevel()<Level.max().getLevel());
            if(found) {
                return Optional.of(lv);
            } else {
                return Optional.empty();
            }
        }

    }

    public void start() {
        router = new Router();
        new Thread(router).start();
    }




}
