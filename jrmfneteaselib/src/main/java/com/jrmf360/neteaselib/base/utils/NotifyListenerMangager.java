package com.jrmf360.neteaselib.base.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/1/20.
 */

public class NotifyListenerMangager {
    public static NotifyListenerMangager manager;


    private List<INotifyListener> listeners = new ArrayList();

    private Map<String, INotifyListener> maps = new HashMap();


    //单例模式​
    public static NotifyListenerMangager getInstance() {
        synchronized ("key") {
            if (manager == null) {
                manager = new NotifyListenerMangager();
            }
        }
        return manager;
    }


    //注册监听
    public void registerListener(INotifyListener lister, String tag) {
        if (listeners.contains(lister)) return;
        listeners.add(lister);
        maps.put(tag, lister);
    }


    //去除监听​
    public void unRegisterListener(INotifyListener lister) {
        if (listeners.contains(lister)) {
            listeners.remove(lister);
        }
        if (maps.get(lister) != null) {
            maps.remove(lister);
        }
    }


    //向所有注册页面发通知​
    public void nofityAllContext(Object obj) {
        for (INotifyListener lister : listeners) {
            lister.notifyContext(obj);
        }
    }


    //向某一页面发通知
    public void nofityContext(Object obj, String tag) {
        INotifyListener lister = maps.get(tag);
        if (lister != null) {
            lister.notifyContext(obj);
        }
    }

    //定时向某一页面发通知
    public void nofityContextTime(final Object obj, final String tag, int time) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                INotifyListener lister = maps.get(tag);
                if (lister != null) {
                    lister.notifyContext(obj);
                }
            }
        }, time);
    }


    //去除所有监听,建议系统退出时
    public void removeAllListener() {
        listeners.clear();
        maps.clear();
    }

}

