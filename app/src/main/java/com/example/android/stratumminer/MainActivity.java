package com.example.android.stratumminer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stratumminer.connection.IMiningConnection;
import com.example.android.stratumminer.connection.StratumMiningConnection;
import com.example.android.stratumminer.stratum.StratumSocket;
import com.example.android.stratumminer.worker.CpuMiningWorker;
import com.example.android.stratumminer.worker.IMiningWorker;

import java.net.MalformedURLException;
import java.net.URI;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import static android.R.id.edit;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.android.stratumminer.Constants.DEFAULT_BACKGROUND;
import static com.example.android.stratumminer.Constants.DEFAULT_SCREEN;
import static com.example.android.stratumminer.Constants.PREF_BACKGROUND;
import static com.example.android.stratumminer.Constants.PREF_NEWS_RUN_ONCE;
import static com.example.android.stratumminer.Constants.PREF_PASS;
import static com.example.android.stratumminer.Constants.PREF_SCREEN;
import static com.example.android.stratumminer.Constants.PREF_THREAD;
import static com.example.android.stratumminer.Constants.PREF_TITLE;
import static com.example.android.stratumminer.Constants.PREF_URL;
import static com.example.android.stratumminer.Constants.PREF_USER;

public class MainActivity extends AppCompatActivity {

    EditText et_serv;
    EditText et_user;
    EditText et_pass;
    CheckBox cb_service;
    CheckBox cb_screen_awake;

    int  baseThreadCount;

    boolean mBound = false;
    MinerService mService;

    public int curScreenPos=0;

    public ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("LC", "Main: onServiceConnected()");
            MinerService.LocalBinder binder = (MinerService.LocalBinder) service;
            mService = binder.getService();
            mBound=true;
            Log.i("LC", "Main: Service Connected");
        }

        public void onServiceDisconnected(ComponentName name) {  mBound=false;   }
    };


    public void startMining() {
        Log.i("LC", "Main: startMining()");
        mService.startMiner();
    }

    public void stopMining()
    {
        Log.i("LC", "Main: stopMining()");
        mService.stopMiner();

    }

