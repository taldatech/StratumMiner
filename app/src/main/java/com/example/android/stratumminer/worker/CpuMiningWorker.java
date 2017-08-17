package com.example.android.stratumminer.worker;

import android.util.Log;

import com.example.android.stratumminer.Console;
import com.example.android.stratumminer.MiningWork;
import com.example.android.stratumminer.MinyaException;
import com.example.android.stratumminer.MinyaLog;
import com.example.android.stratumminer.hasher.Hasher;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.android.stratumminer.Constants.DEFAULT_PRIORITY;
import static com.example.android.stratumminer.Constants.DEFAULT_RETRYPAUSE;
import static com.example.android.stratumminer.R.id.parent;
import static java.lang.Thread.MIN_PRIORITY;
import static java.lang.Thread.activeCount;

/**
 * Created by Ben David on 01/08/2017.
 */

public class CpuMiningWorker extends Observable implements IMiningWorker
{
    private Console _console;
    private int _number_of_thread;
    private int _thread_priorirty;
    //private ExecutorService _exec;
    private Worker[] _workr_thread;
    private long _retrypause;
    private class EventList extends ArrayList<IWorkerEvent>
    {
        private static final long serialVersionUID = -4176908211058342478L;
        void invokeNonceFound(MiningWork i_work, int i_nonce)
        {
            MinyaLog.message("Nonce found! +"+((0xffffffffffffffffL)&i_nonce));
            Log.i("CpuMiningWorker", "Nonce found! +"+((0xffffffffffffffffL)&i_nonce));
            for(IWorkerEvent i: this){
                i.onNonceFound(i_work,i_nonce);
            }
        }
    }
    private class Worker extends Thread implements Runnable
    {
        CpuMiningWorker _parent;

        MiningWork _work;
        int _start;
        int _step;
        public long number_of_hashed;
        public Worker(CpuMiningWorker i_parent)
        {
            this._parent=i_parent;
        }
        /**
         * nonce計算のパラメータを指定する。
         * スレッドはi_startを起点にi_startづつnonceを増加させて計算する。
         * @param i_work
         * @param i_start
         * @param i_step
         */
        public void setWork(MiningWork i_work,int i_start,int i_step)
        {
            this._work=i_work;
            this._start=i_start;
            this._step=i_step;
        }
        private final static int NUMBER_OF_ROUND=1; //Original: 100
        public volatile boolean running = false;

        @Override
        public void run()
        {
            //Log.i("CpuMiningWorker","run()");
            running = true;
            //ここにハッシュ処理を書く
            this.number_of_hashed=0;
            try{
                //初期nonceの決定
                int nonce=this._start;
                MiningWork work=this._work;
                Hasher hasher = new Hasher();
                //めんどくさいので計算は途中で止めない
                byte[] target=work.target.refHex();
                while(true){
                    //Log.i("CpuMiningWorker","run(), while loop");
                    for(long i=NUMBER_OF_ROUND-1;i>=0;i--){
                        byte[] hash = hasher.hash(work.header.refHex(), nonce);
                        //nonceのチェック
                        for (int i2 = hash.length - 1; i2 >= 0; i2--) {
                            //Log.i("for loop in while", "iteration: i2= " + i2 );
                            if ((hash[i2] & 0xff) > (target[i2] & 0xff)){
                                Log.i("hash[i2] & 0xff", "first condition, iteration: i= " + i + " i2= " + i2);
                                break;
                            }
                            if ((hash[i2] & 0xff) < (target[i2] & 0xff)){
                                //発見!
                                Log.i("hash[i2] & 0xff", "second condition, iteration: i=" + i + " i2= " + i2);
                                this._parent._as_listener.invokeNonceFound(work,nonce);
                                break;
                            }
                        }
                        nonce+=this._step;
                    }
                    this.number_of_hashed+=NUMBER_OF_ROUND;
                    Log.i("number_of_hashed", "" + number_of_hashed);
                    Thread.sleep(10L);
                    //running = false;
                }
            } catch (GeneralSecurityException e){
                e.printStackTrace();
                setChanged();
                notifyObservers(Notification.SYSTEM_ERROR);
                try {
                    stopWork();
                } catch (MinyaException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e) {
                //Shutdownのハンドリング
                //running = false;
                MinyaLog.debug("Thread killed. Hashes= "+this.number_of_hashed);
                Log.d("CpuMiningWorker", "Thread killed. Hashes= "+this.number_of_hashed);
                _console.write("Thread killed. #Hashes="+this.number_of_hashed);
                calcSpeedPerThread(number_of_hashed);
                _last_time=System.currentTimeMillis();
            }
        }
    }
//    public CpuMiningWorker()
//    {
//        this(Runtime.getRuntime().availableProcessors(),DEFAULT_RETRYPAUSE,DEFAULT_PRIORITY,null);
//    }
    public void calcSpeedPerThread(long numOfHashes) {
        Log.i("calcSpeedHashes: = ", "" + numOfHashes);
        long curr_time =  System.currentTimeMillis();
        double delta_time = Math.max(1,curr_time-this._last_time)/1000.0;
//        _last_time = curr_time;
        Log.i("delta_time_double = ", "" + delta_time);
        Log.i("calcSpeedPerThread","speed_calc_double= " + numOfHashes/delta_time);
        double speed_calc = ((double)numOfHashes/delta_time);
        Log.i("speed_calc = ", "" + speed_calc);
        _speed=(double)speed_calc;
        setChanged();
        notifyObservers(Notification.SPEED);

    }

