#ifndef CONTENT_IPTV_MEDIAPROCESSOR_INTERFACE_H_
#define CONTENT_IPTV_MEDIAPROCESSOR_INTERFACE_H_

namespace yinhe {

class MediaProcessorInterface {
public:
    static int GetPlayMode();
    static int SetVideoWindow(int x, int y, int width, int height);
    static int VideoShow();
    static int VideoHide();
    static void InitVideo(void *pVideoPara, unsigned paramSize);
    static void InitAudio(void *pAudioPara, unsigned paramSize);
    static bool StartPlay();
    static int WriteData(unsigned char *pBuffer, unsigned int nSize);
    static bool Pause();
    static bool Resume();
    static bool Fast();
    static bool StopFast();
    static bool Stop();
    static bool Seek();
    static bool SetVolume(int volume);
    static int GetVolume();
    static int GetAudioMute(bool *isMute);
    static int SetAudioMute(bool isMute);
    static bool SetRatio(int nRatio);
    static int GetAudioBalance();
    static bool SetAudioBalance(int nAudioBalance);
    static void GetVideoPixels(int &width, int &height);
    static bool IsSoftFit();
    static void SetEPGSize(int w, int h);
    static void SwitchAudioTrack(int pid);
    static void SwitchSubtitle(int pid);
    static void SetProperty(int nType, int nSub, int nValue);
    static long GetCurrentPlayTime();
    static void LeaveChannel();
    static void Playerback_register_evt_cb(void *pfunc, void *hander);
    static bool SetEos();
    static int EnableSubtitle(void *pSubtitlePara, unsigned paramSize);
};

}

#endif