#include "MediaPlayerCTC.h"
#include <sstream>
#include "jni/MediaPlayerCTC_jni.h"
#include "device_interface.h"
#include "device_callback.h"

#include "base/values.h"
#include "base/json/json_reader.h"

#include "base/strings/string_util.h"

#include "base/android/jni_string.h"

#include "android/log.h"
#undef MediaPlayerLOGD
#define MediaPlayerLOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "MediaPlayer", __VA_ARGS__)

using yinhe::DeviceInterface;

namespace content {
namespace iptv {

MediaPlayerCTC* MediaPlayerCTC::playerList[MediaPlayerCTC::max_player_num] = {0};

const std::string MediaPlayerCTC::http_stream_head("http://");

int MediaPlayerCTC::create()
{
    int playerId = -1;

    for (int i = 0; i != max_player_num; ++i) {
        if (playerList[i] == 0) {
            MediaPlayerCTC *player = new MediaPlayerCTC(i);
            playerList[i] = player;
            MediaPlayerLOGD("create player id %d", i);
            playerId = i;
            break;
        }
    }

    if (playerId != -1) {
        DeviceCallback::addVKEventListener(deviceSendVKCallback);
        DeviceCallback::addRefreshVideoDisplayListener(deviceSetVideoWindowCallback);
    }

    return playerId;
}

MediaPlayerCTC* MediaPlayerCTC::getMediaPlayerById(int playerId)
{
    if (playerId < 0 || playerId >= max_player_num) {
        return 0;
    }

    return playerList[playerId];
}

void MediaPlayerCTC::deviceSendVKCallback()
{
    MediaPlayerLOGD("%s", __FUNCTION__);
    notifyMediaEvent();
}

void MediaPlayerCTC::deviceSetVideoWindowCallback(int x, int y, int w, int h)
{
    MediaPlayerLOGD("%s", __FUNCTION__);
    JNIEnv* env = base::android::AttachCurrentThread();
    Java_MediaPlayerCTC_RefreshVideoDisplay(env, x, y, w, h);
}

MediaPlayerCTC::MediaPlayerCTC(int playerId)
    : m_playerId(playerId)
    , m_singleOrPlaylistMode(0)
    , m_videoDisplayMode(1)
    , m_videoDisplayLeft(0)
    , m_videoDisplayTop(0)
    , m_videoDisplayWidth(0)
    , m_videoDisplayHeight(0)
    , m_muteFlag(-1)
    , m_volume(-1)
    , m_nativeUIFlag(1)
    , m_muteUIFlag(1)
    , m_audioVolumeUIFlag(1)
    , m_audioTrackUIFlag(1)
    , m_progressBarUIFlag(1)
    , m_channelNoUIFlag(1)
    , m_subtitileFlag(0)
    , m_videoAlpha(0)
    , m_allowTrickmodeFlag(1)
    , m_cycleFlag(1)
    , m_randomFlag(0)
    , m_channelNum(-1)
    , m_currentIndex(-1)
    , m_bindRef(1)
    , m_playerState(PlayerState_Stop)
    , m_playerType(PlayerType_None)
    , m_httpPlayerFlag(false)
{
    JNIEnv* env = base::android::AttachCurrentThread();
    Java_MediaPlayerCTC_Create(env, m_playerId);
}

void MediaPlayerCTC::setSingleOrPlaylistMode(int mode)
{
    m_singleOrPlaylistMode = mode;
}
int MediaPlayerCTC::getSingleOrPlaylistMode()
{
    return m_singleOrPlaylistMode;
}

void MediaPlayerCTC::setVideoDisplayMode(int mode)
{
    m_videoDisplayMode = mode;
}
int MediaPlayerCTC::getVideoDisplayMode()
{
    return m_videoDisplayMode;
}

void MediaPlayerCTC::setVideoDisplayArea(int left, int top, int width, int height)
{
    m_videoDisplayLeft = left;
    m_videoDisplayTop = top;
    m_videoDisplayWidth = width;
    m_videoDisplayHeight = height;

    return;
}
int MediaPlayerCTC::getVideoDisplayLeft()
{
    return m_videoDisplayLeft;
}
int MediaPlayerCTC::getVideoDisplayTop()
{
    return m_videoDisplayTop;
}
int MediaPlayerCTC::getVideoDisplayWidth()
{
    return m_videoDisplayWidth;
}
int MediaPlayerCTC::getVideoDisplayHeight()
{
    return m_videoDisplayHeight;
}
void MediaPlayerCTC::refreshVideoDisplay()
{
    DeviceInterface::refreshVideoDisplay(m_playerId, m_videoDisplayLeft,
                                         m_videoDisplayTop, m_videoDisplayWidth,
                                         m_videoDisplayHeight, m_videoDisplayMode);
}

void MediaPlayerCTC::setMuteFlag(int flag)
{
    m_muteFlag = flag;

    JNIEnv* env = base::android::AttachCurrentThread();
    Java_MediaPlayerCTC_SetMuteFlag(env, m_playerId, flag);

    if (getNativeUIFlag() != 0 && getMuteUIFlag() != 0) {
        // TODO:
        // enable mute ui
    }
}
int MediaPlayerCTC::getMuteFlag()
{
    if (m_muteFlag == -1) {
        m_muteFlag = (getVolume() != 0) ? 0 : 1;
    }

    return m_muteFlag;
}

void MediaPlayerCTC::setNativeUIFlag(int flag)
{
    m_nativeUIFlag = flag;

    if (m_nativeUIFlag == 0) {
        // TODO:
        // disable all native ui
    }
}
int MediaPlayerCTC::getNativeUIFlag()
{
    return m_nativeUIFlag;
}

void MediaPlayerCTC::setMuteUIFlag(int flag)
{
    m_muteUIFlag = flag;

    if (m_muteUIFlag == 0) {
        // TODO:
        // disable mute native ui
    }
}
int MediaPlayerCTC::getMuteUIFlag()
{
    return m_muteUIFlag;
}

void MediaPlayerCTC::setAudioVolumeUIFlag(int flag)
{
    m_audioVolumeUIFlag = flag;

    if (m_audioVolumeUIFlag == 0) {
        // TODO:
        // disable volume native ui
    }
}
int MediaPlayerCTC::getAudioVolumeUIFlag()
{
    return m_audioVolumeUIFlag;
}

void MediaPlayerCTC::setAudioTrackUIFlag(int flag)
{
    m_audioTrackUIFlag = flag;

    if (m_audioTrackUIFlag == 0) {
        // TODO:
        // disable track native ui
    }
}
int MediaPlayerCTC::getAudioTrackUIFlag()
{
    return m_audioTrackUIFlag;
}

void MediaPlayerCTC::setProgressBarUIFlag(int flag)
{
    m_progressBarUIFlag = flag;

    if (m_progressBarUIFlag == 0) {
        // TODO:
        // disable track native ui
    }
}
int MediaPlayerCTC::getProgressBarUIFlag()
{
    return m_progressBarUIFlag;
}

void MediaPlayerCTC::setChannelNoUIFlag(int flag)
{
    m_channelNoUIFlag = flag;

    if (m_channelNoUIFlag == 0) {
        // TODO:
        // disable channel no native ui
    }
}
int MediaPlayerCTC::getChannelNoUIFlag()
{
    return m_channelNoUIFlag;
}

void MediaPlayerCTC::setSubtitileFlag(int flag)
{
    m_subtitileFlag = flag;
}
int MediaPlayerCTC::getSubtitileFlag()
{
    return m_subtitileFlag;
}

void MediaPlayerCTC::setVideoAlpha(int alpha)
{
    m_videoAlpha = alpha;
}
int MediaPlayerCTC::getVideoAlpha()
{
    return m_videoAlpha;
}

void MediaPlayerCTC::setAllowTrickmodeFlag(int flag)
{
    m_allowTrickmodeFlag = flag;
}
int MediaPlayerCTC::getAllowTrickmodeFlag()
{
    return m_allowTrickmodeFlag;
}

void MediaPlayerCTC::setCycleFlag(int flag)
{
    m_cycleFlag = flag;
}
int MediaPlayerCTC::getCycleFlag()
{
    return m_cycleFlag;
}

void MediaPlayerCTC::setRandomFlag(int flag)
{
    m_randomFlag = flag;
}
int MediaPlayerCTC::getRandomFlag()
{
    return m_randomFlag;
}

std::string MediaPlayerCTC::getMediaCode()
{
    const media_param *param = getCurrentMediaParam();
    if (param) {
        return param->mediaCode;
    }

    return std::string();
}
int MediaPlayerCTC::getMediaDuration()
{
    int mediaDuration = -1;

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        mediaDuration = Java_MediaPlayerCTC_GetMediaDuration(env, m_playerId);
    } else {
        mediaDuration = DeviceInterface::getMediaDuration(m_playerId);
    }

