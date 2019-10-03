package callcenter;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class InMemCallQueueTest {

    @Test
    public void poll() {
        CallQueue queue = new InMemCallQueue();

        Call pollCall = queue.poll(Level.Fresher);
        assertThat(pollCall).isNull();

        Call call = Call.genByLevel(Level.Fresher);
        queue.add(call);

        pollCall = queue.poll(Level.Fresher);
        assertThat(pollCall.getId()).isEqualTo(call.getId());

        pollCall = queue.poll(Level.Fresher);
        assertThat(pollCall).isNull();

    }

    @Test
    public void hasNext() {
        CallQueue queue = new InMemCallQueue();

        assertThat(queue.hasNext(Level.Fresher)).isFalse();
        assertThat(queue.hasNext(Level.TL)).isFalse();

        queue.add(Call.genByLevel(Level.Fresher));
        assertThat(queue.hasNext(Level.Fresher)).isTrue();
        assertThat(queue.hasNext(Level.TL)).isFalse();

        queue.poll(Level.Fresher);
        assertThat(queue.hasNext(Level.Fresher)).isFalse();
        assertThat(queue.hasNext(Level.TL)).isFalse();
    }
}