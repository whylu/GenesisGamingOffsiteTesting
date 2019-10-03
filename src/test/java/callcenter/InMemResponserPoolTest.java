package callcenter;

import callcenter.responser.Fresher;
import callcenter.responser.Responser;
import callcenter.responser.TL;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class InMemResponserPoolTest {

    @Test
    public void testPoll() {
        InMemResponserPool pool = new InMemResponserPool();

        Responser fresher = pool.poll(Level.Fresher);
        assertThat(fresher).isNull();

        Responser f1 = new Fresher();
        pool.register(f1);
        fresher = pool.poll(Level.Fresher);
        assertThat(fresher.getId()).isEqualTo(f1.getId());

        fresher = pool.poll(Level.Fresher);
        assertThat(fresher).isNull();
    }

    @Test
    public void hasFree() {
        InMemResponserPool pool = new InMemResponserPool();
        assertThat(pool.hasFree(Level.Fresher)).isFalse();
        pool.register(new Fresher());
        assertThat(pool.hasFree(Level.Fresher)).isTrue();
    }

}