    return mediaDuration;
}
std::string MediaPlayerCTC::getCurrentPlayTime()
{
    std::string currentPlayTime;

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        base::android::ScopedJavaLocalRef<jstring> jcurrentPlayTime =
            Java_MediaPlayerCTC_GetCurrentPlayTime(env, m_playerId);
        currentPlayTime = base::android::ConvertJavaStringToUTF8(env, jcurrentPlayTime);
    } else {
        currentPlayTime = DeviceInterface::getCurrentPlayTime(m_playerId);
    }

    return currentPlayTime;
}
std::string MediaPlayerCTC::getPlaybackMode()
{
    return DeviceInterface::getPlaybackMode(m_playerId);
}
int MediaPlayerCTC::GetLastError()
{
    return -1;
}
int MediaPlayerCTC::getChannelNum()
{
    if (checkPlayerType(PlayerType_Live)
        || checkPlayerType(PlayerType_Tvod)) {
        return m_channelNum;
    }

    return -1;
}
std::string MediaPlayerCTC::getCurrentAudioChannel()
{
    return DeviceInterface::getCurrentAudioChannel(m_playerId);
}
std::string MediaPlayerCTC::getAudioTrack()
{
    return DeviceInterface::getAudioTrack(m_playerId);
}
std::string MediaPlayerCTC::getSubtitle()
{
    return DeviceInterface::getSubtitle(m_playerId);
}
int MediaPlayerCTC::getMediaCount()
{
    return m_mediaList.size();
}
int MediaPlayerCTC::getCurrentIndex()
{
    return m_currentIndex;
}
std::string MediaPlayerCTC::getEntryID()
{
    const media_param *param = getCurrentMediaParam();
    if (param) {
        return param->entryID;
    }

    return std::string();
}
std::string MediaPlayerCTC::getPlaylist()
{
    return joinMediaList(m_mediaList);
}

