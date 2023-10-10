package com.pharos.walker.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.constants.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (isTokenExpired(response)) {//根据和服务端的约定判断token过期
            Log.e("okhttps", "intercept: 静默自动刷新Token,然后重新请求数据" );
            //同步请求方式，获取最新的Token
            String newSession = getToken();
            //使用新的Token，创建新的请求
            Request newRequest = chain.request()
                    .newBuilder()
                    .addHeader("Authorization","Bearer " +newSession)
                    .build();
             //重新请求
            return chain.proceed(newRequest);
        }
        return response;
    }
    private boolean isTokenExpired(Response response) {
        if (response.code() == 401) {
            return true;
        }
        return false;
    }
//    private boolean isTokenExpired(Response response) throws IOException {
//        String result = response.body().string();
//        JSONObject toJsonObj;
//        try {
//            toJsonObj = new JSONObject(result);
//            int code = toJsonObj.getInt("code");
//            if (code == 401)
//                return true;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return true;
//        }
//       return false;
//    }
//
    public static String getToken(){
        String result = OkHttpUtils.getSyncString(Api.tokenUrl + "?grant_type=client_credentials");
        TokenBean tokenBean = new Gson().fromJson(result,TokenBean.class);
        if (tokenBean.getCode() == 0){
            SPHelper.saveToken(tokenBean.getData().getAccess_token());
            return tokenBean.getData().getAccess_token();
        }
        return "";
    }
}
