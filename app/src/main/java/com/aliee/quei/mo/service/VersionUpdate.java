package com.aliee.quei.mo.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;

import com.aliee.quei.mo.BuildConfig;
import com.aliee.quei.mo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class VersionUpdate extends Service {
    private static final int TIMEOUT = 10 * 1000;// 超时
    private static String down_url = null;

    private static final int DOWN_OK = 1;
    private static final int DOWN_ERROR = 0;
    private int downloadSize;
    private String app_name;

    private Intent updateIntent;
    private PendingIntent pendingIntent;

    private int notification_id = 0;
    NotificationCompat.Builder mBuilder;
    NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateIntent = intent;
        app_name = intent.getStringExtra("appName");
        down_url = intent.getStringExtra("down_url");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 创建文件
        createFile(app_name);
        initNotify();
        createThread();
        //download(down_url, updateFile.toString());
        // 设置下载过程中，点击通知栏，回到主界面
//       // updateIntent = new Intent(this, SettingActivity.class);
//        pendingIntent = PendingIntent.getActivity(this, 0, updateIntent,0);
        return super.onStartCommand(intent, flags, startId);

    }

    private void initNotify() {
        mBuilder = new NotificationCompat.Builder(this, "updateApk");
        mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setContentIntent(getDefalutIntent(0))
                // .setNumber(number)//显示数量
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                // .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                // .setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                // Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
                // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "updateApk";
            String channelName = "版本更新";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);

        }
    }

    public void showNotify(String status, int progress) {
        mBuilder.setContentTitle(getApplication().getResources().getString(R.string.app_name));
        mBuilder.setContentText(status + (progress + "%"));
        mBuilder.setProgress(100, progress, false);
        notificationManager.notify(notification_id, mBuilder.build());
    }

    /***
     * 开线程下载
     */
    public void createThread() {
        /***
         * 更新UI
         */
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWN_OK:
//                        Uri uri = Uri.fromFile(updateFile);
                        //下载完成后，弹出安装界面
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
//                        intent1.setDataAndType(uri, "application/vnd.android.package-archive");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT< Build.VERSION_CODES.O) {
                            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri contentUri = FileProvider.getUriForFile(VersionUpdate.this, BuildConfig.APPLICATION_ID + ".file.provider", updateFile);
                            intent1.setDataAndType(contentUri, "application/vnd.android.package-archive");
                        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            //Android 8.0及以上
                            //判断是否开启未知来源apk安装权限 true-开启 false-没开启
                            boolean haveInstallPermission = VersionUpdate.this.getPackageManager().canRequestPackageInstalls();
                            Uri packageURI = Uri.parse("package:" + VersionUpdate.this.getPackageName());
                            if (!haveInstallPermission){
                                //未开启，跳转至系统开启界面
                                Intent intent2 = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
                                startActivity(intent2);
                            }else {
                                Uri apkUri = FileProvider.getUriForFile(VersionUpdate.this, BuildConfig.APPLICATION_ID + ".file.provider", updateFile);
                                intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent1.setDataAndType(apkUri, "application/vnd.android.package-archive");
                            }

                        } else{
                            Uri uri = getApkUri(updateFile);
                            intent1.setDataAndType(uri, "application/vnd.android.package-archive");
//                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }

                        notificationManager.cancel(notification_id);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);

                        //更新通知信息，并添加点击意图
                        //  showNotify("点击安装", 100);
                        //showCustomProgressNotify("点击安装", 100);
                        // 下载完成，点击安装
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            Uri contentUri = FileProvider.getUriForFile(VersionUpdate.this, BuildConfig.APPLICATION_ID + ".fileprovider", updateFile);
//                            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//                        } else {
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//                        }
//                      //  intent.setDataAndType(uri, "application/vnd.android.package-archive");
//                        pendingIntent = PendingIntent.getActivity(VersionUpdate.this, 0, intent, 0);
//                        notificationManager.cancel(notification_id);
//                        mBuilder.setContentIntent(pendingIntent);
//                        notificationManager.notify(notification_id, mBuilder.build());
                        stopService(updateIntent);
                        break;
                    case DOWN_ERROR:
                        showNotify("下载失败", 100);
                        break;
                    default:
                        stopService(updateIntent);
                        break;
                }

            }

        };

        final Message message = new Message();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    long downloadSize = downloadUpdateFile(down_url, updateFile.toString());
                    if (downloadSize > 0) {
                        // 下载成功
                        message.what = DOWN_OK;
                        showNotify("下载完成", 100);
               /*         mBuilder.setContentText("下载完成");
                        showCustomProgressNotify("下载完成", 100);*/
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = DOWN_ERROR;
                    handler.sendMessage(message);
                }

            }
        }).start();
    }

    private static Uri getApkUri(File apkFile) {
//        Log.d("VersionUpdate", apkFile.toString());
        //如果没有设置 SDCard 写权限，或者没有 SDCard,apk 文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        Uri uri = Uri.fromFile(apkFile);
//        Log.d("VersionUpdate", uri.toString());
        return uri;
    }

    public void download(String down_url, final String file) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(down_url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                //  String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    //  File file = new File(saveDir, saveName );
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        showNotify("下载中", progress);
                    }
                    fos.flush();
                    // listener.onDownloadSuccess();
                } catch (Exception e) {
                    // listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }


    /***
     * 下载文件
     *
     * @return
     */
    public long downloadUpdateFile(String down_url, String file) throws Exception {
        int down_step = 1;// 提示step
        int totalSize;// 文件总大小
        int downloadCount = 0;// 已经下载好的大小
        int updateCount = 0;// 已经上传的文件大小
        InputStream inputStream;
        OutputStream outputStream;

        URL url = new URL(down_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        // 获取下载文件的size
        totalSize = httpURLConnection.getContentLength();
        if (httpURLConnection.getResponseCode() == 404) {
            throw new Exception("fail!");
        }
        inputStream = httpURLConnection.getInputStream();
        outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
        byte[] buffer = new byte[2048];
        int readsize = 0;
        /*  while ((readsize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readsize);
            downloadCount += readsize;// 时时获取下载到的大小

            LogUtil.showLog("msg---downloadCount:" + readsize);

            *//**
         * 每次增张5%
         *//*
            if (updateCount == 0 || (downloadCount * 100 / totalSize - down_step) >= updateCount) {
                updateCount += down_step;
                showNotify("下载中", updateCount);
*//*                mBuilder.setContentTitle("下载中");
                showCustomProgressNotify("下载中", updateCount);*//*
            }
        }*/

        while ((readsize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readsize);
            downloadCount += readsize;
            int progress = (int) (downloadCount * 1.0f / totalSize * 100);
            // 下载中
            // showNotify("下载中", progress);
        }
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        inputStream.close();
        outputStream.close();
        return downloadCount;

    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    public static File updateDir = null;
    public static File updateFile = null;

    /***
     * 创建文件
     */
    public static void createFile(String name) {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory() + "/data/com.pipi.comic/apk/");
            updateFile = new File(updateDir + "/" + name + ".apk");
            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}