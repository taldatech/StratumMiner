package com.example.android.stratumminer;

/**
 * Created by Ben David on 01/08/2017.
 */

public class StratumMiningWork extends MiningWork
{
    public String job_id;
    public String xnonce2;
    public StratumMiningWork(HexArray i_data,HexArray i_target, String i_job_id, String i_xnonce2)
    {
        super(i_data,i_target);
        this.job_id=i_job_id;
        this.xnonce2=i_xnonce2;
        return;
    }
}