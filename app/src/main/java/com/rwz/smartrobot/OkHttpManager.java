package com.rwz.smartrobot;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpManager {

    private static OkHttpManager instance;

    public static OkHttpManager getInstance() {
        if(instance == null)
            synchronized (OkHttpManager.class) {
                if (instance == null) {
                    instance = new OkHttpManager();
                }
            }
        return instance;
    }

    private OkHttpClient client;

    private OkHttpManager() {
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
    }

    public void request(String content, Callback callback) {
        //https://www.kancloud.cn/turing/www-tuling123-com/718227
        String json = "{\n" +
                "\t\"reqType\":0,\n" +
                "    \"perception\": {\n" +
                "        \"inputText\": {\n" +
                "            \"text\": \"%1$s\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"userInfo\": {\n" +
                "        \"apiKey\": \"c992d0f37fbc4c629a331cc0de204f5c\",\n" +
                "        \"userId\": \"357235\"\n" +
                "    }\n" +
                "}";
        RequestBody body = FormBody.create(MediaType.parse("application/json"), String.format(json, content));
        Request request = new Request.Builder()
                .url("http://openapi.tuling123.com/openapi/api/v2")
                .post(body).build();
        client.newCall(request).enqueue(callback);
    }

}
