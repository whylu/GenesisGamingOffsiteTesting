package example;

import callcenter.*;
import callcenter.responser.Responser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyCallCenter extends CallCenter {

    private static Logger logger = LoggerFactory.getLogger(MyCallCenter.class);
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public MyCallCenter() {
        setCallQueue(new InMemCallQueue());
        setPool(new InMemResponserPool());
    }

    @Override
    protected void handle(Call call, Responser responser, CallCenter.ResultCallback resultCallback) {
        logger.info("Handle Call id: {}, issueLv:{} by Responser id: {}, lv: {}", call.getId(), call.getIssueLevel(), responser.getId(), responser.getLevel());
        executorService.execute(() -> {
            responser.handle(call, resultCallback);
        });
    }

    @Override
    protected void onCallFinished(Result result) {
        logger.info("Call finished: call: {}, level:{}, last responser: {}, level: {}",
                result.getCall().getId(), result.getCall().getLevel(),
                result.getResponser().getId(), result.getResponser().getLevel());
    }

    @Override
    protected void onCallFailed(Result result) {
        logger.error("Call failed: call level: {}, last responser level: {}", result.getCallLevel(), result.getResponserLevel());
    }

}
