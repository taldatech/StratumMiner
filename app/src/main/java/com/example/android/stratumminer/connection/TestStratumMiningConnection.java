package com.example.android.stratumminer.connection;

import com.example.android.stratumminer.MiningWork;
import com.example.android.stratumminer.MinyaException;
import com.example.android.stratumminer.StratumMiningWork;
import com.example.android.stratumminer.stratum.StratumJsonMethodMiningNotify;
import com.example.android.stratumminer.stratum.StratumJsonMethodSetDifficulty;
import com.example.android.stratumminer.stratum.StratumJsonResultSubscribe;
import com.example.android.stratumminer.stratum.StratumWorkBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Ben David on 01/08/2017.
 */

public class TestStratumMiningConnection  implements IMiningConnection
{
    /**
     * サンプルデータの格納クラス
     */
    static class Dataset{
        String name;
        String sr;
        String nt;
        String st;
        int nonce;
        Dataset(String i_name,String i_sr,String i_nt,String i_st,int i_nonce)
        {
            this.name=i_name;
            this.sr=i_sr;
            this.nt=i_nt;
            this.st=i_st;
            this.nonce=i_nonce;
        }
    }
    final static Dataset[] data={
		/*DATASET1*/
            new Dataset(
			/*NAME*/ "ACCEPTABLE_PAT1",
			/*SR   */	"{\"error\": null, \"id\": 1, \"result\": [[\"mining.notify\", \"ae6812eb4cd7735a302a8a9dd95cf71f\"], \"f8002fb8\", 4]}",
			/*NT   */	"{\"params\": [\"e4c\", \"713aff93fae198b15732ff94f1b989f7e6290cee8587ad5c3277f79e381872f9\", \"01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff26026a0b062f503253482f04aca1b75208\", \"0d2f7374726174756d506f6f6c2f000000000140ace4dfd40100001976a914d2468dfebad54e0c5deac5a27bea66f79a61228788ac00000000\", [\"00a3da053c03e6d86a3d0c8b2e2f468dcdf43029c0f7e5b6fa2a148268cdea65\", \"f64d6bb298faf383a8e690e8cc13af27b932b7209286a3f89faff96b75d8ab28\", \"77f456dd6f8f0e1c2d2228f69029c79422c0dbbb4319f50e28a7298c291b932e\", \"0ae03db5b78634af9d018466ee4cbe0019ebd0fa5a3ff5f14114512869a6bf84\", \"bff35f65d8637f36bb393572e70960bffccde1507973712aa903c7315dc56263\", \"7aa88a90e04ffbfca7b703c2ca2ea8429ee8b1ff9feab5497d3ef1449f8f6bf2\", \"ed5c9dde6b5eab2e2134dde3accf2e09ddec81f7759a3b94c48238b547f8fbce\"], \"00000001\", \"1c23ccc0\", \"52b7a1a9\", false], \"id\": null, \"method\": \"mining.notify\"}",
			/*ST   */	"{\"params\": [64], \"id\": null, \"method\": \"mining.set_difficulty\"}",
			/*NONCE*/	0x00012879),
            //String WORK_DATA="00000001713aff93fae198b15732ff94f1b989f7e6290cee8587ad5c3277f79e381872f9d9b8ddd7ec14b8b5a955cb3801455367d2f63129cb749298bd922166f0fb7bd952b7a1a91c23ccc000000000000000800000000000000000000000000000000000000000000000000000000000000000000000000000000080020000";
            //String WORK_TARGET="000000000000000000000000000000000000000000000000000000fcff030000";
		/*DATASET2*/
            new Dataset(
			/*NAME*/ "ACCEPTABLE_NYN",
			/*SR   */	"{\"error\": null, \"id\": 1, \"result\": [[\"mining.notify\", \"ae6812eb4cd7735a302a8a9dd95cf71f\"], \"2800304b\", 4]}",
			/*NT   */	"{\"params\": [\"5138\", \"914ec264690182c3258a2850b4d5ff06eb82c896f1f9b7ae49354acbd3cbd856\", \"01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff2f026c53062f503253482f04442fd55208\", \"162f6e79616e2e6c75636b796d696e6572732e636f6d2f0000000001801edec8000000001976a914f7a5ba85e33ae57ce35ba539cafeb7a2eabe8ceb88ac00000000\", [], \"00000001\", \"1d050327\", \"52d52f15\", true], \"id\": null, \"method\": \"mining.notify\"}",
			/*ST   */	"{\"params\": [16], \"id\": null, \"method\": \"mining.set_difficulty\"}",
			/*NONCE*/	0x0000e16f),
            new Dataset(
				/*NAME*/ "ACCEPTABLE_NYN",
				/*SR   */	"{\"error\": null, \"id\": 1, \"result\": [[\"mining.notify\", \"ae6812eb4cd7735a302a8a9dd95cf71f\"], \"28003af1\", 4]}",
				/*NT   */	"{\"params\": [\"4dd9\", \"40a5e84bd5dd28dacea5e20c244febebc0cf7798feef17d4e71f7a3f0cfc74e9\", \"01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff3003f48800062f503253482f04dfacdb5208\", \"162f6e79616e2e6c75636b796d696e6572732e636f6d2f0000000001801edec8000000001976a914f7a5ba85e33ae57ce35ba539cafeb7a2eabe8ceb88ac00000000\", [], \"00000001\", \"1d14d81f\", \"52dbacab\", true], \"id\": null, \"method\": \"mining.notify\"}",
				/*ST   */	"{\"params\": [16], \"id\": null, \"method\": \"mining.set_difficulty\"}",
				/*NONCE*/	0x0003b038)

    };
    private MiningWork _work;
    public TestStratumMiningConnection(int i_idx)
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            StratumJsonResultSubscribe s=new StratumJsonResultSubscribe(mapper.readTree(data[i_idx].sr));
            StratumJsonMethodMiningNotify n=new StratumJsonMethodMiningNotify(mapper.readTree(data[i_idx].nt));
            StratumJsonMethodSetDifficulty d=new StratumJsonMethodSetDifficulty(mapper.readTree(data[i_idx].st));
            StratumWorkBuilder sb=new StratumWorkBuilder(s);
            sb.setDiff(d);
            sb.setNotify(n);
            this._work=sb.buildMiningWork();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MinyaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private boolean _is_connect=false;

