package com.sorry.stalker.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sorry on 2016/5/19.
 */
public class Downloader {

    public final static int DOWNLOADEDTORRENT = 0x11;

    public static void _downloadAsyn(final String url, final String destFileDir, final OkHttpClient client, final Handler handler)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Download","failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try
                {
                    is = response.body().byteStream();
                    Log.i("dir",destFileDir);
                    File dir = new File(destFileDir);
                    if (!dir.exists()) {
                        try {
                            //按照指定的路径创建文件夹
                            dir.mkdirs();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                    File file = new File(destFileDir, getFileName(url));
                    if (!file.exists()) {
                        try {
                            //在指定的文件夹中创建文件
                            file.createNewFile();
                        } catch (Exception e) {
                        }
                    }
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    Log.i("Download","done");
                    Message msg = new Message();
                    msg.what = DOWNLOADEDTORRENT;
                    msg.obj = getFileName(url);
                    handler.sendMessage(msg);
                } catch (IOException e)
                {
                    Log.i("Download",e.toString());
                }
            }
        });
    }

    public static String getFileName(String url){
        return url.split("title=")[1]+".torrent";
    }
}
