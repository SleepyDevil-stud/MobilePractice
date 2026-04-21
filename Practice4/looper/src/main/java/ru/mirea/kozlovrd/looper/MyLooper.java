package ru.mirea.kozlovrd.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import java.util.logging.LogRecord;

    public class MyLooper extends Thread{
        public Handler mHandler;
        public Handler mainHandler;
        public MyLooper(Handler mainThreadHandler){
            mainHandler = mainThreadHandler;
        }
        public void run(){
            Log.d("MyLooper", "run");
            Looper.prepare();
            mHandler = new Handler(Looper.myLooper()){
                long startTime = System.currentTimeMillis();

                public void handleMessage(Message msg){
                    String data = msg.getData().getString("KEY");
                    Log.d("MyLooper get message", data);


                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", String.format("My age is: %d", System.currentTimeMillis() - startTime));
                    bundle.putString("work", String.format("I work as %s", data));
                    message.setData(bundle);
                    mainHandler.sendMessage(message);
                }
            };
            Looper.loop();

        }
    }
