package callcenter;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class CallTest {

    @Test
    public void getId() {
        assertThat(Call.genByLevel(Level.Fresher).getId()).isNotNull();
    }

    @Test
    public void getLevel() {
        assertThat(Call.genByLevel(Level.Fresher).getLevel()).isEqualTo(Level.Fresher);
    }
}