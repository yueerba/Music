package com.music.lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.content.Context;
import android.util.Log;

import com.music.base.IConstants;

public class LyricDownloadManager {
    private static final String TAG = "dingfeng";

    public static final String GB2312 = "GB2312";
    public static final String UTF_8 = "utf-8";
    private final int mTimeOut = 10 * 1000; // 10s
    private LyricXMLParser mLyricXMLParser = new LyricXMLParser();
    private URL mUrl = null;
    private int mDownloadLyricId = -1;

    // private Context mContext = null;

    public LyricDownloadManager(Context c) {
        // mContext = c;
    }

    /*
     * 根据歌曲名和歌手名取得该歌的XML信息文件 返回歌词保存路径
     */
    public String searchLyricFromWeb(String musicName, String singerName, String oldMusicName) {
        Log.i(TAG, "下载前，歌曲名:" + musicName + ",歌手名:" + singerName);
        // 传进来的如果是汉字，那么就要进行编码转化
        try {
            musicName = URLEncoder.encode(musicName, UTF_8);
            singerName = URLEncoder.encode(singerName, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 百度音乐盒的API
        String strUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + musicName + "$$" + singerName + "$$$$";

        // 生成URL
        try {
            mUrl = new URL(strUrl);
            Log.i(TAG, "请求获取歌词信息的URL：" + mUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection httpConn = (HttpURLConnection) mUrl.openConnection();
            httpConn.setReadTimeout(mTimeOut);
            if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "http连接失败");
                return null;
            }
            httpConn.connect();
            Log.i(TAG, "http连接成功");

            // 将百度音乐盒的返回的输入流传递给自定义的XML解析器，解析出歌词的下载ID
            mDownloadLyricId = mLyricXMLParser.parseLyricId(httpConn.getInputStream());
            httpConn.disconnect();
        } catch (IOException e) {
            Log.i(TAG, "http连接连接IO异常");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            Log.i(TAG, "XML解析错误");
            e.printStackTrace();
            return null;
        }
        return fetchLyricContent(musicName, singerName, oldMusicName);
    }

    /**
     * 根据歌词下载ID，获取网络上的歌词文本内容
     *
     * @param musicInfo
     */
    private String fetchLyricContent(String musicName, String singerName, String oldMusicName) {
        if (mDownloadLyricId == -1) {
            Log.i(TAG, "未指定歌词下载ID");
            return null;
        }
        BufferedReader br = null;
        StringBuilder content = null;
        String temp = null;
        String lyricURL = "http://box.zhangmen.baidu.com/bdlrc/" + mDownloadLyricId / 100 + "/" + mDownloadLyricId + ".lrc";
        Log.i(TAG, "歌词的真实下载地址:" + lyricURL);
        try {
            mUrl = new URL(lyricURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // 获取歌词文本，存在字符串类中
        try {
            // 建立网络连接
            br = new BufferedReader(new InputStreamReader(mUrl.openStream(), GB2312));
            if (br != null) {
                content = new StringBuilder();
                // 逐行获取歌词文本
                while ((temp = br.readLine()) != null) {
                    content.append(temp);
                    Log.i(TAG, "<Lyric>" + temp);
                }
                br.close();
            }
        } catch (IOException e) {
            Log.i(TAG, "歌词获取失败");
            e.printStackTrace();
        }

        try {
            musicName = URLDecoder.decode(musicName, UTF_8);
            singerName = URLDecoder.decode(singerName, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (content != null) {
            // 检查保存的目录是否已经创建
            String folderPath = IConstants.LYRICPATH;

            File lyricFolder = new File(folderPath);
            if (!lyricFolder.exists()) {
                lyricFolder.mkdirs();
            }
            String savePath = folderPath + File.separator + oldMusicName + ".lrc";
            Log.i(TAG, "歌词保存路径:" + savePath);
            saveLyric(content.toString(), savePath);

            return savePath;
        } else {
            return null;
        }

    }

    /**
     * 将歌词保存到本地，写入外存中
     */
    private void saveLyric(String content, String filePath) {
        File file = new File(filePath);
        try {
            OutputStream outstream = new FileOutputStream(file);
            OutputStreamWriter out = new OutputStreamWriter(outstream);
            out.write(content);
            out.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Log.i(TAG, "很遗憾，将歌词写入外存时发生了IO错误");
        }
        Log.i(TAG, "歌词保存成功");
    }
}
