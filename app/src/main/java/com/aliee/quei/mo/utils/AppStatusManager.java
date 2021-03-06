package com.aliee.quei.mo.utils;

/**
 * Conpany:成都灵云智联信息技术有限公司
 * Auther:Sun.Fuyi
 * Date: 2017/11/13
 * Effect:
 */
public class AppStatusManager {
    private static AppStatusManager mInstance = null;

    private int appStatus = AppStatusConstant.APP_FORCE_KILLED;

    private AppStatusManager() {

    }

    public static AppStatusManager getInstance() {
        if(mInstance==null) {
            synchronized (AppStatusManager.class) {
                if(mInstance==null)
                    mInstance = new AppStatusManager();
            }
        }
        return mInstance;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }

    public int getAppStatus() {
        return appStatus;
    }


    public static class AppStatusConstant {

        /**
         * App被回收，初始状态
         */
        public static final int APP_FORCE_KILLED = 0;

        /**
         * 正常运行
         */
        public static final int APP_NORMAL = 1;
    }
}
