package callcenter;

public interface CallQueue {


    void add(Call call);

    Call poll(Level level);

    void addFirst(Call call);

    boolean hasNext(Level level);

    int size();
}
