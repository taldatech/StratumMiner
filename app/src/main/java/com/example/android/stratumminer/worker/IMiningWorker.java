package com.example.android.stratumminer.worker;

import com.example.android.stratumminer.MiningWork;
import com.example.android.stratumminer.MinyaException;

/**
 * Created by Ben David on 01/08/2017.
 */

public interface IMiningWorker
{
    public enum Notification {
        SYSTEM_ERROR,
        PERMISSION_ERROR,
        CONNECTION_ERROR,
        AUTHENTICATION_ERROR,
        COMMUNICATION_ERROR,
        LONG_POLLING_FAILED,
        LONG_POLLING_ENABLED,
        CONNECTING,
        NEW_BLOCK_DETECTED,
        SPEED,
        NEW_WORK,
        POW_TRUE,
        POW_FALSE,
        TERMINATED
    };
    /**
     * ワークを開始します。既にワークを実行中の場合は一度すべてのワークをシャットダウンして再起動します。
     * 統計情報はリセットされます。
     * @throws MinyaException
     */
    public boolean doWork(MiningWork i_work) throws MinyaException;
    /**
     * 実行中のワークを停止します。
     * @throws MinyaException
     */
    public void stopWork() throws MinyaException;
    /**
     * 進行度を返します。
     * @return
     */
    public int getProgress();
    /**
     * {@link #doWork}が計算したハッシュの数を返します。
     * @return
     */
    public long getNumberOfHash();

    public void addListener(IWorkerEvent i_listener) throws MinyaException;
}
