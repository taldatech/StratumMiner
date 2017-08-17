package com.example.android.stratumminer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ben David on 01/08/2017.
 */

public class MinyaLog
{
    public static interface ILogOutput
    {
        final static int LV_DEBUG	=1;
        final static int LV_MESSAGE	=2;
        final static int LV_WARNING	=3;
        public void println(int level,String s);
    }
    public static class StandardOutput implements ILogOutput
    {
        public void println(int level,String s)
        {
            System.out.println(s);
            System.out.flush();
        }
    }
    private static ILogOutput output=new StandardOutput();
    public static void setOutput(ILogOutput p){
        MinyaLog.output=p;
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static String getDateHeader()
    {
        //現在日時を取得する
        Calendar c = Calendar.getInstance();
        //フォーマットパターンを指定して表示する
        return "["+sdf.format(c.getTime())+"]";
    }
    public static synchronized void message(String s)
    {
        MinyaLog.output.println(ILogOutput.LV_MESSAGE,getDateHeader()+s);
    }

    public static synchronized void warning(String s)
    {
        MinyaLog.output.println(ILogOutput.LV_WARNING,getDateHeader()+"Warning:"+s);
    }
    public static synchronized void debug(String s)
    {
        MinyaLog.output.println(ILogOutput.LV_DEBUG,getDateHeader()+"Debug:"+s);
    }
}