int MediaPlayerCTC::bindNativePlayerInstance(int nativePlayerInstanceID)
{
    m_bindRef++;
    return 0;
}
int MediaPlayerCTC::releaseMediaPlayer(int nativePlayerInstanceID)
{
    m_bindRef--;
    if (m_bindRef > 0) {
        return 0;
    }

    stop();
    leaveChannel();

    if (m_playerId >=0 && m_playerId < max_player_num) {
        playerList[m_playerId] = 0;
    }
    delete this;

    return 0;
}
void MediaPlayerCTC::setSingleMedia(const std::string &mediaStr)
{
    std::vector<media_param> parseOut;
    if(parseMediaJson(mediaStr, parseOut)) {
        m_mediaList.clear();
        m_mediaList.insert(m_mediaList.end(), parseOut.begin(), parseOut.end());

        setCurrentIndex(0);
    }
}
void MediaPlayerCTC::addSingleMedia(int index, const std::string &mediaStr)
{
    std::vector<media_param> parseOut;
    if(parseMediaJson(mediaStr, parseOut)) {
        std::vector<media_param>::const_iterator insertIndex = m_mediaList.begin();
        if (index < 0 || (size_t)index > m_mediaList.size()) {
            insertIndex = m_mediaList.end();
        } else {
            insertIndex = m_mediaList.begin() + index;
        }

        m_mediaList.insert(insertIndex, parseOut.begin(), parseOut.end());
    }
}
void MediaPlayerCTC::addBatchMedia(const std::string &batchMediaStr)
{
    std::vector<media_param> parseOut;
    if(parseMediaJson(batchMediaStr, parseOut)) {
        m_mediaList.insert(m_mediaList.end(), parseOut.begin(), parseOut.end());
    }
}
int MediaPlayerCTC::joinChannel(int userChannelId)
{
    setPlayerType(PlayerType_Live);
    setPlayerState(PlayerState_Play);

    return DeviceInterface::joinChannel(m_playerId, userChannelId);
}
int MediaPlayerCTC::leaveChannel()
{
    setPlayerType(PlayerType_None);
    setPlayerState(PlayerState_Stop);

    setHttpPlayerFlag(false);

    return DeviceInterface::leaveChannel(m_playerId);
}
void MediaPlayerCTC::removeMediaByEntryID(const std::string &entryID)
{
    removeMediaByIndex(getIndexByEntryID(entryID));
}
void MediaPlayerCTC::removeMediaByIndex(int index)
{
    if (index < 0 || (size_t)index >= m_mediaList.size()) {
        return;
    }

    m_mediaList.erase(m_mediaList.begin() + index);

    if (index == getCurrentIndex()) {
        setCurrentIndex(-1);
    }
}
void MediaPlayerCTC::clearAllMedia()
{
    m_mediaList.clear();
    setCurrentIndex(-1);
}

void MediaPlayerCTC::moveMediaByIndex(const std::string &entryID, int toIndex)
{
    moveMediaByIndex1(getIndexByEntryID(entryID), toIndex);
}
void MediaPlayerCTC::moveMediaByOffset(const std::string &entryID, int offset)
{
    moveMediaByOffset1(getIndexByEntryID(entryID), offset);
}
void MediaPlayerCTC::moveMediaByIndex1(int index, int toIndex)
{
    if (index == toIndex) {
        return;
    }
    if (index < 0 || (size_t)index >= m_mediaList.size()) {
        return;
    }
    if (toIndex < 0 || (size_t)toIndex >= m_mediaList.size()) {
        return;
    }

    const media_param *srcParam = getMediaParamByIndex(index);
    if (srcParam == 0) {
        return;
    }

    media_param srcCopy = *srcParam;
    m_mediaList.erase(m_mediaList.begin() + index);
    m_mediaList.insert(m_mediaList.begin() + toIndex, srcCopy);
}
void MediaPlayerCTC::moveMediaByOffset1(int index, int offset)
{
    moveMediaByIndex1(index, index + offset);
}

