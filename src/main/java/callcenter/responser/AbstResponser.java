package callcenter.responser;

import callcenter.Call;
import callcenter.CallCenter;
import callcenter.Level;
import callcenter.Result;
import callcenter.utils.Utils;

public class AbstResponser implements Responser {
    private String id;
    private Level level;
    public AbstResponser(Level level) {
        this.id = "r"+Utils.genResponserId();
        this.level = level;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void handle(Call call, CallCenter.ResultCallback resultCallback) {
        Result result = handle(call);
        resultCallback.callback(result);
    }


    protected Result handle(Call call) {
        if(call.getIssueLevel().getLevel()> level.getLevel()) {
            call.setLevel(level.escalate());
            return Result.failure(call, this); //can't handle this call level
        }
        if(call.getDuration()>0) {
            try {
                Thread.sleep(call.getDuration()); // take a while
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        call.setLevel(this.getLevel()); // decide call level, if repsonser can handle this call
        return Result.success(call, this);
    }
}
