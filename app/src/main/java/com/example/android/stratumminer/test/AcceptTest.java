package com.example.android.stratumminer.test;

import com.example.android.stratumminer.MiningWork;
import com.example.android.stratumminer.connection.TestStratumMiningConnection;

/**
 * Created by Ben David on 01/08/2017.
 */

public class AcceptTest
{
    public static void main(String[] args)
    {
        try {
            TestStratumMiningConnection twf=new TestStratumMiningConnection(0);
            MiningWork mw=twf.getWork();
            mw.dump();
            long start = System.currentTimeMillis();
            System.out.println(System.currentTimeMillis()-start);
            return;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}