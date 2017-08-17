//package com.example.android.stratumminer;
//
//import com.example.android.stratumminer.connection.IMiningConnection;
//import com.example.android.stratumminer.connection.StratumMiningConnection;
//import com.example.android.stratumminer.worker.CpuMiningWorker;
//import com.example.android.stratumminer.worker.IMiningWorker;
//
///**
// * Created by Ben David on 01/08/2017.
// */
//
//public class EntryPoint
//{
////    private static final String DEFAULT_URL = "stratum+tcp://stratum-eu.nyan.luckyminers.com:3320";
////    private static final String DEFAULT_USER = "user";
////    private static final String DEFAULT_PASS = "pass";
//
//    private static final String DEFAULT_URL = "stratum+tcp://litecoinpool.org:3333";
//    private static final String DEFAULT_USER = "ltcTeminer.1";
//    private static final String DEFAULT_PASS = "1";
//
//    public static void main(String[] args)
//    {
//        try {
////			IMiningConnection mc=new TestStratumMiningConnection(0);
//            IMiningConnection mc=new StratumMiningConnection(DEFAULT_URL,DEFAULT_USER,DEFAULT_PASS);
//            IMiningWorker imw=new CpuMiningWorker(4);
//            SingleMiningChief smc=new SingleMiningChief(mc,imw);
//            smc.startMining();
//            for(;;){
//                Thread.sleep(1000);
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//}