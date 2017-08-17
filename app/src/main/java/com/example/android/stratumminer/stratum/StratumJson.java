package com.example.android.stratumminer.stratum;

import com.example.android.stratumminer.HexArray;
import com.example.android.stratumminer.MinyaException;

/**
 * Created by Ben David on 01/08/2017.
 */

public class StratumJson
{
    protected StratumJson()
    {
        return;
    }
    /**
     * コピーコンストラクタ
     * @param i_src
     */
    protected StratumJson(StratumJson i_src)
    {
        return;
    }
    /**
     * HEXArrayを文字サイズチェック付きでnewする。
     * @param i_str
     * @param i_str_len
     * @return
     * @throws MinyaException
     */
    protected static HexArray toHexArray(String i_str, int i_str_len) throws MinyaException
    {
        if (i_str.length() != i_str_len) {
            throw new MinyaException();
        }
        return new HexArray(i_str);
    }
}