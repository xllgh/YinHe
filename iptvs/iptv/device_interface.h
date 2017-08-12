#ifndef CONTENT_IPTV_DEVICE_INTERFACE_H_
#define CONTENT_IPTV_DEVICE_INTERFACE_H_

#include <string>

namespace yinhe {

class DeviceInterface {
public:
    static std::string deviceRead(const std::string &key);
    static void deviceWrite(const std::string &key, const std::string &value);
    static std::string getAuthInfo(const std::string &encryToken);
    static std::string getEvent();
    static void setValueByName(const std::string &name, const std::string &value);
    static std::string getValueByName(const std::string &name);
    static void refreshVideoDisplay(int playerId, int left, int top,
                                    int width, int height, int mode);
    static int getMediaDuration(int playerId);
    static std::string getCurrentPlayTime(int playerId);
    static std::string getPlaybackMode(int playerId);
    static std::string getCurrentAudioChannel(int playerId);
    static std::string getAudioTrack(int playerId);
    static std::string getSubtitle(int playerId);
    static int joinChannel(int playerId, int userChannelId);
    static int leaveChannel(int playerId);
    static int play(int playerId, const std::string &mediaURL, const std::string &mediaCode,
                    int mediaType, int audioType, int videoType, int streamType,
                    int drmType, int fingerPrint, int copyProtection, int allowTrickmode,
                    const std::string &startTime, const std::string &endTime,
                    const std::string &entryID, const std::string &timestamp);
    static int seek(int playerId, int type, const std::string &timestamp);
    static void pause(int playerId);
    static void fastForward(int playerId, float speed);
    static void fastRewind(int playerId, float speed);
    static void resume(int playerId);
    static void gotoEnd(int playerId);
    static void gotoStart(int playerId);
    static void stop(int playerId);
    static void switchAudioChannel(int playerId);
    static void switchAudioTrack(int playerId);
    static void switchSubtitle(int playerId);
    static int getAudioPID(int playerId);
    static std::string getAudioPIDs(int playerId);
    static int setAudioPID(int playerId, int aPid);
    static int getSubtitlePID(int playerId);
    static std::string getSubtitlePIDs(int playerId);
    static int setSubtitlePID(int playerId, int sPid);
private:
    static void switchMode(int playerId, int mode);
};

}

#endif
