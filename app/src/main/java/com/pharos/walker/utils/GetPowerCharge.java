package com.pharos.walker.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GetPowerCharge {

    private static final String TAG ="powercharge" ;

    /** 执行shell命令，
    /*  获取到“hi”是断开充电
    /*  获取到“lo”是接入充电
    /*  视图更新时，可根据自己业务定时执行
    */
    public static boolean getPowerCharge(){
        String[] cmd = {"/bin/sh","-c"," cat /d/gpio | grep 'gpio-94' |  awk '{print $6}'"};
        String charge = getString(cmd);
        if (charge.contains("lo")){
            return true;
        }else if (charge.contains("hi")){
            return false;
        }
        return false;
    }

    public static String getString(String[] commandStr) {
        Log.d(TAG, "command: "+commandStr);
        BufferedReader br = null;
        try {
            Runtime.getRuntime().exec("su");
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()/*, Charset.forName("GBK")*/));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            Log.d(TAG, "result:"+sb.toString());
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