//    private static final String DEFAULT_URL = "stratum+tcp://litecoinpool.org:3333";
//    private static final String DEFAULT_USER = "ltcTeminer.1";
//    private static final String DEFAULT_PASS = "1";
//
////    private static final String DEFAULT_URL = "stratum+tcp://tbdice.org:13333";
////    private static final String DEFAULT_USER = "LVXh1QYdKmRLFjoho9hSqHNfJoVHrSGUjW";
////    private static final String DEFAULT_PASS = "1234";
//
//    private static final String TAG = "MainActivity";
//    private static final long DEFAULT_SCAN_TIME = 5000;
//    private static final long DEFAULT_RETRY_PAUSE = 30000;
//
//    public static final String PREFS_NAME = "prefs";
//    SharedPreferences settings;
//
//    private IMiningConnection mc = null;
//    private IMiningWorker imw = null;
//    private SingleMiningChief smc = null;
//
//    //private Worker worker;
//    private long lastWorkTime;
//    private long lastWorkHashes;
//
//    private EditText URL, Cred;
//
//    int temperature;

    private static int updateDelay=1000;
    String unit = " h/s";

    Handler statusHandler = new Handler() { };

    final Runnable rConsole = new Runnable() {
        public void run() {
            //Log.i("LC", "StatusActivity:updateConsole:"+mService.console.getConsole());
            TextView txt_console = (TextView) findViewById(R.id.status_textView_console);
            txt_console.setText(mService.cString);
            txt_console.invalidate();
        }
    };

    final Runnable rSpeed = new Runnable() {
        public void run() {
            // Log.i("LC", "StatusActivity:updateSpeed");
            TextView tv_speed = (TextView) findViewById(R.id.status_textView_speed);
            DecimalFormat df = new DecimalFormat("#.##");
            tv_speed.setText(df.format(mService.speed)+unit);
        }
    };
    final Runnable rAccepted = new Runnable() {
        public void run() {
            // Log.i("LC", "StatusActivity:updateAccepted");
            TextView txt_accepted = (TextView) findViewById(R.id.status_textView_accepted);
            txt_accepted.setText(String.valueOf(mService.accepted));
        }
    };
    final Runnable rRejected = new Runnable() {
        public void run() {
            // Log.i("LC", "StatusActivity:updateRejected");
            TextView txt_rejected = (TextView) findViewById(R.id.status_textView_rejected);
            txt_rejected.setText(String.valueOf(mService.rejected));
        }
    };
    final Runnable rStatus = new Runnable() {
        public void run() {
            //  Log.i("LC", "StatusActivity:updateStatus");
            TextView txt_status = (TextView) findViewById(R.id.status_textView_status);
            txt_status.setText(mService.status);
        }
    };
    final Runnable rBtnStart= new Runnable() {
        public void run() {
            // Log.i("LC", "StatusActivity: Miner stopped, changing button to start");
            Button b = (Button) findViewById(R.id.status_button_startstop);
            b.setText(getString(R.string.main_button_start));
            if (firstRunFlag) {
                b.setEnabled(true);
                b.setClickable(true);
//                firstRunFlag = false;
            }
            else if (StartShutdown) {
                b.setEnabled(false);
                b.setClickable(false);
                if (!ShutdownStarted) {
                    ShutdownStarted = true;
                    CpuMiningWorker worker = (CpuMiningWorker)mService.imw;
                    ThreadStatusAsyncTask threadWaiter = new ThreadStatusAsyncTask();
                    threadWaiter.execute(worker);
                }
            }
        }
    };
    final Runnable rBtnStop= new Runnable() {
        public void run() {
            //Log.i("LC", "StatusActivity: Miner stopped, changing button to stop");
            Button b = (Button) findViewById(R.id.status_button_startstop);
            b.setText(getString(R.string.main_button_stop));
            b.setEnabled(true);
        }
    };

    public volatile  boolean firstRunFlag = true;
    public volatile  boolean ShutdownStarted = false;
    public volatile  boolean StartShutdown = false;

    Thread updateThread = new Thread () {
        public void run() {
            Log.i("LC", "StatusActivity: Update thread started");
            // wait for service to bind
            while (mBound==false)
            {
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    Log.i("LC", "StatusActivity:updateThread: Interrupted");
                }
            }

            // If the service is running make sure the button is changed to "Stop Mining" and vice versa
            if(mService.running==true) { statusHandler.post(rBtnStop); }
            else { statusHandler.post(rBtnStart); }

            while (mBound==true)	{
                try {
                    sleep(updateDelay);
                } catch (InterruptedException e) {
                    Log.i("LC", "StatusActivity:updateThread: Interrupted");
                }

                statusHandler.post(rConsole);
                statusHandler.post(rSpeed);
                statusHandler.post(rAccepted);
                statusHandler.post(rRejected);
                statusHandler.post(rStatus);
                if(mService.running==true) { statusHandler.post(rBtnStop); }
                else {statusHandler.post(rBtnStart);
                }
            } }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Log.i("LC", "Main: in onCreate()");
