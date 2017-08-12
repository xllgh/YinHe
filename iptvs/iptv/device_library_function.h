#ifndef CONTENT_IPTV_DEVICE_LIBRARY_FUNCTION_H_
#define CONTENT_IPTV_DEVICE_LIBRARY_FUNCTION_H_

#include "base/synchronization/lock.h"

namespace yinhe {

#define DEFINE_DEVICE_FUNCTION(function) \
    public: \
        static function##_type function() { \
            if (!ensureLibraryInit()) { \
                return 0; \
            } \
            return function##_fp; \
        } \
    private: \
        static function##_type function##_fp;

class DeviceLibraryFunction {    
private:
    static bool ensureLibraryInit();
    static bool checkBrowserProcess();

    static bool libraryInitialized;
    static base::Lock libraryOpenLock;
private:
    typedef int (*initCtc_type)(void);
    static initCtc_type initCtc_fp;
public:
    static const int deviceReadBuffSize = 512;
    typedef int (*deviceRead_type)(const char *params, char *buf, int length);
    DEFINE_DEVICE_FUNCTION(deviceRead);
public:
    typedef int (*deviceWrite_type)(const char *params, const char *buf, int length);
    DEFINE_DEVICE_FUNCTION(deviceWrite);
public:
    typedef void (*getAuthInfo_type)(const char *encryToken, char *buf, int type);
    DEFINE_DEVICE_FUNCTION(getAuthInfo);
public:
    typedef int (*setVideoWindow_type)(int x, int y, int w, int h, int mode);
    DEFINE_DEVICE_FUNCTION(setVideoWindow);
public:
    typedef int (*mediaDuration_type)(void);
    DEFINE_DEVICE_FUNCTION(mediaDuration);
public:
    typedef int (*getAudioTrack_type)(char *audioTrack, int len);
    DEFINE_DEVICE_FUNCTION(getAudioTrack);
public:
    typedef int (*getSubtitle_type)(char *subtitle, int len);
    DEFINE_DEVICE_FUNCTION(getSubtitle);
public:
    typedef int (*joinChannel_type)(int id, int channelNum);
    DEFINE_DEVICE_FUNCTION(joinChannel);
public:
    static const int deviceMediaTimeBuffSize = 32;
    typedef struct {
        char const *cMediaUrl;
        int iMediaType;
        int iAudioType;
        int iVideoType;
        int iStreamType;
        int iDrmType;
        int iFingerPrint;
        int iCopyProtection;
        int iAllowTrickmode;
        char const *cMediaCode;
        char const *cEntryID;
        char cStartTime[deviceMediaTimeBuffSize];
        char cEndTime[deviceMediaTimeBuffSize];
        int currplaying_media;
        int nextplay_media;
    } deviceMediaParam;
    typedef int (*play_type)(int id, const deviceMediaParam *pMedia, const char *ctm);
    DEFINE_DEVICE_FUNCTION(play);
public:
    typedef int (*seek_type)(int mode, const char *value);
    DEFINE_DEVICE_FUNCTION(seek);
public:
    typedef int (*pause_type)(void);
    DEFINE_DEVICE_FUNCTION(pause);
public:
    typedef int (*forward_type)(int id, float speed);
    DEFINE_DEVICE_FUNCTION(forward);
public:
    typedef int (*rewind_type)(int id, float speed);
    DEFINE_DEVICE_FUNCTION(rewind);
public:
    typedef int (*resume_type)(void);
    DEFINE_DEVICE_FUNCTION(resume);
public:
    typedef int (*gotoEnd_type)(void);
    DEFINE_DEVICE_FUNCTION(gotoEnd);
public:
    typedef int (*stop_type)(int id);
    DEFINE_DEVICE_FUNCTION(stop);
public:
    typedef int (*switchMode_type)(int modeType);
    DEFINE_DEVICE_FUNCTION(switchMode);
public:
    typedef int (*setAudioPID_type)(unsigned short aPid);
    DEFINE_DEVICE_FUNCTION(setAudioPID);
public:
    typedef unsigned short (*getAudioPID_type)(void);
    DEFINE_DEVICE_FUNCTION(getAudioPID);
public:
    typedef void (*getAudioPIDs_type)(char **audioPIDs);
    DEFINE_DEVICE_FUNCTION(getAudioPIDs);
public:
    typedef int (*setSubtitlePID_type)(unsigned short sPid);
    DEFINE_DEVICE_FUNCTION(setSubtitlePID);
public:
    typedef unsigned short (*getSubtitlePID_type)(void);
    DEFINE_DEVICE_FUNCTION(getSubtitlePID);
public:
    typedef void (*getSubtitlePIDs_type)(char **subtitlePIDs);
    DEFINE_DEVICE_FUNCTION(getSubtitlePIDs);
private:
    typedef struct {
        int (*GetPlayMode)();
        int (*SetVideoWindow)(int , int , int , int);
        int (*VideoShow)(void);
        int (*VideoHide)(void);
        void (*InitVideo)(void *, unsigned);
        void (*InitAudio)(void *, unsigned);
        bool (*StartPlay)();
        int (*WriteData)(unsigned char *, unsigned int);
        bool (*Pause)();
        bool (*Resume)();
        bool (*Fast)();
        bool (*StopFast)();
        bool (*Stop)();
        bool (*Seek)();
        bool (*SetVolume)(int);
        int (*GetVolume)();
        int (*GetAudioMute)(bool *);
        int (*SetAudioMute)(bool);
        bool (*SetRatio)(int);
        int (*GetAudioBalance)();
        bool (*SetAudioBalance)(int);
        void (*GetVideoPixels)(int &, int &);
        bool (*IsSoftFit)();
        void (*SetEPGSize)(int , int);
        void (*SwitchAudioTrack)(int);
        void (*SwitchSubtitle)(int);
        void (*SetProperty)(int , int , int);
        long (*GetCurrentPlayTime)();
        void (*leaveChannel)();
        void (*playerback_register_evt_cb)(void *, void *);
        bool (*SetEos)();
        int (*EnableSubtitle)(void *, unsigned);
    } browserMediaProcessor;
public:
    typedef void (*setBrowserMediaProcessorHandle_type)(const browserMediaProcessor *);
    DEFINE_DEVICE_FUNCTION(setBrowserMediaProcessorHandle);
private:
    typedef void (*regSendVKCallback_type)(void (*)(void));
    DEFINE_DEVICE_FUNCTION(regSendVKCallback);
private:
    typedef void (*regSetVideoWindowCallback_type)(void (*)(int, int, int, int));
    DEFINE_DEVICE_FUNCTION(regSetVideoWindowCallback);
};

}

#endif
