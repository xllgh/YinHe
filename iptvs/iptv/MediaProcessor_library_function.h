#ifndef CONTENT_IPTV_MEDIAPROCESSOR_LIBRARY_FUNCTION_H_
#define CONTENT_IPTV_MEDIAPROCESSOR_LIBRARY_FUNCTION_H_

#include "base/synchronization/lock.h"

namespace yinhe {

#define DEFINE_MEDIAPROCESSOR_FUNCTION(function) \
    public: \
        static function##_type function() { \
            if (!ensureLibraryInit()) { \
                return 0; \
            } \
            return function##_fp; \
        } \
    private: \
        static function##_type function##_fp;

class MediaProcessorLibraryFunction {
private:
    static bool ensureLibraryInit();

    static bool libraryInitialized;
    static base::Lock libraryOpenLock;
public:
    typedef int (*GetPlayMode_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(GetPlayMode);
public:
    typedef int (*SetVideoWindow_type)(int, int, int, int);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SetVideoWindow);
public:
    typedef int (*VideoShow_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(VideoShow);
public:
    typedef int (*VideoHide_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(VideoHide);
public:
    typedef void (*InitVideo_type)(void *);
    DEFINE_MEDIAPROCESSOR_FUNCTION(InitVideo);
public:
    typedef void (*InitAudio_type)(void *);
    DEFINE_MEDIAPROCESSOR_FUNCTION(InitAudio);
public:
    typedef bool (*StartPlay_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(StartPlay);
public:
    typedef int (*WriteData_type)(unsigned char *, unsigned int);
    DEFINE_MEDIAPROCESSOR_FUNCTION(WriteData);
public:
    typedef bool (*Pause_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(Pause);
public:
    typedef bool (*Resume_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(Resume);
public:
    typedef bool (*Fast_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(Fast);
public:
    typedef bool (*StopFast_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(StopFast);
public:
    typedef bool (*Stop_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(Stop);
public:
    typedef bool (*Seek_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(Seek);
public:
    typedef bool (*SetVolume_type)(int);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SetVolume);
public:
    typedef int (*GetVolume_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(GetVolume);
public:
    typedef bool (*GetAudioMute_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(GetAudioMute);
public:
    typedef int (*SetAudioMute_type)(bool);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SetAudioMute);
public:
    typedef bool (*SetRatio_type)(int);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SetRatio);
public:
    typedef int (*GetAudioBalance_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(GetAudioBalance);
public:
    typedef bool (*SetAudioBalance_type)(int);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SetAudioBalance);
public:
    typedef void (*GetVideoPixels_type)(int *, int *);
    DEFINE_MEDIAPROCESSOR_FUNCTION(GetVideoPixels);
public:
    typedef bool (*IsSoftFit_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(IsSoftFit);
public:
    typedef void (*SetEPGSize_type)(int , int);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SetEPGSize);
public:
    typedef void (*SetSurface_type)(void *, void *);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SetSurface);
public:
    typedef void (*SwitchAudioTrack_type)(int);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SwitchAudioTrack);
public:
    typedef void (*SwitchSubtitle_type)(int);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SwitchSubtitle);
public:
    typedef void (*SetProperty_type)(int, int, int);
    DEFINE_MEDIAPROCESSOR_FUNCTION(SetProperty);
public:
    typedef long(*GetCurrentPlayTime_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(GetCurrentPlayTime);
public:
    typedef void (*leaveChannel_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(leaveChannel);
public:
    typedef void (*playerback_register_evt_cb_type)(void *, void *);
    DEFINE_MEDIAPROCESSOR_FUNCTION(playerback_register_evt_cb);
public:
    typedef bool (*SetEos_type)();
    DEFINE_MEDIAPROCESSOR_FUNCTION(SetEos);
public:
    typedef int (*EnableSubtitle_type)(void *);
    DEFINE_MEDIAPROCESSOR_FUNCTION(EnableSubtitle);
};

}

#endif