void MediaPlayerCTC::moveMediaToNext(const std::string &entryID)
{
    moveMediaToNext1(getIndexByEntryID(entryID));
}
void MediaPlayerCTC::moveMediaToPrevious(const std::string &entryID)
{
    moveMediaToPrevious1(getIndexByEntryID(entryID));
}
void MediaPlayerCTC::moveMediaToFirst(const std::string &entryID)
{
    moveMediaToFirst1(getIndexByEntryID(entryID));
}
void MediaPlayerCTC::moveMediaToLast(const std::string &entryID)
{
    moveMediaToLast1(getIndexByEntryID(entryID));
}

void MediaPlayerCTC::moveMediaToNext1(int index)
{
    moveMediaByOffset1(index, 1);
}
void MediaPlayerCTC::moveMediaToPrevious1(int index)
{
    moveMediaByOffset1(index, -1);
}
void MediaPlayerCTC::moveMediaToFirst1(int index)
{
    moveMediaByIndex1(index, 0);
}
void MediaPlayerCTC::moveMediaToLast1(int index)
{
    moveMediaByIndex1(index, m_mediaList.size() - 1);
}

void MediaPlayerCTC::selectMediaByIndex(int index)
{
    if (index < 0 || (size_t)index >= m_mediaList.size()) {
        return;
    }

    setCurrentIndex(index);
}
void MediaPlayerCTC::selectMediaByOffset(int offset)
{
    int index = getCurrentIndex() + offset;
    selectMediaByIndex(index);
}

void MediaPlayerCTC::selectNext()
{
    selectMediaByOffset(1);
}
void MediaPlayerCTC::selectPrevious()
{
    selectMediaByOffset(-1);
}
void MediaPlayerCTC::selectFirst()
{
    selectMediaByIndex(0);
}
void MediaPlayerCTC::selectLast()
{
    selectMediaByIndex(m_mediaList.size() - 1);
}

void MediaPlayerCTC::selectMediaByEntryID(const std::string &entryID)
{
    selectMediaByIndex(getIndexByEntryID(entryID));
}

void MediaPlayerCTC::playFromStart()
{
    MediaPlayerLOGD("%s m_playerType %d", __FUNCTION__, getPlayerType());

    switch (getPlayerType()) {
        case PlayerType_None: {
            devicePlay("0");
            break;
        }
        case PlayerType_Vod: {
            deviceSeek(1, "0");
            break;
        }
        case PlayerType_Live: {
            break;
        }
        case PlayerType_Tvod: {
            gotoEnd();
            break;
        }
    }
}
void MediaPlayerCTC::playByTime(int type, const std::string &timestamp, float speed)
{
    MediaPlayerLOGD("%s m_playerType %d", __FUNCTION__, getPlayerType());

    switch (getPlayerType()) {
        case PlayerType_None: {
            devicePlay(timestamp);
            break;
        }
        case PlayerType_Vod: {
            deviceSeek(type, timestamp);
            break;
        }
        case PlayerType_Live: {
            break;
        }
        case PlayerType_Tvod: {
            deviceSeek(type, timestamp);
            break;
        }
    }
}

void MediaPlayerCTC::devicePlay(const std::string &timestamp)
{
    const media_param *param = getCurrentMediaParam();
    if (param == 0) {
        return;
    }

    setPlayerType(PlayerType_Vod);
    setPlayerState(PlayerState_Play);

    DeviceInterface::play(m_playerId, param->mediaURL, param->mediaCode,
                          param->mediaType, param->audioType, param->videoType,
                          param->streamType, param->drmType, param->fingerPrint,
                          param->copyProtection, param->allowTrickmode,
                          param->startTime, param->endTime, param->entryID,
                          timestamp);

    if (base::StartsWith(param->mediaURL, http_stream_head, base::CompareCase::INSENSITIVE_ASCII)) {
        setHttpPlayerFlag(true);
    }

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        base::android::ScopedJavaLocalRef<jstring> jmediaURL =
            base::android::ConvertUTF8ToJavaString(env, param->mediaURL);
        base::android::ScopedJavaLocalRef<jstring> jtimestamp =
            base::android::ConvertUTF8ToJavaString(env, timestamp);
        Java_MediaPlayerCTC_PlayByTime(env, m_playerId, jmediaURL, jtimestamp);
    }
}
void MediaPlayerCTC::deviceSeek(long type, const std::string &timestamp)
{
    if (checkPlayerType(PlayerType_Live)) {
        setPlayerType(PlayerType_Tvod);
    }

    setPlayerState(PlayerState_Play);

    DeviceInterface::seek(m_playerId, type, timestamp);

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        base::android::ScopedJavaLocalRef<jstring> jtimestamp =
            base::android::ConvertUTF8ToJavaString(env, timestamp);
        Java_MediaPlayerCTC_Seek(env, m_playerId, jtimestamp);
    }
}

