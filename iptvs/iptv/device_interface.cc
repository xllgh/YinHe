#include "device_interface.h"
#include "device_library_function.h"

#include "android/log.h"
#undef DeviceInterfaceLOGD
#define DeviceInterfaceLOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "DeviceInterface", __VA_ARGS__)

namespace yinhe {

#define CALL_DEVICE_FUNCTION(function) \
    DeviceLibraryFunction::function##_type function##_fp = \
        DeviceLibraryFunction::function();

std::string DeviceInterface::deviceRead(const std::string &key)
{
    char buff[DeviceLibraryFunction::deviceReadBuffSize] = {0};
    do {
        CALL_DEVICE_FUNCTION(deviceRead);

        if (!deviceRead_fp) {
            break;
        }

        deviceRead_fp(key.c_str(), buff, DeviceLibraryFunction::deviceReadBuffSize - 1);
    } while(false);

    DeviceInterfaceLOGD("%s %s %s", __FUNCTION__, key.c_str(), buff);

    return buff;
}

void DeviceInterface::deviceWrite(const std::string &key, const std::string &value)
{
    do {
        CALL_DEVICE_FUNCTION(deviceWrite);

        if (!deviceWrite_fp) {
            break;
        }

        deviceWrite_fp(key.c_str(), value.c_str(), 0);
    } while(false);

    DeviceInterfaceLOGD("%s %s %s", __FUNCTION__, key.c_str(), value.c_str());
}

std::string DeviceInterface::getAuthInfo(const std::string &encryToken)
{
    char buff[DeviceLibraryFunction::deviceReadBuffSize] = {0};

    do {
        CALL_DEVICE_FUNCTION(getAuthInfo);

        if (!getAuthInfo_fp) {
            break;
        }

        getAuthInfo_fp(encryToken.c_str(), buff, 0);
    } while(false);

    DeviceInterfaceLOGD("%s %s %s", __FUNCTION__, encryToken.c_str(), buff);

    return buff;
}

std::string DeviceInterface::getEvent()
{
    return deviceRead("getEvent");
}

void DeviceInterface::setValueByName(const std::string &name, const std::string &value)
{
    deviceWrite(name, value);
}

std::string DeviceInterface::getValueByName(const std::string &name)
{
    return deviceRead(name);
}

void DeviceInterface::refreshVideoDisplay(int playerId, int left, int top,
                                         int width, int height, int mode)
{
    DeviceInterfaceLOGD("refreshVideoDisplay %d %d %d %d %d",
                        left, top, width, height, mode);

    CALL_DEVICE_FUNCTION(setVideoWindow);

    if (!setVideoWindow_fp) {
        return;
    }

    setVideoWindow_fp(left, top, width, height, mode);
}

int DeviceInterface::getMediaDuration(int playerId)
{
    int duration = -1;

    do {
        CALL_DEVICE_FUNCTION(mediaDuration);

        if (!mediaDuration_fp) {
            break;
        }

        duration = mediaDuration_fp();
    } while(false);

    DeviceInterfaceLOGD("%s %d", __FUNCTION__, duration);
    return duration;
}

std::string DeviceInterface::getCurrentPlayTime(int playerId)
{
    return deviceRead("CurrentPlayTime");
}

std::string DeviceInterface::getPlaybackMode(int playerId)
{
    return deviceRead("PlayBackMode");
}

std::string DeviceInterface::getCurrentAudioChannel(int playerId)
{
    return deviceRead("AudioChannel");
}

std::string DeviceInterface::getAudioTrack(int playerId)
{
    char buff[DeviceLibraryFunction::deviceReadBuffSize] = {0};

    do {
        CALL_DEVICE_FUNCTION(getAudioTrack);

        if (!getAudioTrack_fp) {
            break;
        }

        getAudioTrack_fp(buff, DeviceLibraryFunction::deviceReadBuffSize - 1);
    } while(false);

    DeviceInterfaceLOGD("%s %s", __FUNCTION__, buff);

    return buff;
}

std::string DeviceInterface::getSubtitle(int playerId)
{
    char buff[DeviceLibraryFunction::deviceReadBuffSize] = {0};

    do {
        CALL_DEVICE_FUNCTION(getSubtitle);

        if (!getSubtitle_fp) {
            break;
        }

        getSubtitle_fp(buff, DeviceLibraryFunction::deviceReadBuffSize - 1);
    } while(false);

    DeviceInterfaceLOGD("%s %s", __FUNCTION__, buff);

    return buff;
}

int DeviceInterface::joinChannel(int playerId, int userChannelId)
{
    DeviceInterfaceLOGD("%s %d", __FUNCTION__, userChannelId);

    CALL_DEVICE_FUNCTION(joinChannel);

    if (!joinChannel_fp) {
        return -1;
    }

    return joinChannel_fp(playerId, userChannelId);
}

int DeviceInterface::leaveChannel(int playerId)
{
    deviceWrite("leaveChannel", std::string());
    return 0;
}

int DeviceInterface::play(int playerId, const std::string &mediaURL, const std::string &mediaCode,
                    int mediaType, int audioType, int videoType, int streamType,
                    int drmType, int fingerPrint, int copyProtection, int allowTrickmode,
                    const std::string &startTime, const std::string &endTime,
                    const std::string &entryID, const std::string &timestamp)
{
    DeviceInterfaceLOGD("%s %s %s", __FUNCTION__, mediaURL.c_str(), timestamp.c_str());

    DeviceLibraryFunction::deviceMediaParam param;
    param.cMediaUrl = mediaURL.c_str();
    param.iMediaType = mediaType;
    param.iAudioType = audioType;
    param.iVideoType = videoType;
    param.iStreamType = streamType;
    param.iDrmType = drmType;
    param.iFingerPrint = fingerPrint;
    param.iCopyProtection = copyProtection;
    param.iAllowTrickmode = allowTrickmode;
    param.cMediaCode = mediaCode.c_str();
    param.cEntryID = entryID.c_str();
    strncpy(param.cStartTime, startTime.c_str(),
            DeviceLibraryFunction::deviceMediaTimeBuffSize);
    strncpy(param.cEndTime, endTime.c_str(),
            DeviceLibraryFunction::deviceMediaTimeBuffSize);
    param.currplaying_media = 0;
    param.nextplay_media = 0;

    CALL_DEVICE_FUNCTION(play);

    if (!play_fp) {
        return -1;
    }

    return play_fp(playerId, &param, timestamp.c_str());
}

int DeviceInterface::seek(int playerId, int type, const std::string &timestamp)
{
    DeviceInterfaceLOGD("%s %d %s", __FUNCTION__, type, timestamp.c_str());

    CALL_DEVICE_FUNCTION(seek);

    if (!seek_fp) {
        return -1;
    }

    return seek_fp(type, timestamp.c_str());
}

void DeviceInterface::pause(int playerId)
{
    DeviceInterfaceLOGD("%s", __FUNCTION__);

    CALL_DEVICE_FUNCTION(pause);

    if (!pause_fp) {
        return;
    }

    pause_fp();
    return;
}

void DeviceInterface::fastForward(int playerId, float speed)
{
    DeviceInterfaceLOGD("%s %f", __FUNCTION__, speed);

    CALL_DEVICE_FUNCTION(forward);

    if (!forward_fp) {
        return;
    }

    forward_fp(playerId, speed);
    return;
}

void DeviceInterface::fastRewind(int playerId, float speed)
{
    DeviceInterfaceLOGD("%s %f", __FUNCTION__, speed);

    CALL_DEVICE_FUNCTION(rewind);

    if (!rewind_fp) {
        return;
    }

    rewind_fp(playerId, speed);
    return;
}

void DeviceInterface::resume(int playerId)
{
    DeviceInterfaceLOGD("%s", __FUNCTION__);

    CALL_DEVICE_FUNCTION(resume);

    if (!resume_fp) {
        return;
    }

    resume_fp();
    return;
}

void DeviceInterface::gotoEnd(int playerId)
{
    DeviceInterfaceLOGD("%s", __FUNCTION__);

    CALL_DEVICE_FUNCTION(gotoEnd);

    if (!gotoEnd_fp) {
        return;
    }

    gotoEnd_fp();
    return;
}

void DeviceInterface::gotoStart(int playerId)
{
    deviceWrite("gotoStart", std::string());
}

void DeviceInterface::stop(int playerId)
{
    DeviceInterfaceLOGD("%s", __FUNCTION__);

    CALL_DEVICE_FUNCTION(stop);

    if (!stop_fp) {
        return;
    }

    stop_fp(playerId);
    return;
}

void DeviceInterface::switchAudioChannel(int playerId)
{
    switchMode(playerId, 1);
}

void DeviceInterface::switchAudioTrack(int playerId)
{
    switchMode(playerId, 2);
}

void DeviceInterface::switchSubtitle(int playerId)
{
    switchMode(playerId, 3);
}

int DeviceInterface::getAudioPID(int playerId)
{
    int apid = -1;
    do {
        CALL_DEVICE_FUNCTION(getAudioPID);

        if (!getAudioPID_fp) {
            break;
        }

        apid = getAudioPID_fp();
    } while(false);

    DeviceInterfaceLOGD("%s %d", __FUNCTION__, apid);

    return apid;
}

std::string DeviceInterface::getAudioPIDs(int playerId)
{
    std::string apids;

    do {
        CALL_DEVICE_FUNCTION(getAudioPIDs);

        if (!getAudioPIDs_fp) {
            break;
        }

        char *buff = NULL;
        getAudioPIDs_fp(&buff);
        apids = buff;
        free(buff);
    } while(false);

    DeviceInterfaceLOGD("%s %s", __FUNCTION__, apids.c_str());

    return apids;
}

int DeviceInterface::setAudioPID(int playerId, int aPid)
{
    DeviceInterfaceLOGD("%s %d", __FUNCTION__, aPid);

    CALL_DEVICE_FUNCTION(setAudioPID);

    if (!setAudioPID_fp) {
        return -1;
    }

    return setAudioPID_fp(aPid);
}

int DeviceInterface::getSubtitlePID(int playerId)
{
    int spid = -1;
    do {
        CALL_DEVICE_FUNCTION(getSubtitlePID);

        if (!getSubtitlePID_fp) {
            break;
        }

        spid = getSubtitlePID_fp();
    } while(false);

    DeviceInterfaceLOGD("%s %d", __FUNCTION__, spid);

    return spid;
}
std::string DeviceInterface::getSubtitlePIDs(int playerId)
{
    std::string spids;

    do {
        CALL_DEVICE_FUNCTION(getSubtitlePIDs);

        if (!getSubtitlePIDs_fp) {
            break;
        }

        char *buff = NULL;
        getSubtitlePIDs_fp(&buff);
        spids = buff;
        free(buff);
    } while(false);

    DeviceInterfaceLOGD("%s %s", __FUNCTION__, spids.c_str());

    return spids;
}
int DeviceInterface::setSubtitlePID(int playerId, int sPid)
{
    DeviceInterfaceLOGD("%s %d", __FUNCTION__, sPid);

    CALL_DEVICE_FUNCTION(setSubtitlePID);

    if (!setSubtitlePID_fp) {
        return -1;
    }

    return setSubtitlePID_fp(sPid);
}

void DeviceInterface::switchMode(int playerId, int mode)
{
    DeviceInterfaceLOGD("%s %d", __FUNCTION__, mode);

    CALL_DEVICE_FUNCTION(switchMode);

    if (!switchMode_fp) {
        return;
    }

    switchMode_fp(mode);
    return;
}

}
