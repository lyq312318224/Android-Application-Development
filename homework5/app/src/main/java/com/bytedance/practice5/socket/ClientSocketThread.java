package com.bytedance.practice5.socket;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class ClientSocketThread extends Thread {
    public ClientSocketThread(SocketActivity.SocketCallback callback) {
        this.callback = callback;
    }

    private SocketActivity.SocketCallback callback;
    private boolean stopFlag = false;

    //head请求内容
    private static String content = "HEAD / HTTP/1.1\r\nHost:www.zju.edu.cn\r\n\r\n";
    private volatile String message = content;
    private synchronized void clearMsg() {
        this.message = "";
    }
    @Override
    public void run() {
        // TODO 6 用socket实现简单的HEAD请求（发送content）
        //  将返回结果用callback.onresponse(result)进行展示
        try {
            Socket socket =new Socket("www.zju.edu.cn",80);
            BufferedOutputStream os=new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream is=new BufferedInputStream(socket.getInputStream());
            double n=1;
            byte[] data=new byte[1024*5];
            int len=-1;
            while (!stopFlag&&socket.isConnected()){
                if(!message.isEmpty()){
                    Log.d("socket","客户端发送"+message);
                    os.write(message.getBytes());
                    os.flush();
                    clearMsg();
                    int receiveLen = is.read(data);
                    if (receiveLen != -1) {
                        String receive = new String(data, 0, receiveLen);
                        Log.d("socket", "客户端收到 " + receive);
                        callback.onResponse(receive);
                    } else {
                        Log.d("socket", "客户端收到-1");
                    }
                }
                sleep(300);
            }
            Log.d("socket","客户端断开");
            os.flush();
            os.close();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}