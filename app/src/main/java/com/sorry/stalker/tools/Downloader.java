package com.sorry.stalker.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.frostwire.jlibtorrent.LibTorrent;
import com.frostwire.jlibtorrent.Priority;
import com.frostwire.jlibtorrent.Session;
import com.frostwire.jlibtorrent.TorrentAlertAdapter;
import com.frostwire.jlibtorrent.TorrentHandle;
import com.frostwire.jlibtorrent.alerts.BlockFinishedAlert;
import com.frostwire.jlibtorrent.alerts.StateChangedAlert;
import com.frostwire.jlibtorrent.alerts.StatsAlert;
import com.frostwire.jlibtorrent.alerts.TorrentFinishedAlert;
import com.frostwire.jlibtorrent.swig.libtorrent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sorry on 2016/5/19.
 */
public class Downloader {

    private final static int PLAYVIDEO = 0x3;
    private final static int GETSUBTITLE = 0x4;
    public final static int DOWNLOADEDTORRENT = 0x11;
    public final static int DOWNLOADEDSUBTITLE = 0x13;
    public final static int UPDATESPEED = 0x12;


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
                    Message msg = new Message();
                    File file = new File(destFileDir, getFileName(url,msg));
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


                    msg.obj = getFileName(url, msg);

                    handler.sendMessage(msg);
                } catch (IOException e)
                {
                    Log.i("Download",e.toString());
                }
            }
        });
    }

    public static String getFileName(String url, Message msg){

        if(url.split("title=").length != 1) {
            msg.what = DOWNLOADEDTORRENT;
            return url.split("title=")[1] + ".torrent";
        }
        else{

            msg.what = DOWNLOADEDSUBTITLE;
            String[] a = url.split("/");
            return url.split("/")[a.length-1];
        }

    }

    public static void downloadVideo(final String torrentName, final Session s , final TorrentHandle th, final Handler mainShowHandler){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                final CountDownLatch signal = new CountDownLatch(1);
                th.setSequentialDownload(true);
                for(int i = 0; i < th.getPiecePriorities().length; i++){
                    if(i <=5 ) {
                        th.piecePriority(i, Priority.SEVEN);
                    }
                    else if(i > 5 && i <= 10) {
                        th.piecePriority(i, Priority.SIX);
                    }
                    else if(i > 10 && i <= 20) {
                        th.piecePriority(i, Priority.FIVE);
                    }
                    else if(i >20 && i <= 30) {
                        th.piecePriority(i, Priority.FOUR);
                    }
                    else if(i >30 && i <= 50) {
                        th.piecePriority(i, Priority.THREE);
                    }
                    else if(i >50 && i <= 70) {
                        th.piecePriority(i, Priority.TWO);
                    }
                    else{
                        th.piecePriority(i, Priority.NORMAL);
                    }
                }

                s.addListener(new TorrentAlertAdapter(th) {
                    boolean played = false;
                    @Override
                    public void blockFinished(BlockFinishedAlert alert) {
                        int p = (int) (th.getStatus().getProgress() * 100);

                        //System.out.println("Progress: " + p + " for torrent name: " + alert.torrentName());
                        //System.out.println(s.getStats().download());
                        if(p > 1 && !played){
                            played = true;
                            Message msg = new Message();
                            msg.what = PLAYVIDEO;
                            msg.obj = "/storage/emulated/0/Stalker/torrent/" + alert.torrentName();
                            mainShowHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void stateChanged(StateChangedAlert alert) {
                        //System.out.println(th.getStatus().getState().toString());
                        super.stateChanged(alert);
                    }

                    @Override
                    public void stats(StatsAlert alert) {
                        if(th.getStatus().getProgressPpm()/10000 >= 10 && !played){
                            played = true;
                            Message msg = new Message();
                            msg.what = PLAYVIDEO;
                            msg.obj = "/storage/emulated/0/Stalker/torrent/" + alert.torrentName();
                            mainShowHandler.sendMessage(msg);
                        }
                        String progress = th.getStatus().getProgressPpm()/10000+"%";
                        String downloadSpeed = th.getStatus().getDownloadRate()/1024+ "KB/s";
                        Message msg = new Message();
                        msg.what = UPDATESPEED;
                        msg.obj = "缓冲中...\n"+ progress+" "+downloadSpeed;
                        mainShowHandler.sendMessage(msg);
                        System.out.println("Download Speed: " + downloadSpeed);
                        super.stats(alert);
                    }

                    @Override
                    public void torrentFinished(TorrentFinishedAlert alert) {
                        System.out.print("Torrent finished");
                        signal.countDown();
                    }
                });

                th.resume();

                try {
                    signal.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                libtorrent.default_storage_disk_write_access_log(false);

            }
        });
        thread.start();
    }
}
