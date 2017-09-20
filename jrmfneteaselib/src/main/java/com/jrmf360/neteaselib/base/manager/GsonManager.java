package com.jrmf360.neteaselib.base.manager;

import com.google.gson.Gson;

/**
 *
 */
public class GsonManager {
    private static Gson gson = new Gson();

    private GsonManager() {
    }

    public static Gson getInstance() {
        return gson;
    }

    public void release() {
        if (gson != null) {
            gson = null;
        }
    }
}
