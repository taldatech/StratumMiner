package com.example.android.stratumminer.worker;

import com.example.android.stratumminer.MiningWork;

/**
 * Created by Ben David on 01/08/2017.
 */

public interface IWorkerEvent
{
    public void onNonceFound(MiningWork i_work, int i_nonce);
}
