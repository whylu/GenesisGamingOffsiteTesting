package callcenter;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class LevelTest {

    @Test
    public void isMax() {
        assertThat(Level.Fresher.isMax()).isFalse();
        assertThat(Level.PM.isMax()).isTrue();
    }
}