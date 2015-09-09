package com.angcyo.y2androidapi.control;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.angcyo.y2androidapi.moudle.AndroidClassLinkBean;
import com.angcyo.y2androidapi.moudle.ApiData;
import com.angcyo.y2androidapi.moudle.SummaryWrap;
import com.angcyo.y2androidapi.util.FileUtil;
import com.angcyo.y2androidapi.util.Logger;
import com.angcyo.y2androidapi.util.Util;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by angcyo on 15-07-26-026.
 */
public class AndroidApiMgr {

    private String url;
    private Context context;
    private Document document;

    private AndroidApiMgr(Context context, String url) throws IOException {
        this.url = url;
        this.context = context;
        File file = new File(FileUtil.getAppCachePath(context), getNameFromUrl(url));
        if (file.exists() && !file.getName().equalsIgnoreCase("package-summary.html")) {
            Logger.e("读取缓冲文件");
            document = ApiData.getDocument(file);
        }
        //文件不存在, 开始缓冲文件
        else {
            FileUtil.saveFileFromStream(FileUtil.getAppCachePath(context) + File.separator + getNameFromUrl(url),
                    new URL(url).openStream());
            document = ApiData.getDocument(file);
        }
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    private List<AndroidClassLinkBean> getPackages() throws IOException {
        return ApiData.getPackages(document);
    }

    private List<AndroidClassLinkBean> getClasses() throws IOException {
        return ApiData.getClasses(document);
    }

    private SummaryWrap getDescr() throws IOException {
        return ApiData.getDescr(document);
    }

    private String getTitle() throws IOException {
        return ApiData.getHeaderTitle(document);
    }

    private String getFullTitle() throws IOException {
        return ApiData.getHeaderFullTitle(document);
    }

    private static String getNameFromUrl(String url) {
        if (url.isEmpty()) {
            return "";
        }
        return url.trim().substring(url.lastIndexOf('/') + 1);
    }

    public static class Builder {
        private Context context;
        private String url;
        private Handler handler;
        private Thread thread;
        private OnNoNetworkListener noNetWork;
        private OnGetSucceedListener getSucceed;
        private OnGetFailedListener getFailed;
        private OnPreGo preGo;
        private AndroidApiMgr apiMgr;

        public Builder(final Context context) {
            this.context = context;
            handler = new Handler(Looper.getMainLooper());
            thread = new Thread() {
                @Override
                public void run() {
                    DataPool dataPool = new DataPool();
                    SummaryPool summaryPool = new SummaryPool();
                    try {
                        apiMgr = new AndroidApiMgr(context, url);
                        dataPool.packLinks = apiMgr.getPackages();
                        dataPool.classLinks = apiMgr.getClasses();

                        summaryPool.setSummaryWrap(apiMgr.getDescr());
                        summaryPool.title = apiMgr.getTitle();
                        summaryPool.fullTitle = apiMgr.getFullTitle();
                        getSucceed(dataPool, summaryPool);
                    } catch (Exception e) {
                        getFailed();
                    }
                }
            };
        }

        public Builder load(String url) {
            this.url = url;
            return this;
        }

        public Builder onNoNetwork(OnNoNetworkListener listener) {
            noNetWork = listener;
            return this;
        }

        public Builder onGetSucceed(OnGetSucceedListener listener) {
            getSucceed = listener;
            return this;
        }

        public Builder onGetFailed(OnGetFailedListener listener) {
            getFailed = listener;
            return this;
        }

        public Builder onPreGo(OnPreGo listener) {
            preGo = listener;
            return this;
        }

        /**
         * 执行
         */
        public void go() {
            if (Util.isNetOk(context)) {
                preGo(url);
                thread.start();
            } else
                noNetwork();
        }

        private void noNetwork() {
            if (noNetWork != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        noNetWork.onNoNetwork();
                    }
                });
            }
        }

        private void preGo(final String url) {
            if (preGo != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        preGo.onPreGo(url);
                    }
                });
            }
        }

        private void getSucceed(final DataPool pool, final SummaryPool summaryPool) {
            if (getSucceed != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getSucceed.onSucceed(pool, summaryPool);
                    }
                });
            }
        }

        private void getFailed() {
            if (getFailed != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getFailed.onFailed();
                    }
                });
            }
        }
    }

    public interface OnNoNetworkListener {
        void onNoNetwork();
    }

    public interface OnPreGo {
        void onPreGo(String url);
    }

    public interface OnGetSucceedListener {
        void onSucceed(DataPool dataPool, SummaryPool summaryPool);
    }

    public interface OnGetFailedListener {
        void onFailed();
    }
}
