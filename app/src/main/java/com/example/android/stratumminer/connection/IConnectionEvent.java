package com.example.android.stratumminer.connection;

import com.example.android.stratumminer.MiningWork;

/**
 * Created by Ben David on 01/08/2017.
 */

public interface IConnectionEvent
{
    public void onNewWork(MiningWork i_new_work);
    public void onSubmitResult(MiningWork i_listener,int i_nonce,boolean i_result);

}
