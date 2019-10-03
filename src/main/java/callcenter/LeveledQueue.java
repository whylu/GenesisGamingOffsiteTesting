package callcenter;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class LeveledQueue<T extends Leveled> {

    private Map<String, Deque<T>> leveledQueues = new HashMap<>();

    public LeveledQueue() {
        for (Level level : Level.values()) {
            leveledQueues.put(level.name(), new ConcurrentLinkedDeque<>());
        }
    }

    public void add(T leveled) {
        if(leveled.getLevel()==null) {
            leveledQueues.get(Level.min().name()).add(leveled);
        } else {
            leveledQueues.get(leveled.getLevel().name()).add(leveled);
        }
    }


    public T poll(Level level) {
        Deque<T> deque = leveledQueues.get(level.name());
        return deque.poll();
    }

    public void addFirst(T call) {
        if(call.getLevel()==null) {
            leveledQueues.get(Level.min().name()).addFirst(call);
        } else {
            leveledQueues.get(call.getLevel().name()).addFirst(call);
        }
    }

    public boolean hasNext(Level level) {
        return leveledQueues.get(level.name()).peek()!=null;
    }


    public int size(Level level) {
        return leveledQueues.get(level.name()).size();
    }

    public int size() {
        int size = 0;
        for (Level level : Level.values()) {
            size += size(level);
        }
        return size;
    }
}
