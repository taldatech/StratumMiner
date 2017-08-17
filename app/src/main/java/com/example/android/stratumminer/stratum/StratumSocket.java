package com.example.android.stratumminer.stratum;

import com.example.android.stratumminer.MinyaException;
import com.example.android.stratumminer.MinyaLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * Created by Ben David on 01/08/2017.
 */

public class StratumSocket extends Socket
{
    private class LoggingWriter extends BufferedWriter
    {
        public LoggingWriter(Writer arg0) {
            super(arg0);
        }
        @Override
        public void write(String str) throws IOException
        {
            super.write(str);
            MinyaLog.debug("TX>"+str);
        }
    }
    private class LoggingReader extends BufferedReader
    {
        public LoggingReader(Reader arg0)
        {
            super(arg0);
        }
        public String readLine() throws IOException
        {
            String s=super.readLine();
            MinyaLog.debug("RX<"+s);
            return s;
        }
    }
    private BufferedWriter _tx;
    private BufferedReader _rx;
    private int _id;

    protected StratumSocket()
    {
    }
    public StratumSocket(URI i_url) throws UnknownHostException, IOException
    {
        super(i_url.getHost(),i_url.getPort());
        this._tx = new LoggingWriter(new OutputStreamWriter(this.getOutputStream()));
        this._rx = new LoggingReader(new InputStreamReader(this.getInputStream()));
        this._id=1;
    }
    public long subscribe(String i_agent_name) throws IOException
    {
        long id;
        synchronized(this)
        {
            id=this._id;
            this._id++;
        }
//        this._tx.write("{\"id\": "+id+", \"method\": \"mining.subscribe\", \"params\": [\""+i_agent_name+"\"]}\n");
        this._tx.write("{\"id\": "+id+", \"method\": \"mining.subscribe\", \"params\": []}\n");
        this._tx.flush();
        return id;
    }
    public long authorize(String i_user,String i_password) throws IOException
    {
        long id;
        synchronized(this)
        {
            id=this._id;
            this._id++;
        }
        this._tx.write("{\"id\": "+id+", \"method\": \"mining.authorize\", \"params\": [\""+i_user+"\",\""+i_password+"\"]}\n");
        this._tx.flush();
        return id;
    }
    public long submit(int i_nonce,String i_user,String i_jobid,String i_nonce2,String i_ntime) throws IOException
    {
        long id;
        synchronized(this)
        {
            id=this._id;
            this._id++;
        }
        String sn=String.format("%08x",(
                ((i_nonce & 0xff000000)>>24)|
                        ((i_nonce & 0x00ff0000)>>8)|
                        ((i_nonce & 0x0000ff00)<<8)|
                        ((i_nonce & 0x000000ff)<<24)));
        // {"method": "mining.submit", "params": ["nyajira.xa", "e4c", "00000000", "52b7a1a9", "79280100"], "id":4}
        String s="{\"id\": "+id+", \"method\": \"mining.submit\", \"params\": [\""
                +i_user+"\", \""+i_jobid+"\",\""+i_nonce2+"\",\""+i_ntime+"\",\""+sn+"\"]}\n";
        this._tx.write(s);
        this._tx.flush();
        return id;
    }
    public StratumJson recvStratumJson() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn=mapper.readTree(this._rx.readLine());
        //parse method
        try {
            return new StratumJsonMethodGetVersion(jn);
        } catch (MinyaException e) {}
        try {
            return new StratumJsonMethodMiningNotify(jn);
        } catch (MinyaException e) {}
        try {
            return new StratumJsonMethodReconnect(jn);
        } catch (MinyaException e) {}
        try {
            return new StratumJsonMethodSetDifficulty(jn);
        } catch (MinyaException e) {}
        try {
            return new StratumJsonMethodShowMessage(jn);
        } catch (MinyaException e) {}
        //parse result(複雑なものから順にね！)
        try {
            return new StratumJsonResultSubscribe(jn);
        } catch (MinyaException e) {}
        try {
            return new StratumJsonResultStandard(jn);
        } catch (MinyaException e) {}
        return null;
    }
    public static void main(String[] args)
    {
        //JsonParseTest
        ObjectMapper mapper = new ObjectMapper();
        try {
            StratumJson s1=new StratumJsonMethodGetVersion(mapper.readTree(StratumJsonMethodGetVersion.TEST_PATT));
            StratumJson s2=new StratumJsonMethodMiningNotify(mapper.readTree(StratumJsonMethodMiningNotify.TEST_PATT));
            StratumJson s3=new StratumJsonMethodReconnect(mapper.readTree(StratumJsonMethodReconnect.TEST_PATT));
            StratumJson s4=new StratumJsonMethodSetDifficulty(mapper.readTree(StratumJsonMethodSetDifficulty.TEST_PATT));
            StratumJson s5=new StratumJsonMethodShowMessage(mapper.readTree(StratumJsonMethodShowMessage.TEST_PATT));
            StratumJson s6=new StratumJsonResultStandard(mapper.readTree(StratumJsonResultStandard.TEST_PATT));
            StratumJson s7=new StratumJsonResultSubscribe(mapper.readTree(StratumJsonResultSubscribe.TEST_PATT));
            return;
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MinyaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}