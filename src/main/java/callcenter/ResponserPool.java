package callcenter;

import callcenter.responser.Responser;

public interface ResponserPool {
    void register(Responser responser);
    Responser poll(Level level);
    boolean hasFree(Level level);
}