void MediaPlayerCTC::pause()
{
    if (!checkPlayerState(PlayerState_Play)) {
        return;
    }

    setPlayerState(PlayerState_Pause);

    DeviceInterface::pause(m_playerId);

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        Java_MediaPlayerCTC_Pause(env, m_playerId);
    }
}
void MediaPlayerCTC::fastForward(float speed)
{
    if (!checkPlayerState(PlayerState_Play)
        && !checkPlayerState(PlayerState_Pause)
        && !checkPlayerState(PlayerState_Trick)) {
        return;
    }

    setPlayerState(PlayerState_Trick);

    DeviceInterface::fastForward(m_playerId, speed);

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        Java_MediaPlayerCTC_FastForward(env, m_playerId, speed);
    }
}
void MediaPlayerCTC::fastRewind(float speed)
{
    if (!checkPlayerState(PlayerState_Play)
        && !checkPlayerState(PlayerState_Pause)
        && !checkPlayerState(PlayerState_Trick)) {
        return;
    }

    setPlayerState(PlayerState_Trick);

    DeviceInterface::fastRewind(m_playerId, speed);

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        Java_MediaPlayerCTC_FastRewind(env, m_playerId, speed);
    }
}
void MediaPlayerCTC::resume()
{
    if (!checkPlayerState(PlayerState_Pause)
        && !checkPlayerState(PlayerState_Trick)) {
        return;
    }

    setPlayerState(PlayerState_Play);

    DeviceInterface::resume(m_playerId);

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        Java_MediaPlayerCTC_Resume(env, m_playerId);
    }
}
void MediaPlayerCTC::gotoEnd()
{
    // FIXME:
    // check play state && type
    setPlayerState(PlayerState_Play);

    DeviceInterface::gotoEnd(m_playerId);

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        Java_MediaPlayerCTC_GotoEnd(env, m_playerId);
    }
}
void MediaPlayerCTC::gotoStart()
{
    // FIXME:
    // check play state && type
    setPlayerState(PlayerState_Play);

    DeviceInterface::gotoStart(m_playerId);

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        Java_MediaPlayerCTC_GotoStart(env, m_playerId);
    }
}
void MediaPlayerCTC::stop()
{
    setPlayerState(PlayerState_Stop);
    setPlayerType(PlayerType_None);

    setHttpPlayerFlag(false);

    DeviceInterface::stop(m_playerId);

    if (getHttpPlayerFlag()) {
        JNIEnv* env = base::android::AttachCurrentThread();
        Java_MediaPlayerCTC_Stop(env, m_playerId);
    }
}

void MediaPlayerCTC::switchAudioChannel()
{
    DeviceInterface::switchAudioChannel(m_playerId);

    if (getNativeUIFlag() != 0 && getAudioVolumeUIFlag() != 0) {
        // TODO:
        // enable audio volume native ui
    }
}
void MediaPlayerCTC::switchAudioTrack()
{
    DeviceInterface::switchAudioTrack(m_playerId);

    if (getNativeUIFlag() != 0 && getAudioTrackUIFlag() != 0) {
        // TODO:
        // enable audio track native ui
    }
}
void MediaPlayerCTC::switchSubtitle()
{
    DeviceInterface::switchSubtitle(m_playerId);
}

void MediaPlayerCTC::sendVendorSpecificCommand(const std::string &xml)
{
}

int MediaPlayerCTC::getAudioPID()
{
    return DeviceInterface::getAudioPID(m_playerId);
}
std::string MediaPlayerCTC::getAudioPIDs()
{
    return DeviceInterface::getAudioPIDs(m_playerId);
}
void MediaPlayerCTC::setAudioPID(int aPid)
{
    DeviceInterface::setAudioPID(m_playerId, aPid);
}

int MediaPlayerCTC::getSubtitlePID()
{
    return DeviceInterface::getSubtitlePID(m_playerId);
}
std::string MediaPlayerCTC::getSubtitlePIDs()
{
    return DeviceInterface::getSubtitlePIDs(m_playerId);
}
void MediaPlayerCTC::setSubtitlePID(int sPid)
{
    DeviceInterface::setSubtitlePID(m_playerId, sPid);
}

void MediaPlayerCTC::setVolume(int volume)
{
    m_volume = volume;

    JNIEnv* env = base::android::AttachCurrentThread();
    Java_MediaPlayerCTC_SetVolume(env, m_playerId, m_volume);

    if (getNativeUIFlag() != 0 && getMuteUIFlag() != 0) {
        // TODO:
        // enable mute ui
    }
}
int MediaPlayerCTC::getVolume()
{
    if (m_volume == -1) {
        JNIEnv* env = base::android::AttachCurrentThread();
        m_volume = Java_MediaPlayerCTC_GetVolume(env, m_playerId);

        if (m_volume % 5 != 0) {
            m_volume = (m_volume / 5 + 1) * 5;
        }
    }

    return m_volume;
}

void MediaPlayerCTC::set(const std::string &ioStr, const std::string &wrStr)
{
}
std::string MediaPlayerCTC::get(const std::string &ioStr)
{
    return std::string();
}

