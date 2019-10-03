package callcenter;

import callcenter.responser.Responser;

import java.util.HashMap;
import java.util.Map;

/**
 * This is just a POC Responser Pool, need to improve:
 * 1. group responser by level
 * 2. thread-safe to access responser
 * 3. more effective way to get free responser
 */
public class InMemResponserPool extends LeveledQueue<Responser> implements ResponserPool {


    @Override
    public void register(Responser responser) {
        add(responser);
    }

    @Override
    public boolean hasFree(Level level) {
        return hasNext(level);
    }
}
