package com.example.android.stratumminer;

/**
 * Created by Ben David on 01/08/2017.
 */

public class MiningWork
{
    public final HexArray data;		//big endian
    public final HexArray target;	//big endian
    public final HexArray header;	//little endian
    private static byte[] headerByData(byte[] data)
    {
        byte[] h = new byte[80];
        for (int i = 0; i < 80; i += 4) {
            h[i]     = data[i + 3];
            h[i + 1] = data[i + 2];
            h[i + 2] = data[i + 1];
            h[i + 3] = data[i];
        }
        return h;
    }
    public MiningWork(HexArray i_data,HexArray i_target)
    {
        this.data=i_data;
        this.target=i_target;
        this.header=new HexArray(headerByData(i_data.refHex()));
        return;
    }
    public void dump()
    {
        System.out.println("data:"+this.data.getStr());
        System.out.println("target:"+this.target.getStr());
    }
    /**
     * This function makes submitdata from current work and nonce value.
     * @param i_nonce
     * @return
     */
    public String makeSubmitData(int i_nonce)
    {
        byte[] d = this.data.refHex().clone();
        d[79] = (byte) (i_nonce >>  0);
        d[78] = (byte) (i_nonce >>  8);
        d[77] = (byte) (i_nonce >> 16);
        d[76] = (byte) (i_nonce >> 24);
        return new HexArray(d).getStr();
    }
    public final static int INDEX_OF_PREVHASH	=4;							//32byte
    public final static int INDEX_OF_MARKERTOOT	=INDEX_OF_PREVHASH+32;		//32byte
    public final static int INDEX_OF_NTIME		=INDEX_OF_MARKERTOOT+32;	//4byte
    public final static int INDEX_OF_NBIT 		=INDEX_OF_NTIME+4;			//4byte
}