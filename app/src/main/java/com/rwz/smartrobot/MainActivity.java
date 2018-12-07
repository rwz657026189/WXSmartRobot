package com.rwz.smartrobot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);


    }

    public void onClick(View view) {
        // Note: 需要保证server端配置的keep-alive timeout 为60s
       new Thread(){
           @Override
           public void run() {
               OkHttpClient client = new OkHttpClient.Builder()
                       .retryOnConnectionFailure(false)
                       .build();
               int count = 0;
               try {
                   for (int i = 0; i != 10; i++) {
                       count = i;
                       Response response = client.newCall(new Request.Builder()
                               .url("http://192.168.50.210:7080/common/version/demo")
                               .get()
                               .build()).execute();
                       try {
                           System.out.println(response.body().string());
                       } finally {
                           response.close();
                       }
                       Thread.sleep(61000);
                   }
               } catch (Exception e) {
                   e.printStackTrace();
                   Log.e(TAG, "run: count = " +  count);
               }
           }
       }.start();
    }



    public void onClick2(View view) {
        OkHttpManager.getInstance().request("你好", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                //{
                //	"emotion": {
                //		"robotEmotion": {
                //			"a": 0,
                //			"d": 0,
                //			"emotionId": 0,
                //			"p": 0
                //		},
                //		"userEmotion": {
                //			"a": 0,
                //			"d": 0,
                //			"emotionId": 10300,
                //			"p": 0
                //		}
                //	},
                //	"intent": {
                //		"actionName": "",
                //		"code": 10004,
                //		"intentName": ""
                //	},
                //	"results": [{
                //		"groupType": 1,
                //		"resultType": "text",
                //		"values": {
                //			"text": "仅仅是一个打招呼，我能开心好久好久"
                //		}
                //	}]
                //}
                Log.d(TAG, "onResponse: " + string);
            }
        });

    }


}
