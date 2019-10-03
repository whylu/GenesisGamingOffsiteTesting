package callcenter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.assertj.core.api.Assertions.*;

public class LeveledQueueTest {

    @Test
    public void add() {
        LeveledQueue<Call> queue = new LeveledQueue<>();

        Call pollCall = queue.poll(Level.Fresher);
        assertThat(pollCall).isNull();

        Call call = Call.genByLevel(Level.Fresher);
        queue.add(call);

        pollCall = queue.poll(Level.Fresher);
        assertThat(pollCall.getId()).isEqualTo(call.getId());

        pollCall = queue.poll(Level.Fresher);
        assertThat(pollCall).isNull();

        // add a null level call
        queue.add(Call.genByLevel(Level.Fresher));
        assertThat(queue.size(Level.Fresher)).isOne();


    }

    @Test
    public void testAddMultiThread() throws InterruptedException {
        LeveledQueue<Call> queue = new LeveledQueue<>();

        int threadNumber = 100;
        int callNumber = 1000;
        List<Thread> threads = new ArrayList<>();
        for(int i=0; i<threadNumber; i++) {
            Thread thread = new Thread(() -> {
                for(int c=0; c<callNumber; c++) {
                    queue.add(Call.genByLevel(Level.Fresher));
                }
            });
            threads.add(thread);

        }
        for(int i=0; i<threadNumber; i++) {
            Thread thread = new Thread(() -> {
                for(int c=0; c<callNumber; c++) {
                    queue.add(Call.genByLevel(Level.TL));
                }
            });
            threads.add(thread);

        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        assertThat(queue.size(Level.Fresher)).isEqualTo(threadNumber * callNumber);
        assertThat(queue.size(Level.TL)).isEqualTo(threadNumber * callNumber);
    }


    @Test
    public void poll() throws InterruptedException {
        Queue<Call> calls = new ConcurrentLinkedDeque<>();

        LeveledQueue<Call> queue = new LeveledQueue<>();
        int callNumber = 100000;
        for(int i=0; i<callNumber; i++) {
            queue.add(Call.genByLevel(Level.Fresher));
        }

        List<Thread> threads = new ArrayList<>();
        int threadNumber = 100;
        int loop = callNumber / threadNumber;
        for(int i=0; i<threadNumber; i++) {
            Thread thread = new Thread(() -> {
                for(int c=0; c<loop; c++) {
                    Call call = queue.poll(Level.Fresher);
                    calls.add(call);
                }
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        assertThat(calls.size()).isEqualTo(callNumber);

    }

    @Test
    public void addFirst() {
        LeveledQueue<Call> queue = new LeveledQueue<>();
        queue.add(Call.genByLevel(Level.Fresher));
        queue.add(Call.genByLevel(Level.Fresher));
        queue.add(Call.genByLevel(Level.Fresher));
        queue.add(Call.genByLevel(Level.Fresher));
        Call call = Call.genByLevel(Level.Fresher);
        queue.addFirst(call);
        Call poll_ = queue.poll(Level.Fresher);
        assertThat(poll_).isEqualTo(call);
    }

    @Test
    public void hasNext() {
        LeveledQueue<Call> queue = new LeveledQueue<>();
        assertThat(queue.hasNext(Level.Fresher)).isFalse();
        queue.add(Call.genByLevel(Level.Fresher));
        assertThat(queue.hasNext(Level.Fresher)).isTrue();
    }
}