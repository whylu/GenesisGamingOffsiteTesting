package callcenter.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class UtilsTest {

    @Test
    public void genId() {
        Utils.reset();
        assertThat(Utils.genCallId()).isEqualTo(1L);
        assertThat(Utils.genCallId()).isEqualTo(2L);
    }
}