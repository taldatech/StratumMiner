package com.example.android.stratumminer.test;

import com.example.android.stratumminer.connection.StratumMiningConnection;

/**
 * Created by Ben David on 01/08/2017.
 */

public class StratumConnectionTest
{
    private static final String DEFAULT_URL = "stratum+tcp://xxxx";
    private static final String DEFAULT_USER = "user";
    private static final String DEFAULT_PASS = "pass";
    public static void main(String[] args)
    {
        try {
            StratumMiningConnection smc=new StratumMiningConnection(DEFAULT_URL,DEFAULT_USER,DEFAULT_PASS);
            smc.connect();
            for(;;){
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}