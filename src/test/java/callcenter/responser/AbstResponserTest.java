package callcenter.responser;

import callcenter.Call;
import callcenter.Level;
import callcenter.Result;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class AbstResponserTest extends AbstResponser{

    public AbstResponserTest() {
        super(Level.Fresher);
    }

    @Test
    public void testHandle() {
        AbstResponserTest responser = new AbstResponserTest();
        Result good = responser.handle(Call.genByIssueLevel(Level.Fresher));
        assertThat(good.isSuccess()).isTrue();
        assertThat(good.getCallLevel()).isEqualTo(Level.Fresher);
        assertThat(good.getResponserLevel()).isEqualTo(Level.Fresher);

        Call pmCall = Call.genByIssueLevel(Level.PM);
        Result result = new Fresher().handle(pmCall);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getCallLevel()).isEqualTo(Level.TL);
        assertThat(result.getResponserLevel()).isEqualTo(Level.Fresher);

        result = new TL().handle(pmCall);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getCallLevel()).isEqualTo(Level.PM);
        assertThat(result.getResponserLevel()).isEqualTo(Level.TL);

        result = new PM().handle(pmCall);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getCallLevel()).isEqualTo(Level.PM);
        assertThat(result.getResponserLevel()).isEqualTo(Level.PM);
    }
}