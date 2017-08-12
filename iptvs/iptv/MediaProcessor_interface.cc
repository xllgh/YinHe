#include "MediaProcessor_interface.h"
#include "MediaProcessor_library_function.h"

namespace yinhe {

#define CALL_MEDIAPROCESSOR_FUNCTION(function) \
    MediaProcessorLibraryFunction::function##_type function##_fp = \
        MediaProcessorLibraryFunction::function();

int MediaProcessorInterface::GetPlayMode()
{
    CALL_MEDIAPROCESSOR_FUNCTION(GetPlayMode);

    if (!GetPlayMode_fp) {
        return -1;
    }

    return GetPlayMode_fp();
}

int MediaProcessorInterface::SetVideoWindow(int x, int y, int width, int height)
{
    CALL_MEDIAPROCESSOR_FUNCTION(SetVideoWindow);

    if (!SetVideoWindow_fp) {
        return -1;
    }

    return SetVideoWindow_fp(x, y, width, height);
}

int MediaProcessorInterface::VideoShow()
{
    CALL_MEDIAPROCESSOR_FUNCTION(VideoShow);

    if (!VideoShow_fp) {
        return -1;
    }

    return VideoShow_fp();
}

int MediaProcessorInterface::VideoHide()
{
    CALL_MEDIAPROCESSOR_FUNCTION(VideoHide);

    if (!VideoHide_fp) {
        return -1;
    }

    return VideoHide_fp();
}

void MediaProcessorInterface::InitVideo(void *pVideoPara, unsigned paramSize)
{
    CALL_MEDIAPROCESSOR_FUNCTION(InitVideo);

    if (!InitVideo_fp) {
        return;
    }

    InitVideo_fp(pVideoPara);

    return;
}

void MediaProcessorInterface::InitAudio(void *pAudioPara, unsigned paramSize)
{
    CALL_MEDIAPROCESSOR_FUNCTION(InitAudio);

    if (!InitAudio_fp) {
        return;
    }

    InitAudio_fp(pAudioPara);

    return;
}

bool MediaProcessorInterface::StartPlay()
{
    CALL_MEDIAPROCESSOR_FUNCTION(StartPlay);

    if (!StartPlay_fp) {
        return false;
    }

    return StartPlay_fp();
}

int MediaProcessorInterface::WriteData(unsigned char *pBuffer, unsigned int nSize)
{
    CALL_MEDIAPROCESSOR_FUNCTION(WriteData);

    if (!WriteData_fp) {
        return -1;
    }

    return WriteData_fp(pBuffer, nSize);
}

bool MediaProcessorInterface::Pause()
{
    CALL_MEDIAPROCESSOR_FUNCTION(Pause);

    if (!Pause_fp) {
        return false;
    }

    return Pause_fp();
}

bool MediaProcessorInterface::Resume()
{
    CALL_MEDIAPROCESSOR_FUNCTION(Resume);

    if (!Resume_fp) {
        return false;
    }

    return Resume_fp();
}

bool MediaProcessorInterface::Fast()
{
    CALL_MEDIAPROCESSOR_FUNCTION(Fast);

    if (!Fast_fp) {
        return false;
    }

    return Fast_fp();
}

bool MediaProcessorInterface::StopFast()
{
    CALL_MEDIAPROCESSOR_FUNCTION(StopFast);

    if (!StopFast_fp) {
        return false;
    }

    return StopFast_fp();
}

bool MediaProcessorInterface::Stop()
{
    CALL_MEDIAPROCESSOR_FUNCTION(Stop);

    if (!Stop_fp) {
        return false;
    }

    return Stop_fp();
}

bool MediaProcessorInterface::Seek()
{
    CALL_MEDIAPROCESSOR_FUNCTION(Seek);

    if (!Seek_fp) {
        return false;
    }

    return Seek_fp();
}

bool MediaProcessorInterface::SetVolume(int volume)
{
    CALL_MEDIAPROCESSOR_FUNCTION(SetVolume);

    if (!SetVolume_fp) {
        return false;
    }

    return SetVolume_fp(volume);
}

int MediaProcessorInterface::GetVolume()
{
    CALL_MEDIAPROCESSOR_FUNCTION(GetVolume);

    if (!GetVolume_fp) {
        return -1;
    }

    return GetVolume_fp();
}

int MediaProcessorInterface::GetAudioMute(bool *isMute)
{
    CALL_MEDIAPROCESSOR_FUNCTION(GetAudioMute);

    if (!GetAudioMute_fp) {
        return -1;
    }

    return GetAudioMute_fp();
}

int MediaProcessorInterface::SetAudioMute(bool isMute)
{
    CALL_MEDIAPROCESSOR_FUNCTION(SetAudioMute);

    if (!SetAudioMute_fp) {
        return -1;
    }

    return SetAudioMute_fp(isMute);
}

bool MediaProcessorInterface::SetRatio(int nRatio)
{
    CALL_MEDIAPROCESSOR_FUNCTION(SetRatio);

    if (!SetRatio_fp) {
        return false;
    }

    return SetRatio_fp(nRatio);
}

int MediaProcessorInterface::GetAudioBalance()
{
    CALL_MEDIAPROCESSOR_FUNCTION(GetAudioBalance);

    if (!GetAudioBalance_fp) {
        return -1;
    }

    return GetAudioBalance_fp();
}

bool MediaProcessorInterface::SetAudioBalance(int nAudioBalance)
{
    CALL_MEDIAPROCESSOR_FUNCTION(SetAudioBalance);

    if (!SetAudioBalance_fp) {
        return false;
    }

    return SetAudioBalance_fp(nAudioBalance);
}

void MediaProcessorInterface::GetVideoPixels(int &width, int &height)
{
    CALL_MEDIAPROCESSOR_FUNCTION(GetVideoPixels);

    if (!GetVideoPixels_fp) {
        return;
    }

    return GetVideoPixels_fp(&width, &height);
}

bool MediaProcessorInterface::IsSoftFit()
{
    CALL_MEDIAPROCESSOR_FUNCTION(IsSoftFit);

    if (!IsSoftFit_fp) {
        return false;
    }

    return IsSoftFit_fp();
}

void MediaProcessorInterface::SetEPGSize(int w, int h)
{
    CALL_MEDIAPROCESSOR_FUNCTION(SetEPGSize);

    if (!SetEPGSize_fp) {
        return;
    }

    SetEPGSize_fp(w, h);

    return;
}

void MediaProcessorInterface::SwitchAudioTrack(int pid)
{
    CALL_MEDIAPROCESSOR_FUNCTION(SwitchAudioTrack);

    if (!SwitchAudioTrack_fp) {
        return;
    }

    SwitchAudioTrack_fp(pid);

    return;
}

void MediaProcessorInterface::SwitchSubtitle(int pid)
{
    CALL_MEDIAPROCESSOR_FUNCTION(SwitchSubtitle);

    if (!SwitchSubtitle_fp) {
        return;
    }

    SwitchSubtitle_fp(pid);

    return;
}

void MediaProcessorInterface::SetProperty(int nType, int nSub, int nValue)
{
    CALL_MEDIAPROCESSOR_FUNCTION(SetProperty);

    if (!SetProperty_fp) {
        return;
    }

    SetProperty_fp(nType, nSub, nValue);

    return;
}

long MediaProcessorInterface::GetCurrentPlayTime()
{
    CALL_MEDIAPROCESSOR_FUNCTION(GetCurrentPlayTime);

    if (!GetCurrentPlayTime_fp) {
        return -1;
    }

    return GetCurrentPlayTime_fp();
}

void MediaProcessorInterface::LeaveChannel()
{
    CALL_MEDIAPROCESSOR_FUNCTION(leaveChannel);

    if (!leaveChannel_fp) {
        return;
    }

    leaveChannel_fp();

    return;
}

void MediaProcessorInterface::Playerback_register_evt_cb(void *pfunc, void *hander)
{
    CALL_MEDIAPROCESSOR_FUNCTION(playerback_register_evt_cb);

    if (!playerback_register_evt_cb_fp) {
        return;
    }

    playerback_register_evt_cb_fp(pfunc, hander);

    return;
}

bool MediaProcessorInterface::SetEos()
{
    CALL_MEDIAPROCESSOR_FUNCTION(SetEos);

    if (!SetEos_fp) {
        return false;
    }

    return SetEos_fp();
}

int MediaProcessorInterface::EnableSubtitle(void *pSubtitlePara, unsigned paramSize)
{
    CALL_MEDIAPROCESSOR_FUNCTION(EnableSubtitle);

    if (!EnableSubtitle_fp) {
        return -1;
    }

    return EnableSubtitle_fp(pSubtitlePara);
}

}
