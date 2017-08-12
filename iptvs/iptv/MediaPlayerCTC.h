#ifndef CONTENT_IPTV_MediaPlayerCTC_H_
#define CONTENT_IPTV_MediaPlayerCTC_H_

#include <string>
#include <vector>
#include <map>

namespace content {
namespace iptv {

class MediaPlayerCTC {
public:
    static int create();
    static MediaPlayerCTC* getMediaPlayerById(int playerId);
    static void deviceSendVKCallback();
    static void deviceSetVideoWindowCallback(int x, int y, int w, int h);
public:
    void setSingleOrPlaylistMode(int mode);
    int getSingleOrPlaylistMode();

    void setVideoDisplayMode(int mode);
    int getVideoDisplayMode();

    void setVideoDisplayArea(int left, int top, int width, int height);
    int getVideoDisplayLeft();
    int getVideoDisplayTop();
    int getVideoDisplayWidth();
    int getVideoDisplayHeight();

    void setMuteFlag(int flag);
    int getMuteFlag();

    void setNativeUIFlag(int flag);
    int getNativeUIFlag();

    void setMuteUIFlag(int flag);
    int getMuteUIFlag();

    void setAudioVolumeUIFlag(int flag);
    int getAudioVolumeUIFlag();

    void setAudioTrackUIFlag(int flag);
    int getAudioTrackUIFlag();

    void setProgressBarUIFlag(int flag);
    int getProgressBarUIFlag();

    void setChannelNoUIFlag(int flag);
    int getChannelNoUIFlag();

    void setSubtitileFlag(int flag);
    int getSubtitileFlag();

    void setVideoAlpha(int alpha);
    int getVideoAlpha();

    void setAllowTrickmodeFlag(int flag);
    int getAllowTrickmodeFlag();

    void setCycleFlag(int flag);
    int getCycleFlag();

    void setRandomFlag(int flag);
    int getRandomFlag();

    std::string getMediaCode();
    int getMediaDuration();
    std::string getCurrentPlayTime();
    std::string getPlaybackMode();
    int GetLastError();
    int getChannelNum();
    std::string getCurrentAudioChannel();
    std::string getAudioTrack();
    std::string getSubtitle();
    int getMediaCount();
    int getCurrentIndex();
    std::string getEntryID();
    std::string getPlaylist();

    int bindNativePlayerInstance(int nativePlayerInstanceID);
    int releaseMediaPlayer(int nativePlayerInstanceID);
    void setSingleMedia(const std::string &mediaStr);
    void addSingleMedia(int index, const std::string &mediaStr);
    void addBatchMedia(const std::string &batchMediaStr);
    int joinChannel(int userChannelId);
    int leaveChannel();
    void removeMediaByEntryID(const std::string &entryID);
    void removeMediaByIndex(int index);
    void clearAllMedia();

    void moveMediaByIndex(const std::string &entryID, int toIndex);
    void moveMediaByOffset(const std::string &entryID, int offset);
    void moveMediaByIndex1(int index, int toIndex);
    void moveMediaByOffset1(int index, int offset);

    void moveMediaToNext(const std::string &entryID);
    void moveMediaToPrevious(const std::string &entryID);
    void moveMediaToFirst(const std::string &entryID);
    void moveMediaToLast(const std::string &entryID);

    void moveMediaToNext1(int index);
    void moveMediaToPrevious1(int index);
    void moveMediaToFirst1(int index);
    void moveMediaToLast1(int index);

    void selectMediaByIndex(int index);
    void selectMediaByOffset(int offset);

    void selectNext();
    void selectPrevious();
    void selectFirst();
    void selectLast();

    void selectMediaByEntryID(const std::string &entryID);

    void playFromStart();
    void playByTime(int type, const std::string &timestamp, float speed);

    void pause();
    void fastForward(float speed);
    void fastRewind(float speed);
    void resume();
    void gotoEnd();
    void gotoStart();
    void stop();

    void refreshVideoDisplay();
    void switchAudioChannel();
    void switchAudioTrack();
    void switchSubtitle();

    void sendVendorSpecificCommand(const std::string &xml);

    int getAudioPID();
    std::string getAudioPIDs();
    void setAudioPID(int aPid);

    int getSubtitlePID();
    std::string getSubtitlePIDs();
    void setSubtitlePID(int sPid);

    void setVolume(int volume);
    int getVolume();

    void set(const std::string &ioStr, const std::string &wrStr);
    std::string get(const std::string &ioStr);
private:
    typedef struct {
        std::string mediaURL;
        std::string mediaCode;
        int mediaType;
        int audioType;
        int videoType;
        int streamType;
        int drmType;
        int fingerPrint;
        int copyProtection;
        int allowTrickmode;
        std::string startTime;
        std::string endTime;
        std::string entryID;
    } media_param;

    typedef enum {
        PlayerState_Stop,
        PlayerState_Play,
        PlayerState_Pause,
        PlayerState_Trick
    } PlayerState;

    typedef enum {
        PlayerType_None,
        PlayerType_Vod,
        PlayerType_Live,
        PlayerType_Tvod
    } PlayerType;
private:
    MediaPlayerCTC(int playerId);

    void setCurrentIndex(int index);
    const media_param* getCurrentMediaParam();
    const media_param* getMediaParamByIndex(int index);
    int getIndexByEntryID(const std::string &entryID);
    void devicePlay(const std::string &timestamp);
    void deviceSeek(long type, const std::string &timestamp);

    void notifyMediaBegining();
    void notifyMediaEnd();
    void notifyGoChannel();
    void notifyMediaError();
    void notifyPlayModeChange(int new_play_mode);

    bool checkPlayerState(PlayerState state);
    PlayerState getPlayerState();
    void setPlayerState(PlayerState state);

    bool checkPlayerType(PlayerType type);
    PlayerType getPlayerType();
    void setPlayerType(PlayerType type);

    void setHttpPlayerFlag(bool flag);
    bool getHttpPlayerFlag();
private:
    int m_playerId;

    int m_singleOrPlaylistMode;

    int m_videoDisplayMode;
    int m_videoDisplayLeft;
    int m_videoDisplayTop;
    int m_videoDisplayWidth;
    int m_videoDisplayHeight;

    int m_muteFlag;
    int m_volume;

    int m_nativeUIFlag;
    int m_muteUIFlag;
    int m_audioVolumeUIFlag;
    int m_audioTrackUIFlag;
    int m_progressBarUIFlag;
    int m_channelNoUIFlag;

    int m_subtitileFlag;

    int m_videoAlpha;

    int m_allowTrickmodeFlag;
    int m_cycleFlag;
    int m_randomFlag;

    int m_channelNum;
    int m_currentIndex;

private:
    int m_bindRef;
    std::vector<media_param> m_mediaList;
    PlayerState m_playerState;
    PlayerType m_playerType;
    bool m_httpPlayerFlag;
private:
    static const int max_player_num = 3;
    static MediaPlayerCTC* playerList[max_player_num];

    static const std::string http_stream_head;

    static bool parseMediaJson(const std::string &mediaStr, std::vector<media_param> &parseOut);
    static std::string joinMediaList(std::vector<media_param> &mediaList);
    static const std::string trim(const std::string &trimString);
    static bool getIntegerValueFromMap(const std::map<std::string, std::string> &paramMap,
                                       const std::string &key, bool important,
                                       int defaultValue, int &outValue);
    static bool notifyMediaEvent();
};

}
}

#endif
