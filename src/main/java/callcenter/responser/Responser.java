package callcenter.responser;

import callcenter.*;

public interface Responser extends Leveled {
    String getId();
    Level getLevel();


    void handle(Call call, CallCenter.ResultCallback resultCallback);
}