bool MediaPlayerCTC::parseMediaJson(const std::string &mediaStr, std::vector<media_param> &parseOut)
{
    std::vector<std::string> mediaParamArray;
    std::string::size_type jsonStartIndex = std::string::npos;
    std::string::size_type jsonEndIndex = 0;

    for (;;) {
        jsonStartIndex = mediaStr.find('{', jsonEndIndex);
        if (jsonStartIndex == std::string::npos) {
            break;
        }

        jsonEndIndex = mediaStr.find('}', jsonStartIndex);
        if (jsonEndIndex == std::string::npos) {
            break;
        }

        mediaParamArray.push_back(trim(mediaStr.substr(jsonStartIndex + 1,
                                       jsonEndIndex - jsonStartIndex - 1)));
    }

    if (mediaParamArray.empty()) {
        return false;
    }

    for (std::vector<std::string>::const_iterator iter = mediaParamArray.begin();
         iter != mediaParamArray.end(); ++iter) {

        std::string paramStr = *iter;

        std::map<std::string, std::string> paramMap;
        std::string::size_type keyStartIndex = 0;
        std::string::size_type keyEndIndex = 0;

        std::string::size_type valueStartIndex = 0;
        std::string::size_type valueEndIndex = 0;

        for (;;) {
            std::string keyString;
            std::string valueString;

            keyStartIndex = paramStr.find_first_not_of(',', valueEndIndex);
            if (keyStartIndex == std::string::npos) {
                break;
            }

            keyEndIndex = paramStr.find(':', keyStartIndex);
            if (keyEndIndex == std::string::npos) {
                break;
            }

            keyString = trim(paramStr.substr(keyStartIndex, keyEndIndex - keyStartIndex));

            valueStartIndex = paramStr.find_first_not_of(' ', keyEndIndex + 1);
            if (valueStartIndex == std::string::npos) {
                break;
            }

            switch (paramStr[valueStartIndex]) {
                case '\"':
                case '\'': {
                    char endQuote = paramStr[valueStartIndex];
                    valueEndIndex = paramStr.find(endQuote, valueStartIndex + 1);
                    if (valueEndIndex == std::string::npos) {
                        valueString = trim(paramStr.substr(valueStartIndex + 1));
                        break;
                    } else {
                        valueString = trim(paramStr.substr(valueStartIndex + 1, valueEndIndex - valueStartIndex - 1));
                        valueEndIndex = paramStr.find(',', valueEndIndex);
                    }

                    break;
                }
                default: {
                    valueEndIndex = paramStr.find(',', valueStartIndex);
                    if (valueEndIndex == std::string::npos) {
                        valueString = trim(paramStr.substr(valueStartIndex));
                        break;
                    } else {
                        valueString = trim(paramStr.substr(valueStartIndex, valueEndIndex - valueStartIndex));
                    }

                    break;
                }
            }

            if (!keyString.empty() && !valueString.empty()) {
                paramMap[keyString] = valueString;
            }

            if (valueEndIndex == std::string::npos) {
                break;
            }
        }

        media_param paramParsed;

        // mediaUrl
        if (paramMap.count("mediaUrl")) {
            paramParsed.mediaURL = paramMap["mediaUrl"];
        } else if (paramMap.count("mediaURL")) {
            paramParsed.mediaURL = paramMap["mediaURL"];
        }
        if (paramParsed.mediaURL.empty()) {
            continue;
        }

        // mediaCode
        if (paramMap.count("mediaCode")) {
            paramParsed.mediaCode = paramMap["mediaCode"];
        }
        if (paramParsed.mediaCode.empty()) {
            continue;
        }

        if (!getIntegerValueFromMap(paramMap, "mediaType", true, 0, paramParsed.mediaType)) {
            continue;
        }

        if (!getIntegerValueFromMap(paramMap, "audioType", true, 0, paramParsed.audioType)) {
            continue;
        }

        if (!getIntegerValueFromMap(paramMap, "videoType", true, 0, paramParsed.videoType)) {
            continue;
        }

        if (!getIntegerValueFromMap(paramMap, "streamType", true, 0, paramParsed.streamType)) {
            continue;
        }

        if (!getIntegerValueFromMap(paramMap, "drmType", true, 0, paramParsed.drmType)) {
            continue;
        }

        if (!getIntegerValueFromMap(paramMap, "fingerPrint", false, 1, paramParsed.fingerPrint)) {
            continue;
        }

        if (!getIntegerValueFromMap(paramMap, "copyProtection", false, 0, paramParsed.copyProtection)) {
            continue;
        }

        if (!getIntegerValueFromMap(paramMap, "allowTrickmode", false, 1, paramParsed.allowTrickmode)) {
            continue;
        }

        if (paramMap.count("startTime")) {
            paramParsed.startTime = paramMap["startTime"];
        }

        if (paramMap.count("endTime")) {
            paramParsed.endTime = paramMap["endTime"];
        }

        if (paramMap.count("entryID")) {
            paramParsed.entryID = paramMap["entryID"];
        }

        MediaPlayerLOGD("mediaURL %s", paramParsed.mediaURL.c_str());
        MediaPlayerLOGD("mediaCode %s", paramParsed.mediaCode.c_str());
        MediaPlayerLOGD("mediaType %d", paramParsed.mediaType);
        MediaPlayerLOGD("audioType %d", paramParsed.audioType);
        MediaPlayerLOGD("videoType %d", paramParsed.videoType);
        MediaPlayerLOGD("streamType %d", paramParsed.streamType);
        MediaPlayerLOGD("drmType %d", paramParsed.drmType);
        MediaPlayerLOGD("fingerPrint %d", paramParsed.fingerPrint);
        MediaPlayerLOGD("copyProtection %d", paramParsed.copyProtection);
        MediaPlayerLOGD("allowTrickmode %d", paramParsed.allowTrickmode);
        MediaPlayerLOGD("startTime %s", paramParsed.startTime.c_str());
        MediaPlayerLOGD("endTime %s", paramParsed.endTime.c_str());
        MediaPlayerLOGD("entryID %s", paramParsed.entryID.c_str());

        parseOut.push_back(paramParsed);
    }

    return true;
}

