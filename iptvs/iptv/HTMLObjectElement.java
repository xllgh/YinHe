package com.yinhe.android.iptv;

import android.content.Context;
import android.util.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.net.Uri;
import org.json.JSONArray;
import java.util.Iterator;
import android.content.ComponentName;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

@JNINamespace("content::iptv")
public class HTMLObjectElement {
    private static final String TAG = "HTMLObjectElement";
    private static Context applicationContext = null;

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        if (applicationContext != null) {
            return;
        }

        applicationContext = context.getApplicationContext();
    }

    public static void destory() {
        applicationContext = null;
    }

    //public List readJsonStream(InputStream in) throws IOException {
    //    JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
    //    try {
    //        return readMessagesArray(reader);
    //    }
    //    finally {
    //        reader.close();
    //    }    
    //}

    //public List readMessagesArray(JsonReader reader) throws IOException {
    //    List messages = new ArrayList();

    //    reader.beginArray();
    //    while (reader.hasNext()) {
    //    messages.add(readMessage(reader));
    // }
    // reader.endArray();
    // return messages;
    //}

    @CalledByNative
    private static boolean bagJson(String params) {
        Log.d(TAG, "bagJson");
        if (applicationContext == null) {
            Log.d(TAG, "applicationContext=false");
            return false;
        }
        
        if (params == null || params.isEmpty()) {
            Log.d(TAG, "params=null");
            return false;
        }

        Intent mintent=new Intent();
        //start other 
        mintent.setClassName("com.yinhe.yhJVM", "com.yinhe.yhJVM.MainActivity");
        try {
            int count =0;
            JSONObject jsonObject = null;
            Iterator<String> iterator = null;
            jsonObject = new JSONObject(params);
            iterator = (Iterator<String>)jsonObject.keys();
            
            for(int i = 0;iterator.hasNext();++i){
                count++;
                String key = iterator.next();
                String value = jsonObject.getString(key);
                mintent.putExtra("key"+i, key);
                mintent.putExtra(key, value);
                Log.d(TAG,key);
                Log.d(TAG,value);
            }
            mintent.putExtra("keynum", count);
            mintent.putExtra("jadUrl", "http://172.16.92.1:8080/suite.jad");
            
        }catch (Exception e) {
            Log.d(TAG, "get json failed");
            return false;
        }

	    mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    ComponentName comp=new ComponentName("com.yinhe.yhJVM", "com.yinhe.yhJVM.MainActivity");
	    mintent.setComponent(comp);
	    mintent.setAction("android.intent.action.VIEW");
	    applicationContext.startActivity(mintent);
        createService();
        return true;
    }
    private static void createService(){        
        try{            
            ServerSocket serverSocket=null;
            //ServerSocket serverSocketInstall=null;
            serverSocket=new ServerSocket(6666);
            //serverSocketInstall=new ServerSocket(7777); 
            Socket socket=serverSocket.accept(); 
            //Socket socketInstall=serverSocketInstall.accept();
            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
            //DataInputStream dataInputStreamInstall=new DataInputStream(socketInstall.getInputStream());
            Log.d(TAG,"broswer:"+dataInputStream.readUTF());
            //Log.d(TAG,"broswer:"+dataInputStreamInstall.readUTF()); 
            //Log.d(TAG,"broswer port:"+socketInstall.getPort());
            
            dataInputStream.close();            
            socket.close();            
            serverSocket.close(); 
            //dataInputStreamInstall.close();            
            //socketInstall.close();            
            //serverSocketInstall.close();
           } catch(Exception e) {            
                Log.d(TAG,"broswer:createService false");        
           }    
    }
}