    public CpuMiningWorker(int i_number_of_thread , long retry_pause, int priority,Console console)
    {
        _console = console;
        _thread_priorirty = priority;
        this._retrypause = retry_pause;
        this._number_of_thread=i_number_of_thread;
        this._workr_thread=new Worker[10];
        //Threadの生成
        for(int i=this._number_of_thread-1;i>=0;i--){
            this._workr_thread[i]=new Worker(this);
        }

    }
    private long _last_time=0;
    private long _num_hashed=0;
    private long _tot_hashed=0;
    private double _speed = 0;

    @Override
    public boolean doWork(MiningWork i_work) throws MinyaException
    {
        MinyaLog.debug("Start doWork");
        Log.d("CpuMiningWorker","Start doWork");
        if(i_work!=null){
        //if(this._exec!=null){
            //実行中なら一度すべてのワークを停止
            this.stopWork();
            long hashes=0;
            for(int i=this._number_of_thread-1;i>=0;i--){
                hashes+=this._workr_thread[i].number_of_hashed;
//                Log.i("CpuMiningWorker","hashes: " + this._workr_thread[i].number_of_hashed);
            }
            Log.i("CpuMiningWorker","Hashes: " + hashes);
            //ハッシュレートの計算
            _num_hashed = hashes;
            _tot_hashed += _num_hashed;
            double delta_time = Math.max(1,System.currentTimeMillis()-this._last_time)/1000.0;
            Log.i("delta_time = ", "" + delta_time);
            double speed_calc =((double)_num_hashed/delta_time);
            Log.i("speed_calc = ", "" + speed_calc);
            _speed=speed_calc;
            setChanged();
            notifyObservers(Notification.SPEED);
            MinyaLog.message("Calculated "+ (_speed)+ " Hash/s");
            Log.i("CpuMiningWorker","Calculated "+ (_speed)+ " Hash/s");

        }
        this._last_time=System.currentTimeMillis();
        //Executerの生成
        //this._exec= Executors.newFixedThreadPool(this._number_of_thread);
//        this._last_hashed = this._num_hashed;
        for(int i=this._number_of_thread-1;i>=0;i--){
            this._workr_thread[i] = null;
            System.gc();
            this._workr_thread[i]=new Worker(this);
        }
        for(int i=this._number_of_thread-1;i>=0;i--){
            //Set Work:
            this._workr_thread[i].setWork(i_work,(int)i,this._number_of_thread);
            // Set Priority:
            _workr_thread[i].setPriority(_thread_priorirty);
            //Check if thread already started:
            if (_workr_thread[i].isAlive() == false) {
                //Log.i("isAlive()", " = false, about to  _workr_thread[i].start();");
                try {
                    _workr_thread[i].start();
                } catch (IllegalThreadStateException e){
                    _workr_thread[i].interrupt();
                }
            }
//            if (!_workr_thread[i].running) {
//                _workr_thread[i].start();
//                //this._exec.execute(this._workr_thread[i]);
//            }
        }

        return true;
    }
    @Override
    public void stopWork() throws MinyaException {
        for (Worker t : _workr_thread) {
            if (t != null) {
                Log.i("LC", "Worker: Killing thread ID: " + t.getId());
                _console.write("Worker: Killing thread ID: " + t.getId());
                t.interrupt();
//                Log.i("LC", "Worker: thread ID: " + t.getId() + " isAlive() = " + t.isAlive());
//                if (t.getState() == Thread.State.NEW) {
//                    t.running = false;
//                }
            }
        }
        this._console.write("Worker: Threads killed");
//        int threads = Thread.activeCount();
////        int threads = Thread.getAllStackTraces().size();
////        while (threads > 0) {
//            Log.d("CpuMiningWorker", "Number of threads: " + threads);
//            Thread.currentThread().setPriority(MIN_PRIORITY);
//            _exec.shutdown();
////        this._exec.shutdownNow();
//            //キャンセルの一斉送信
//            try {
//                // Wait a while for existing tasks to terminate
//                if (!_exec.awaitTermination(60, TimeUnit.SECONDS)) {
//                    _exec.shutdownNow(); // Cancel currently executing tasks
//                    // Wait a while for tasks to respond to being cancelled
//                    if (!_exec.awaitTermination(60, TimeUnit.SECONDS))
//                        System.err.println("Pool did not terminate");
//                        //threads = Thread.activeCount();
//                }
//                threads = Thread.activeCount();
//            } catch (InterruptedException ie) {
//                // (Re-)Cancel if current thread also interrupted
//                _exec.shutdownNow();
//                // Preserve interrupt status
//                Thread.currentThread().interrupt();
//                threads = Thread.activeCount();
//            }
//        }
//        while (!_exec.isTerminated()) {
//            try {
//                _exec.shutdownNow();
//                for (Thread t : _workr_thread) {
//                    if (t != null) {
//
//                        Log.i("LC", "Worker: Killing thread ID: "+t.getId());
//                        t.interrupt();
//                    }
//
//                }
//                //停止待ち
////            this._exec.awaitTermination(1500, TimeUnit.MILLISECONDS);
////            Thread.sleep(5000);
//                this._exec.awaitTermination(30, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                //throw new MinyaException(e);
//                // (Re-)Cancel if current thread also interrupted
//                _exec.shutdownNow();
//                // Preserve interrupt status
//                //Thread.currentThread().interrupt();
//                Log.i("CpuMiningWorker", "Caught InterruptedException");
//                throw new MinyaException(e);
//            }
////        }
//        this._exec=null;
    }

    @Override
    public int getProgress()
    {
        return 0;
    }

    @Override
    public long getNumberOfHash()
    {
        Log.i("CpuMiningWorker","getNumberOfHash() = " + _tot_hashed);
        return _tot_hashed;
        //return 0;
    }

    public double get_speed() {
    return _speed;
    }

    public boolean getThreadsStatus() {
        for (Worker t : _workr_thread) {
            if (t != null) {
                if (t.isAlive() == true) return true;
            }
        }
        return false;
    }

    public void ConsoleWrite(String c) {
        _console.write(c);
    }


    private EventList _as_listener=new EventList();

    /**
     * この関数は非同期コールスレッドと衝突するので{@link #doWork(MiningWork)}前に実行する事。
     */
    public void addListener(IWorkerEvent i_listener)
    {
        this._as_listener.add(i_listener);
        return;
    }
}