std::string MediaPlayerCTC::joinMediaList(std::vector<media_param> &mediaList)
{
    std::ostringstream mediaString;
    mediaString << "[";
    for (std::vector<media_param>::const_iterator iter = mediaList.begin();
            iter != mediaList.end(); ++iter) {
        mediaString << "{mediaURL:\"" << (iter->mediaURL) << "\",";
        mediaString << "mediaCode:\"" << (iter->mediaCode) << "\",";
        mediaString << "mediaType:" << (iter->mediaType) << ",";
        mediaString << "audioType:" << (iter->audioType) << ",";
        mediaString << "videoType:" << (iter->videoType) << ",";
        mediaString << "streamType:" << (iter->streamType) << ",";
        mediaString << "drmType:" << (iter->drmType) << ",";
        mediaString << "fingerPrint:" << (iter->fingerPrint) << ",";
        mediaString << "copyProtection:" << (iter->copyProtection) << ",";
        mediaString << "allowTrickmode:" << (iter->allowTrickmode) << ",";
        mediaString << "startTime:" << (iter->startTime) << ",";
        mediaString << "endTime:" << (iter->endTime) << ",";
        mediaString << "entryID:\"" << (iter->entryID) << "\"}";
    }
    mediaString << "]";

    return mediaString.str();
}

void MediaPlayerCTC::setCurrentIndex(int index)
{
    m_currentIndex = index;
}

const MediaPlayerCTC::media_param* MediaPlayerCTC::getCurrentMediaParam()
{
    int index = getCurrentIndex();

    return getMediaParamByIndex(index);
}

const MediaPlayerCTC::media_param* MediaPlayerCTC::getMediaParamByIndex(int index)
{
    if (index < 0 || index > getMediaCount()) {
        return 0;
    }

    return &(m_mediaList[index]);
}

int MediaPlayerCTC::getIndexByEntryID(const std::string &entryID)
{
    for (std::vector<media_param>::const_iterator iter = m_mediaList.begin();
            iter != m_mediaList.end(); ++iter) {
        if (iter->entryID == entryID) {
            return (iter - m_mediaList.begin());
        }
    }

    return -1;
}

const std::string MediaPlayerCTC::trim(const std::string &trimString)
{
    std::string::size_type first_good = trimString.find_first_not_of(' ');
    if (first_good == std::string::npos) {
        return std::string();
    }

    std::string::size_type last_good = trimString.find_last_not_of(' ');
    return trimString.substr(first_good, last_good - first_good + 1);
}

bool MediaPlayerCTC::getIntegerValueFromMap(const std::map<std::string, std::string> &paramMap,
                                            const std::string &key, bool important,
                                            int defaultValue, int &outValue)
{
    std::map<std::string, std::string>::const_iterator iter = paramMap.find(key);

    do {
        if (iter == paramMap.end()) {
            break;
        }

        std::string value = iter->second;
        if (value.empty()) {
            break;
        }

        int convertValue = 0;
        std::istringstream iss(value);
        if (iss >> convertValue) {
            outValue = convertValue;
            return true;
        } else {
            break;
        }
        
    } while(false);

    if (important) {
        return false;
    } else {
        outValue = defaultValue;
        return true;
    }

    return false;
}

