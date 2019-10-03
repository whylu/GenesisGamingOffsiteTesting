package example;

import callcenter.Call;
import callcenter.Level;
import callcenter.responser.Fresher;
import callcenter.responser.TL;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.spy;

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

        callCenter.register(new Fresher());
        callCenter.register(new Fresher());
        callCenter.register(new Fresher());

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
    }


    /**
     *
     * Fresher1    call level 1=====================
     * Fresher2    call level 1============================
     * TL          call level 1==
     * PM                 call level 1==   (should take by PM)
     *                        call level 1==   (should take by TL)
     *                        call level 1==   (should take by PM)
     *
     * @throws InterruptedException
     */
    @Test(timeout = 3000L)
    public void test2() throws InterruptedException {
        MyCallCenter callCenter = new MyCallCenter();
        callCenter.start();

        callCenter.register(new Fresher());
        callCenter.register(new Fresher());
        callCenter.register(new TL());

        Call call1 = Call.genByIssueLevel(Level.Fresher, 800);
        Call call2 = Call.genByIssueLevel(Level.Fresher, 900);
        Call call3 = Call.genByIssueLevel(Level.Fresher, 100);
        Call call4 = Call.genByIssueLevel(Level.Fresher, 100);
        Call call5 = Call.genByIssueLevel(Level.Fresher, 100);
        Call call6 = Call.genByIssueLevel(Level.Fresher, 100);

        callCenter.receive(call1);
        callCenter.receive(call2);
        callCenter.receive(call3);
        Thread.sleep(160);   // all busy except PM
        callCenter.receive(call4);
        Thread.sleep(40);
        callCenter.receive(call5);  // TL is free
        callCenter.receive(call6);   // PM is free


        Thread.sleep(1000);
    }


    // TODO: how can I verify the result order without a result queue?

    /**
     *
     * Fresher1    call level 1=====================
     * Fresher2    call level 2==
     * TL          `-> call level 2==
     *                                    call level 2==   (should take by TL)
     *
     * @throws InterruptedException
     */
    @Test(timeout = 3000L)
    public void test3() throws InterruptedException {
        MyCallCenter callCenter = new MyCallCenter();
        callCenter.start();

        callCenter.register(new Fresher());
        callCenter.register(new Fresher());
        callCenter.register(new TL());

        Call call1 = Call.genByIssueLevel(Level.Fresher, 800);
        Call call2 = Call.genByIssueLevel(Level.TL, 200);
        Call call3 = Call.genByIssueLevel(Level.TL, 200);

        callCenter.receive(call1);
        callCenter.receive(call2);
        Thread.sleep(500);   // all busy except PM
        callCenter.receive(call3);

        Thread.sleep(1000);
    }




}