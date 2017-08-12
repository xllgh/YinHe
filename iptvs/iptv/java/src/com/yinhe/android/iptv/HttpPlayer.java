package com.yinhe.android.iptv;

import android.media.MediaPlayer;

class HttpPlayer extends MediaPlayer {

    public HttpPlayer(int playerId) {
    }

    public boolean PlayByTime(String mediaUrl, String timestamp) {
        return false;
    }

    public boolean Seek(String timestamp) {
        return false;
    }

    public boolean Pause() {
        return false;
    }

    public boolean Resume() {
        return false;
    }

    public boolean Stop() {
        return false;
    }

    public long GetMediaDuration() {
        return -1;
    }

    public String GetCurrentPlayTime() {
        return null;
    }

    public boolean RefreshVideoDisplay(int x, int y, int width, int height) {
        return false;
    }

    public boolean SetSpeed(float speed) {
        return false;
    }

    public boolean SetMuteFlag(int flag) {
        return false;
    }

    public boolean SetVolume(int volume) {
        return false;
    }

    public int GetVolume() {
        return -1;
    }
}