bool MediaPlayerCTC::notifyMediaEvent()
{
    bool result = false;
    do {
        std::string eventText = DeviceInterface::deviceRead("getEventForBrowser");
        if (eventText.empty()) {
            break;
        }

        std::unique_ptr<base::Value> eventJson(base::JSONReader::Read(eventText));
        MediaPlayerLOGD("json type %d", eventJson->GetType());
        if (eventJson->GetType() != base::Value::TYPE_DICTIONARY) {
            break;
        }

        std::unique_ptr<base::DictionaryValue> eventDict(
          static_cast<base::DictionaryValue*>(eventJson.release()));

        bool result = false;

        std::string eventType;
        result = eventDict->GetString("type", &eventType);
        if (!result) {
            MediaPlayerLOGD("can not get event type");
            break;
        }
        MediaPlayerLOGD("%s type = %s", __FUNCTION__, eventType.c_str());

        int instanceId = -1;
        result = eventDict->GetInteger("instance_id", &instanceId);
        if (!result) {
            MediaPlayerLOGD("can not get instance id");
            break;
        }
        MediaPlayerLOGD("%s instance_id = %d", __FUNCTION__, instanceId);

        MediaPlayerCTC *player = MediaPlayerCTC::getMediaPlayerById(instanceId);

        if (player == NULL) {
            break;
        }

        if (eventType == "EVENT_MEDIA_BEGINING") {
            player->notifyMediaBegining();
        }
        else if (eventType == "EVENT_MEDIA_END") {
            player->notifyMediaEnd();
        }
        else if (eventType == "EVENT_GO_CHANNEL") {
            player->notifyGoChannel();
        }
        if (eventType == "EVENT_MEDIA_ERROR") {
            player->notifyMediaError();
        }
        else if (eventType == "EVENT_PLAYMODE_CHANGE") {
            int new_play_mode = -1;
            bool result = eventDict->GetInteger("new_play_mode", &new_play_mode);
            if (!result) {
                MediaPlayerLOGD("can not get new_play_mode");
                break;
            }
            MediaPlayerLOGD("%s new_play_mode = %d", __FUNCTION__, new_play_mode);
            player->notifyPlayModeChange(new_play_mode);
        }

        result = true;
    } while(false);

    return result;
}

void MediaPlayerCTC::notifyMediaBegining()
{
    MediaPlayerLOGD("%s", __FUNCTION__);

    if (checkPlayerType(PlayerType_Live)) {
        setPlayerType(PlayerType_Tvod);
    }

    setPlayerState(PlayerState_Play);
}

void MediaPlayerCTC::notifyMediaEnd()
{
    MediaPlayerLOGD("%s", __FUNCTION__);

    setPlayerState(PlayerState_Stop);
    setPlayerType(PlayerType_None);
}

void MediaPlayerCTC::notifyGoChannel()
{
    MediaPlayerLOGD("%s", __FUNCTION__);

    setPlayerType(PlayerType_Live);
    setPlayerState(PlayerState_Play);
}

void MediaPlayerCTC::notifyMediaError()
{
    MediaPlayerLOGD("%s", __FUNCTION__);
}

void MediaPlayerCTC::notifyPlayModeChange(int new_play_mode)
{
    MediaPlayerLOGD("%s", __FUNCTION__);

    switch(new_play_mode) {
        case 0: { // STOP
            setPlayerType(PlayerType_None);
            setPlayerState(PlayerState_Stop);
            break;
        }
        case 1: { // PAUSE
            if (checkPlayerType(PlayerType_Live)) {
                setPlayerType(PlayerType_Tvod);
            }
            setPlayerState(PlayerState_Pause);
            break;
        }
        case 2: { // NORMAL_PLAY
            setPlayerState(PlayerState_Play);
            break;
        }
        case 3: { // TRICK_MODE
            if (checkPlayerType(PlayerType_Live)) {
                setPlayerType(PlayerType_Tvod);
            }
            setPlayerState(PlayerState_Trick);
            break;
        }
        case 4: { // MULTICAST_CHANNEL_PLAY
            setPlayerType(PlayerType_Live);
            setPlayerState(PlayerState_Play);
            break;
        }
        case 5: { // UNICAST_CHANEL_PLAY
            setPlayerType(PlayerType_Live);
            setPlayerState(PlayerState_Play);
            break;
        }
        default: {
            break;
        }
    }
}

bool MediaPlayerCTC::checkPlayerState(PlayerState state)
{
    return m_playerState == state;
}
MediaPlayerCTC::PlayerState MediaPlayerCTC::getPlayerState()
{
    return m_playerState;
}
void MediaPlayerCTC::setPlayerState(MediaPlayerCTC::PlayerState state)
{
    m_playerState = state;
}

bool MediaPlayerCTC::checkPlayerType(PlayerType type)
{
    return m_playerType == type;
}
MediaPlayerCTC::PlayerType MediaPlayerCTC::getPlayerType()
{
    return m_playerType;
}
void MediaPlayerCTC::setPlayerType(MediaPlayerCTC::PlayerType type)
{
    m_playerType = type;
}

void MediaPlayerCTC::setHttpPlayerFlag(bool flag)
{
    m_httpPlayerFlag = flag;
}
bool MediaPlayerCTC::getHttpPlayerFlag()
{
    return m_httpPlayerFlag;
}

}
}

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     