package example;

import callcenter.Call;
import callcenter.Level;
import callcenter.Result;
import callcenter.responser.Fresher;
import callcenter.responser.PM;
import callcenter.responser.TL;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class MyCallCenterTest {
    private static Logger logger = LoggerFactory.getLogger(MyCallCenterTest.class);

    @Test(timeout = 3000L)
    public void testQueueCall() throws InterruptedException {
        MyCallCenter callCenter = new MyCallCenter();
        callCenter.start();

        for(int i=0; i<100; i++) {
            callCenter.receive(Call.genByIssueLevel(Level.Fresher));
            Thread.sleep(10);
        }
        assertThat(callCenter.getCallQueueSize()).isEqualTo(100);

        callCenter.register(new Fresher());


        while (callCenter.getCallQueueSize()>0) {
            Thread.sleep(10);
        }

    }


    @Test(timeout = 3000L)
    public void testNoLevel1Responser() throws InterruptedException {
        MyCallCenter callCenter = new MyCallCenter();
        callCenter.start();

        callCenter.register(new TL());

        new Thread(() -> {
            for(int i=0; i<100; i++) {
                callCenter.receive(Call.genByIssueLevel(Level.Fresher));
            }
        }).start();

        new Thread(() -> {
            for(int i=0; i<100; i++) {
                callCenter.receive(Call.genByIssueLevel(Level.TL));
            }
        }).start();

        Thread.sleep(100);
        while (callCenter.getCallQueueSize()>0) { //all call should be handled
            Thread.sleep(10);
        }
    }

    @Test
    public void testAllFailed() throws InterruptedException {
        MyCallCenter callCenter = new MyCallCenter();
        callCenter.start();
        for(int i=0; i<100; i++) {
            callCenter.register(new Fresher());
        }

        for(int i=0; i<200; i++) {
            callCenter.receive(Call.genByIssueLevel(Level.TL));
        }
        Thread.sleep(500);
        assertThat(callCenter.results.size()).isEqualTo(0);
        assertThat(callCenter.getCallQueueSize()).isEqualTo(200);
    }

    /**
     *
     * responser1    call1=====================
     * responser2       call2=====================
     * responser3           call3==
     *                             call4==   (should take by  responser3)
     *
     * @throws InterruptedException
     */
    @Test(timeout = 3000L)
    public void test1() throws InterruptedException {
        MyCallCenter callCenter = new MyCallCenter();
        callCenter.start();

        callCenter.register(new Fresher()); //r1
        callCenter.register(new Fresher()); //r2
        callCenter.register(new Fresher()); //r3

        Call call1 = Call.genByIssueLevel(Level.Fresher, 1000);
        Call call2 = Call.genByIssueLevel(Level.Fresher, 1200);
        Call call3 = Call.genByIssueLevel(Level.Fresher, 300);
        Call call4 = Call.genByIssueLevel(Level.Fresher, 200);

        callCenter.receive(call1); //call 2 end at 1000
        Thread.sleep(100);
        callCenter.receive(call2); //call 2 end at 1300
        Thread.sleep(100);
        callCenter.receive(call3); //call 2 end at 500
        callCenter.receive(call4); //call 4 end at 500(call2) + 200 = 700

        Thread.sleep(1500);

        Result result;

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c3");
        assertThat(result.getResponser().getId()).isEqualTo("r3");

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c4");
        assertThat(result.getResponser().getId()).isEqualTo("r3");

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c1");
        assertThat(result.getResponser().getId()).isEqualTo("r1");

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c2");
        assertThat(result.getResponser().getId()).isEqualTo("r2");


    }


    /**
     *
     * Fresher1    call1 level 1===========================
     * Fresher2    call2 level 1=====================================
     * TL          call3 level 1==
     * PM                 call4 level 1==   (should take by PM)
     *                     call5 level 1==   (should take by TL)
     *                       call6 level 1==   (should take by PM)
     *
     Result:
     * Fresher1    call1 level 1===========================
     * Fresher2    call2 level 1=====================================
     * TL          call3 level 1== call5 level 1==
     * PM                 call4 level 1== call6 level 1==
     *
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        MyCallCenter callCenter = new MyCallCenter();
        callCenter.start();

        callCenter.register(new Fresher());
        callCenter.register(new Fresher());
        callCenter.register(new TL());
        callCenter.register(new PM());

        callCenter.receive(Call.genByIssueLevel(Level.Fresher, 8000)); //c1  > Fresher1
        callCenter.receive(Call.genByIssueLevel(Level.Fresher, 9000));//c2  > Fresher1
        callCenter.receive(Call.genByIssueLevel(Level.Fresher, 2000));//c3  > TL
        Thread.sleep(1000);   // all busy except PM
        callCenter.receive(Call.genByIssueLevel(Level.Fresher, 2000));//c4 >> PM end at 240
        Thread.sleep(100);
        callCenter.receive(Call.genByIssueLevel(Level.Fresher, 2000));  //c5 >> TL is free
        Thread.sleep(100);
        callCenter.receive(Call.genByIssueLevel(Level.Fresher, 2000));  //c6 // PM is free

        while(callCenter.results.size()<6) {
            Thread.sleep(100);
        }

        Result result;

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c3");
        assertThat(result.getResponser().getId()).isEqualTo("r3");
        assertThat(result.getResponser().getLevel()).isEqualTo(Level.TL);

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c4");
        assertThat(result.getResponser().getId()).isEqualTo("r4");
        assertThat(result.getResponser().getLevel()).isEqualTo(Level.PM);

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c5");
        assertThat(result.getResponser().getId()).isEqualTo("r3");
        assertThat(result.getResponser().getLevel()).isEqualTo(Level.TL);

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c6");
        assertThat(result.getResponser().getId()).isEqualTo("r4");
        assertThat(result.getResponser().getLevel()).isEqualTo(Level.PM);

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c1");
        assertThat(result.getResponser().getId()).isEqualTo("r1");
        assertThat(result.getResponser().getLevel()).isEqualTo(Level.Fresher);

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c2");
        assertThat(result.getResponser().getId()).isEqualTo("r2");
        assertThat(result.getResponser().getLevel()).isEqualTo(Level.Fresher);

        assertThat(callCenter.results.size()).isZero();
    }


    // TODO: how can I verify the result order without a result queue?

    /**
     *
     * Fresher1    call1 level 1=====================
     * Fresher2    call2 level 2==
     * TL          `-> call2 level 2==
     *               call3 level 2==   (should take by TL)
     *
     Result:
     * Fresher1    call1 level 1=====================
     * Fresher2    call2 Escalate, call3 Escalate
     * TL          `-> call2 level 2== `-> call3 level 2==
     *
     * @throws InterruptedException
     */
    @Test
    public void testCallEscalate() throws InterruptedException {
        MyCallCenter callCenter = new MyCallCenter();
        callCenter.start();

        Fresher fresher1 = spy(new Fresher());
        callCenter.register(fresher1);
        Fresher fresher2 = spy(new Fresher());
        callCenter.register(fresher2);
        TL tl = spy(new TL());
        callCenter.register(tl);

        Call c1 = Call.genByIssueLevel(Level.Fresher, 5000);
        callCenter.receive(c1);
        Call c2 = Call.genByIssueLevel(Level.TL, 2000);
        callCenter.receive(c2);
        Thread.sleep(500);   // all busy except PM
        Call c3 = Call.genByIssueLevel(Level.TL, 2000);
        callCenter.receive(c3);

        while(callCenter.results.size()<3) {
            Thread.sleep(100);
        }

        verify(fresher1).handle(eq(c1), any());
        verify(fresher2).handle(eq(c2), any()); //fresher2 can't handle call2(Level.TL), pass it to TL
        verify(tl).handle(eq(c2), any());
        verify(fresher2).handle(eq(c3), any()); //fresher2 can't handle call3(Level.TL), pass it to TL
        verify(tl).handle(eq(c3), any());

        Result result;

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c2");
        assertThat(result.getResponser().getId()).isEqualTo("r3");
        assertThat(result.getResponser().getLevel()).isEqualTo(Level.TL);

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c3");
        assertThat(result.getResponser().getId()).isEqualTo("r3");
        assertThat(result.getResponser().getLevel()).isEqualTo(Level.TL);

        result = callCenter.results.poll();
        assertThat(result.getCall().getId()).isEqualTo("c1");
        assertThat(result.getResponser().getId()).isEqualTo("r1");
        assertThat(result.getResponser().getLevel()).isEqualTo(Level.Fresher);
    }




}