package com.jrmf360.neteaselib.base.manager;

import android.content.Context;

import com.jrmf360.neteaselib.base.utils.LogUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @创建人 honglin
 * @创建时间 17/1/17 下午2:11
 * @类描述 一句话描述 你的UI
 */
public class GlobalExceptionManager implements Thread.UncaughtExceptionHandler {

    private static GlobalExceptionManager instance = null;

    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    private Context mContext;

    private GlobalExceptionManager() {
    }

    public static GlobalExceptionManager getInstance() {
        synchronized (GlobalExceptionManager.class) {
            if (instance == null) {
                instance = new GlobalExceptionManager();
            }
        }

        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (isNeedHandle(ex)) {
            saveAndUploadCrashInfo(ex);
        } else {
            if (defaultUncaughtExceptionHandler != null){
                defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
            }
        }
    }

    /**
     * 判断是否需要处理异常
     *
     * @param ex
     * @return
     */
    private boolean isNeedHandle(Throwable ex) {
        if (ex == null) {
            return false;
        } else {
            return true;
        }
    }

    private void saveAndUploadCrashInfo(Throwable throwable) {
        // 解析错误信息
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String exceptionType = throwable.getClass().getName();
        String exceptionMsg = writer.toString();

        LogUtil.e("GlobalException","==========exceptionType===============" + exceptionType);
        LogUtil.e("GlobalException","==========exceptionMsg===============" + exceptionMsg);
        CusActivityManager.getInstance().finishAllActivity();
        System.exit(0);
//        showDialog();
    }

    protected void showDialog() {
    }
}
