package com.example.android.stratumminer.test;

import com.example.android.stratumminer.MiningWork;
import com.example.android.stratumminer.connection.TestStratumMiningConnection;

/**
 * Created by Ben David on 01/08/2017.
 */

public class SubmitFormatTest
{
    //private static final String DEFAULT_URL = "stratum+tcp://xxxx";
    //private static final String DEFAULT_USER = "user";
    //private static final String DEFAULT_PASS = "pass";
    public static void main(String[] args)
    {
        try {
            TestStratumMiningConnection smc=new TestStratumMiningConnection(1);
            smc.connect();
            MiningWork mw=smc.getWork();
            smc.submitWork(mw,0x12345678);
            for(;;){
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}