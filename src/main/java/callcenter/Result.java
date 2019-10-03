package callcenter;

import callcenter.responser.Responser;

public class Result {
    private boolean success;
    private Call call;
    private Responser responser;



    public Result(boolean success, Call call, Responser responser) {
        this.success = success;
        this.call = call;
        this.responser = responser;
    }

    public boolean isSuccess() {
        return success;
    }

    public Level getCallLevel() {
        return call.getLevel();
    }

    public Level getResponserLevel() {
        return responser.getLevel();
    }

    public static Result failure(Call call, Responser responser) {
        return new Result(false, call, responser);
    }

    public static Result success(Call call, Responser responser) {
        return new Result(true, call, responser);
    }

    public Call getCall() {
        return call;
    }

    public Responser getResponser() {
        return responser;
    }
}
