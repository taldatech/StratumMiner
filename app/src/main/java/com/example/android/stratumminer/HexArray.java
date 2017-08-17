package com.example.android.stratumminer;

/**
 * Created by Ben David on 01/08/2017.
 */

public final class HexArray
{
    private byte[] _v;
    public HexArray(String i_s)
    {
        this._v=toByteArray(i_s);
    }
    /**
     * copy constructor
     * @param i_src
     */
    public HexArray(HexArray i_src)
    {
        this._v=new byte[i_src._v.length];
        System.arraycopy(i_src._v,0,this._v,0,i_src._v.length);
    }
    /**
     * 配列をインスタンスにアタッチして生成する。
     * @param i_attach_array
     */
    public HexArray(byte[] i_attach_array)
    {
        this._v=i_attach_array;
    }
    public HexArray(HexArray i_src, int i_start,int i_len)
    {
        this(new byte[i_len]);
        System.arraycopy(i_src._v,i_start,this._v,0,i_len);
    }
    public byte[] refHex()
    {
        return this._v;
    }
    public String getStr()
    {
        return toHexString(this._v);
    }
    /**
     * データの一部を文字列化する。
     * @param i_s
     * 開始インデクス
     * @param i_l
     * 長さ
     * @return
     */
    public String getStr(int i_s,int i_l)
    {
        return toHexString(this._v,i_s,i_l);
    }

    public void swapEndian(HexArray i_add)
    {
        byte[] v=this._v;
        for(int i=0;i<this._v.length;i+=4){
            byte x1=v[i+0];
            byte x2=v[i+1];
            v[i+0]=v[i+3];
            v[i+1]=v[i+2];
            v[i+2]=x2;
            v[i+3]=x1;
        }
        return;
    }

    /**
     * 配列を追記する。
     * @param i_add
     */
    public void append(HexArray i_add,int i_s,int i_len)
    {
        this.append(i_add._v,i_s,i_len);
        return;
    }
    public void append(HexArray i_add)
    {
        this.append(i_add._v,0,i_add._v.length);
        return;
    }
    public void append(byte[] i_add)
    {
        this.append(i_add,0,i_add.length);
    }
    public void append(byte[] i_add,int i_s,int i_len)
    {
        byte[] n=new byte[i_len+this._v.length];
        System.arraycopy(this._v,0,n,0,this._v.length);
        System.arraycopy(i_add,i_s,n,this._v.length,i_len);
        this._v=n;
        return;
    }

    private static byte[] toByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)+Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    private static String toHexString(byte[] b)
    {
        StringBuilder sb = new StringBuilder(80);
        for (int i = 0; i < b.length; i++){
            sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    /**
     * i_s番目からi_len個のデータを文字列化
     * @param b
     * @param i_s
     * @param i_len
     * @return
     */
    private static String toHexString(byte[] b,int i_s,int i_len)
    {
        StringBuilder sb = new StringBuilder(i_len*2);
        for (int i = 0; i < i_len; i++){
            sb.append(Integer.toString((b[i+i_s] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    public int getLength()
    {
        return this._v.length;
    }
    public void swapEndian() throws MinyaException
    {
        int l=this._v.length;
        byte[] v=this._v;
        if(l%4!=0){
            throw new MinyaException();
        }
        for(int i=0;i<l;i+=4){
            byte v0=v[i+0];
            byte v1=v[i+1];
            v[i+0]=v[i+3];
            v[i+1]=v[i+2];
            v[i+2]=v1;
            v[i+3]=v0;
        }
    }
}