    @Override
    public MiningWork connect() throws MinyaException
    {
        this._is_connect=true;
        return this._work;
    }

    @Override
    public void disconnect() throws MinyaException
    {
        this._is_connect=false;
        return;
    }

    @Override
    public MiningWork getWork()
    {
        return this._work;
    }

    @Override
    public void addListener(IConnectionEvent i_listener) throws MinyaException
    {
        // TODO Auto-generated method stub
    }
    private String _uid="NyanNyan!";

    @Override
    public void submitWork(MiningWork i_work, int i_nonce) throws MinyaException
    {
        //	[2014-01-14 21:35:40] > {"method": "mining.submit", "params": ["xxxx", "5138", "00000000", "52d52f15", "6fe10000"], "id":4}

        StratumMiningWork w=(StratumMiningWork)i_work;
        this.submit(
                i_nonce,
                this._uid,
                w.job_id,
                w.xnonce2,
                w.data.getStr(MiningWork.INDEX_OF_NTIME,4));
        return;
    }
    private int _id=1;
    private long submit(int i_nonce,String i_user,String i_jobid,String i_nonce2,String i_ntime)
    {
        String sn=String.format("%08x",(
                ((i_nonce & 0xff000000)>>24)|
                        ((i_nonce & 0x00ff0000)>>8)|
                        ((i_nonce & 0x0000ff00)<<8)|
                        ((i_nonce & 0x000000ff)<<24)));
        // {"method": "mining.submit", "params": ["xxxxx", "e4c", "00000000", "52b7a1a9", "79280100"], "id":4}
        String s="{\"id\": "+this._id+", \"method\": \"mining.submit\", \"params\": [\""
                +i_user+"\", \""+i_jobid+"\",\""+i_nonce2+"\",\""+i_ntime+"\",\""+sn+"\"]}\n";
        System.out.println(s);
        return 0;
    }

}
