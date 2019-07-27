package com.example.routeplanner.data.pojos;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Session {

    private final String debugTag = "debugTag";

    private final String ACTIVE = "active";
    private final String USERID = "userId";
    private final String USERNAME = "username";
    private final String LOGIN_TIME = "loginTime";

    private SharedPreferences prefs;

    public Session(Context cntx) {
        prefs = cntx.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    public void printInfo(){
        Log.d(debugTag, "user Id: " + getUserId());
        Log.d(debugTag, "username: " + getUsername());
        Log.d(debugTag, "Active: " + getActive());
        Log.d(debugTag, "LoginTime: " + getLoginTime().toString());
    }

    public void setActive(boolean active){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ACTIVE, active).apply();
    }

    public void setUserId(int userId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(USERID, userId).apply();
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USERNAME, username).apply();
    }

    public void setLoginTime(Long currentTime){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(LOGIN_TIME, currentTime).apply();
    }

    public boolean getActive(){
        return prefs.getBoolean(ACTIVE, false);
    }

    public int getUserId() {
        return prefs.getInt(USERID, 0);
    }

    public String getUsername() {
        return prefs.getString(USERNAME, "");
    }

    public Long getLoginTime(){
        return prefs.getLong(LOGIN_TIME, 0);
    }

}
