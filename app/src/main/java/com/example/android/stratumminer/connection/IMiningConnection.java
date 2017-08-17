package com.example.android.stratumminer.connection;

import com.example.android.stratumminer.MiningWork;
import com.example.android.stratumminer.MinyaException;

/**
 * Created by Ben David on 01/08/2017.
 */

public interface IMiningConnection
{
    /**
     * コネクションから非同期イベントを受け取るオブジェクトを追加する。
     * {@link #connect()}実行前に設定する事。
     * @param i_listener
     * @throws MinyaException
     */
    public void addListener(IConnectionEvent i_listener) throws MinyaException;
    public MiningWork connect() throws MinyaException;
//    public void connect() throws MinyaException;
    public void disconnect() throws MinyaException;
    public MiningWork getWork() throws MinyaException;
    public void submitWork(MiningWork i_work, int i_nonce) throws MinyaException;
//    public boolean submitWork(MiningWork i_work, int i_nonce) throws MinyaException;
}