//        setTitle("StratumMiner");

        //setContentView(R.layout.activity_status);
        Log.i("LC", "Status: onCreate");



        Intent intent = new Intent(getApplicationContext(), MinerService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        Button btn_startStop = (Button) findViewById(R.id.status_button_startstop);
        et_serv = (EditText) findViewById(R.id.server_et);
        et_user = (EditText) findViewById((R.id.user_et));
        et_pass = (EditText) findViewById(R.id.password_et);
        cb_service = (CheckBox) findViewById(R.id.settings_checkBox_background) ;
        cb_service.setChecked(DEFAULT_BACKGROUND);
        cb_screen_awake = (CheckBox) findViewById(R.id.settings_checkBox_keepscreenawake) ;
        cb_screen_awake.setChecked(DEFAULT_SCREEN);
        setThreads();

        // Set Button Click Listener
        btn_startStop.setOnClickListener(new Button.OnClickListener() {

                                             public void onClick(View v) {
                                                 Button b = (Button) v;

                                                 if (b.getText().equals(getString(R.string.status_button_start))==true){
                                                     StringBuilder sb= new StringBuilder();
                                                     sb = new StringBuilder();
                                                     String url = sb.append(et_serv.getText()).toString();
                                                     sb.setLength(0);
                                                     String user = sb.append(et_user.getText()).toString();
                                                     sb.setLength(0);
                                                     String pass = sb.append(et_pass.getText()).toString();
                                                     sb.setLength(0);

                                                     Spinner threadList = (Spinner)findViewById(R.id.spinner1);

                                                     int threads = Integer.parseInt(threadList.getSelectedItem().toString());

                                                     SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
                                                     SharedPreferences.Editor editor = settings.edit();
                                                     settings = getSharedPreferences(PREF_TITLE, 0);
                                                     editor = settings.edit();
                                                     editor.putString(PREF_URL, url);
                                                     editor.putString(PREF_USER, user);
                                                     editor.putString(PREF_PASS, pass);
                                                     editor.putInt(PREF_THREAD, threads);
                                                     editor.putBoolean(PREF_BACKGROUND, cb_service.isChecked());
                                                     editor.putBoolean(PREF_SCREEN, cb_screen_awake.isChecked());
                                                     editor.commit();
                                                     if(settings.getBoolean(PREF_SCREEN,DEFAULT_SCREEN )==true) {
                                                         getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                     }
                                                     startMining();
                                                     firstRunFlag = false;
                                                     b.setText(getString(R.string.main_button_stop));
                                                 }
                                                 else{
                                                     stopMining();
                                                     StartShutdown = true;
                                                     b.setText(getString(R.string.status_button_start));
//                                                     b.setEnabled(false);
//                                                     b.setClickable(false);
//                                                     //Log.i("MainActivity","stopMining: baseThreadCount = " + baseThreadCount);
//                                                     CpuMiningWorker worker = (CpuMiningWorker)mService.imw;
//                                                     ThreadStatusAsyncTask threadWaiter = new ThreadStatusAsyncTask();
//                                                     threadWaiter.execute(worker);
                                                 }
                                             }
        });


        updateThread.start();

//        // Launch news on first run
//        if(settings.getBoolean(PREF_NEWS_RUN_ONCE, false)==false)
//        {
//            intent = new Intent(getApplicationContext(), NewsActivity.class);
//            startActivity(intent);
//        }

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        settings = getSharedPreferences(PREFS_NAME, 0);
//        URL = (EditText)findViewById(R.id.editText1);
//        Cred = (EditText)findViewById(R.id.editText2);
//
//        this.registerReceiver(this.mBatInfoReceiver,
//                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//
//
//        Handler mHandler = new Handler();
//        mHandler.postDelayed(runnable, 1);

//        try {
////			IMiningConnection mc=new TestStratumMiningConnection(0);
//            mc = new StratumMiningConnection(DEFAULT_URL,DEFAULT_USER,DEFAULT_PASS);
//            imw = new CpuMiningWorker();
//            smc = new SingleMiningChief(mc,imw);
//            smc.startMining();
//            for(;;){
//                Thread.sleep(1000);
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    public void setButton (boolean flag) {
        Button btn = (Button) findViewById(R.id.status_button_startstop);
        if (flag) {
            btn.setEnabled(true);
            btn.setClickable(true);
        } else {
            btn.setEnabled(false);
            btn.setClickable(false);
        }
    }

//    private static class ThreadParams {
//        CpuMiningWorker _worker;
//        View _v;
//
//        ThreadParams (CpuMiningWorker worker,View v) {
//            this._worker=worker;
//            this._v=v;
//        }
//    }

    public class ThreadStatusAsyncTask extends AsyncTask<CpuMiningWorker,Integer,Boolean> {


        @Override
        protected Boolean doInBackground(CpuMiningWorker... params) {
            Log.i("AsyncTask","Started");
            long lastTime = System.currentTimeMillis();
            long currTime;
            while (params[0].getThreadsStatus()) {
                currTime = System.currentTimeMillis();
                double deltaTime = (double)(currTime-lastTime)/1000.0;
                if (deltaTime>15.0) {
                    Log.i("AsyncTask","Still Waiting");
                    params[0].ConsoleWrite("Still cooling down...");
                    lastTime = currTime;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                setButton(true);
                ShutdownStarted = false;
                StartShutdown = false;
                firstRunFlag = true;
                Toast.makeText(MainActivity.this,"Cooldown finished",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        super.onPause();
    }


    @Override
    protected void onResume() {
        SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
        if (settings.getBoolean(PREF_BACKGROUND, DEFAULT_BACKGROUND)==true)
        {
            TextView tv_background = (TextView) findViewById(R.id.status_textView_background);
            tv_background.setText("RUN IN BACKGROUND");
        }
        super.onResume();
    }



    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if(updateThread.isAlive()==true) { updateThread.interrupt(); }

        SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
        if(settings.getBoolean(PREF_BACKGROUND,DEFAULT_BACKGROUND )==false)
        {
            if (mService != null && mService.running == true) { stopMining(); }
            Intent intent = new Intent(getApplicationContext(), MinerService.class);
            stopService(intent);
        }

        Log.i("LC", "Main: in onStop()");
        try {
            unbindService(mConnection);
        } catch (RuntimeException e) {
            Log.i("LC", "RuntimeException:"+e.getMessage());
            //unbindService generates a runtime exception sometimes
            //the service is getting unbound before unBindService is called
            //when the window is dismissed by the user, this is the fix
        }

        super.onStop();
    }


    void setThreads()
    {
        try
        {
            //log(Integer.toString(Runtime.getRuntime().availableProcessors()));
            Spinner threadList = (Spinner)findViewById(R.id.spinner1);

            String[] threadsAvailable = new String[Runtime.getRuntime().availableProcessors()];

            for(int i = 0; i <= Runtime.getRuntime().availableProcessors();i++)
            {
                //log(Integer.toString(i));
                threadsAvailable[i] = Integer.toString(i + 1);
                ArrayAdapter threads = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, threadsAvailable);
                threadList.setAdapter(threads);
            }
        }
        catch (Exception e){}
    }
//
//    void setUI()
//    {
//        Button startMining = (Button)findViewById(R.id.button1);
//        Button stopMining = (Button)findViewById(R.id.button2);
//
//        //Not started
//        stopMining.setEnabled(false);
//
//        URL.setText(settings.getString("URLText", "stratum+tcp://litecoinpool.org:3333"));
//        Cred.setText(settings.getString("CredText", "user:password"));
//
//        if(smc != null) {
//            if(smc._connection != null) {
//                startMining.setEnabled(false);
//                stopMining.setEnabled(true);
//            }
//            else {
//                startMining.setEnabled(true);
//                stopMining.setEnabled(false);
//            }
//        }
//        else {
//            startMining.setEnabled(true);
//            stopMining.setEnabled(false);
//        }
//
//        startMining.setOnClickListener(new View.OnClickListener() {
//            //@Override
//            @Override
//            public void onClick(View v)
//            {
//                Spinner threadList = (Spinner)findViewById(R.id.spinner1);
//
//                String URLText = URL.getText().toString();
//                String CredText = Cred.getText().toString();
//
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putString("URLText", URLText);
//                editor.putString("CredText", CredText);
//                editor.commit();
//
//                startMiner(URLText, CredText, threadList.getSelectedItem().toString(), "1.0", "5000", "30000");
//            }
//        });
//
//        stopMining.setOnClickListener(new View.OnClickListener() {
//            //@Override
//            @Override
//            public void onClick(View v)
//            {
//                log("Stopping...");
//                log("This can take a few minutes, please be patient (Up to 2 mins)");
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        try {
//                            if (smc != null) {
////                                mc.disconnect();
//                                smc.stopMining();
////                    imw.stopWork();
////                    mc.disconnect();
//                                mc = null;
//                                imw = null;
//                                smc = null;
//                                setUI();
//                            }
//                        } catch (MinyaException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, 1000);
//            }
//        });
//    }
//
//    void startMiner(String URL, String Auth, String Threads, String Throttle, String ScanTime, String RetryPause)
//    {
//        String[] args = { URL, Auth, Threads, Throttle, ScanTime, RetryPause };
//
//        main(args);
//    }
//
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            setThreads();
//            setUI();
//            //startMiner("http://litecoinpool.org:9332/", "Simran.android:android", "2", "1.0", "5000", "30000");
//        }
//    };
//
//    void Miner(String url, String auth, long scanTime, long retryPause, int nThread, double throttle) {
//        if (nThread < 1)
//            throw new IllegalArgumentException("Invalid number of threads: " + nThread);
//        if (throttle <= 0.0 || throttle > 1.0)
//            throw new IllegalArgumentException("Invalid throttle: " + throttle);
//        if (scanTime < 1L)
//            throw new IllegalArgumentException("Invalid scan time: " + scanTime);
//        if (retryPause < 0L)
//            throw new IllegalArgumentException("Invalid retry pause: " + retryPause);
//        try {
////            mc = new StratumMiningConnection(DEFAULT_URL,DEFAULT_USER,DEFAULT_PASS);
////            imw = new CpuMiningWorker();
//            mc = new StratumMiningConnection(url,DEFAULT_USER,DEFAULT_PASS);
//            imw = new CpuMiningWorker(nThread);
//            smc = new SingleMiningChief(mc,imw);
//            smc.startMining();
////            for(;;){
////                Thread.sleep(1000);
////            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
////            throw new IllegalArgumentException("Invalid URL: " + url);
//            log("Invalid URL: " + url);
//        }
//        ((CpuMiningWorker)smc._worker).addObserver(this);
//        ((StratumMiningConnection)smc._connection).addObserver(this);
////        worker.addObserver(this);
////        Thread t = new Thread(worker);
////        t.setPriority(Thread.MIN_PRIORITY);
////        t.start();
//        log(nThread + " miner threads started");
//        setUI();
//    }
//
//    private static final DateFormat logDateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ");
//    protected static final int REFRESH = 0;
//
//    public void log(final String str) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                TextView Console = (TextView)findViewById(R.id.textView1);
//
//                Console.append(logDateFormat.format(new Date()) + str + "\n");
//                //Log.i(TAG, logDateFormat.format(new Date()) + str);
//
//            }
//        });
//    }
//
//
//    @Override
//    public void update(Observable o, Object arg) {
//        IMiningWorker.Notification n;
//        n = (IMiningWorker.Notification) arg;
//        if (n == IMiningWorker.Notification.SYSTEM_ERROR) {
//            log("System error");
//            System.exit(1);
//        } else if (n == IMiningWorker.Notification.TERMINATED) {
//            log("Miner shutdown");
//        } else if (n == IMiningWorker.Notification.PERMISSION_ERROR) {
//            log("Permission error");
//            System.exit(1);
//        } else if (n == IMiningWorker.Notification.AUTHENTICATION_ERROR) {
//            log("Invalid worker username or password");
//            System.exit(1);
//        } else if (n == IMiningWorker.Notification.CONNECTION_ERROR) {
//            log("Connection error, retrying in " + 3000/1000L + " seconds");
//        } else if (n == IMiningWorker.Notification.COMMUNICATION_ERROR) {
//            log("Communication error");
//        } else if (n == IMiningWorker.Notification.LONG_POLLING_FAILED) {
//            log("Long polling failed");
//        } else if (n ==IMiningWorker.Notification.LONG_POLLING_ENABLED) {
//            log("Long polling activated");
//        } else if (n == IMiningWorker.Notification.NEW_BLOCK_DETECTED) {
//            log("LONGPOLL detected new block");
//        } else if (n == IMiningWorker.Notification.POW_TRUE) {
//            log("PROOF OF WORK RESULT: true (yay!!!)");
//        } else if (n == IMiningWorker.Notification.POW_FALSE) {
//            log("PROOF OF WORK RESULT: false (booooo)");
//        } else if (n == IMiningWorker.Notification.NEW_WORK) {
//            if (lastWorkTime > 0L) {
//                long hashes =smc._worker.getNumberOfHash() - lastWorkHashes;
//                float speed = (float) hashes / Math.max(1, System.currentTimeMillis() - lastWorkTime);
//                log(String.format("%d hashes, %.2f khash/s", hashes, speed) + " - " + temperature/10 + " C");
//            }
//            lastWorkTime = System.currentTimeMillis();
//            lastWorkHashes = smc._worker.getNumberOfHash();
//        }
//        hRefresh.sendEmptyMessage(REFRESH);
//    }
//
//    public void main(String[] args) {
//        String url = null;
//        String auth = null;
//        int nThread = Runtime.getRuntime().availableProcessors();
//        double throttle = 1.0;
//        long scanTime = DEFAULT_SCAN_TIME;
//        long retryPause = DEFAULT_RETRY_PAUSE;
//
//        if (args.length > 0 && args[0].equals("--help")) {
//            Log.i(TAG, "Usage:  java Miner [URL] [USERNAME:PASSWORD] [THREADS] [THROTTLE] [SCANTIME] [RETRYPAUSE]");
//            return;
//        }
//
//        if (args.length > 0) url = args[0];
//        if (args.length > 1) auth = args[1];
//        if (args.length > 2) nThread = Integer.parseInt(args[2]);
//        if (args.length > 3) throttle = Double.parseDouble(args[3]);
//        if (args.length > 4) scanTime = Integer.parseInt(args[4]) * 1000L;
//        if (args.length > 5) retryPause = Integer.parseInt(args[5]) * 1000L;
//
//        try {
//            Miner(url, auth, scanTime, retryPause, nThread, throttle);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //Log.e(TAG, e.getMessage());
//        }
//    }
//
//    Handler hRefresh = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch(msg.what){
//                case REFRESH:
//				/*Refresh UI*/
//                    setUI();
//                    break;
//            }
//        }
//    };
//
//    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
//        @Override
//        public void onReceive(Context arg0, Intent intent) {
//            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
//        }
//    };


}
