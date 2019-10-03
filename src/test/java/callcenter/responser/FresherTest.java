package callcenter.responser;

import callcenter.Level;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class FresherTest {

    @Test
    public void testFresher() {
        Fresher f = new Fresher();
        assertThat(f.getLevel()).isEqualTo(Level.Fresher);
    }

}