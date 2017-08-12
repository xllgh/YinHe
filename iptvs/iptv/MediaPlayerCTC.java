package com.yinhe.android.iptv;

import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import android.content.Context;
import android.os.Build;

import android.util.SparseArray;
import android.util.Log;

@JNINamespace("content::iptv")
public class MediaPlayerCTC {
    private static final String TAG = "MediaPlayerCTC";
    private static Context applicationContext = null;

    private static SparseArray<HttpPlayer> playerList = new SparseArray<HttpPlayer>();

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        if (applicationContext != null) {
            return;
        }

        applicationContext = context.getApplicationContext();
    }

    @CalledByNative
    private static boolean Create(int playerId) {
        HttpPlayer player = null;

        player = playerList.get(playerId);
        if (player != null) {
            player.reset();
            return true;
        }

        player = new HttpPlayer(playerId);
        playerList.put(playerId, player);
        return true;
    }

    @CalledByNative
    private static boolean PlayByTime(int playerId, String mediaUrl, String timestamp) {
        Log.d(TAG, "PlayByTime playerId " + playerId + " url " + mediaUrl + " timestamp " + timestamp);

        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            Create(playerId);
        }

        player = playerList.get(playerId);
        if (player == null) {
            return false;
        }

        return player.PlayByTime(mediaUrl, timestamp);
    }

    @CalledByNative
    private static boolean Seek(int playerId, String timestamp) {
        Log.d(TAG, "Seek playerId " + playerId + " timestamp " + timestamp);

        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return false;
        }

        return player.Seek(timestamp);
    }

    @CalledByNative
    private static boolean Pause(int playerId) {
        Log.d(TAG, "Pause playerId " + playerId);

        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return false;
        }

        return player.Pause();
    }

    @CalledByNative
    private static boolean Resume(int playerId) {
        Log.d(TAG, "Resume playerId " + playerId);

        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return false;
        }

        return player.Resume();
    }

    @CalledByNative
    private static boolean GotoEnd(int playerId) {
        Log.d(TAG, "GotoEnd playerId " + playerId);

        return Seek(playerId, String.valueOf(GetMediaDuration(playerId)));
    }

    @CalledByNative
    private static boolean GotoStart(int playerId) {
        Log.d(TAG, "GotoStart playerId " + playerId);

        return Seek(playerId, "0");
    }

    @CalledByNative
    private static boolean Stop(int playerId) {
        Log.d(TAG, "Stop playerId " + playerId);

        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return false;
        }

        player.Stop();
        playerList.remove(playerId);
        return true;
    }

    @CalledByNative
    private static long GetMediaDuration(int playerId) {
        Log.d(TAG, "GetMediaDuration playerId " + playerId);

        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return 0;
        }

        return player.GetMediaDuration();
    }

    @CalledByNative
    private static String GetCurrentPlayTime(int playerId) {
        Log.d(TAG, "GetCurrentPlayTime playerId " + playerId);

        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return null;
        }

        return player.GetCurrentPlayTime();
    }

    @CalledByNative
    private static boolean RefreshVideoDisplay(int x, int y, int width, int height) {
        Log.d(TAG, "RefreshVideoDisplay " + " x " + x + " y " + y + " width " + width + " height " + height);

        for (int i = 0; i != playerList.size(); ++i) {
            playerList.valueAt(i).RefreshVideoDisplay(x, y, width, height);
        }

        return true;
    }

    @CalledByNative
    private static boolean FastForward(int playerId, float speed) {
        return SetSpeed(playerId, speed);
    }

    @CalledByNative
    private static boolean FastRewind(int playerId, float speed) {
        return SetSpeed(playerId, speed);
    }

    private static boolean SetSpeed(int playerId, float speed) {
        Log.d(TAG, "SetSpeed playerId " + playerId + " speed " + speed);

        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return false;
        }

        return player.SetSpeed(speed);
    }

    @CalledByNative
    private static boolean SetMuteFlag(int playerId, int flag) {
        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return false;
        }

        return player.SetMuteFlag(flag);
    }

    @CalledByNative
    private static boolean SetVolume(int playerId, int volume) {
        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return false;
        }

        return player.SetVolume(volume);
    }

    @CalledByNative
    private static int GetVolume(int playerId) {
        HttpPlayer player = playerList.get(playerId);
        if (player == null) {
            return -1;
        }

        return player.GetVolume();
    }
}

