#include "MediaProcessor_library_function.h"
#include <dlfcn.h>

#include "android/log.h"
#undef MediaProcessorLibraryLOGD
#define MediaProcessorLibraryLOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "MediaProcessorLibrary", __VA_ARGS__)

namespace yinhe {

// init static member
#define INIT_MEDIAPROCESSOR_FUNCTION(function) \
    MediaProcessorLibraryFunction::function##_type MediaProcessorLibraryFunction::function##_fp = 0;

INIT_MEDIAPROCESSOR_FUNCTION(GetPlayMode);
INIT_MEDIAPROCESSOR_FUNCTION(SetVideoWindow);
INIT_MEDIAPROCESSOR_FUNCTION(VideoShow);
INIT_MEDIAPROCESSOR_FUNCTION(VideoHide);
INIT_MEDIAPROCESSOR_FUNCTION(InitVideo);
INIT_MEDIAPROCESSOR_FUNCTION(InitAudio);
INIT_MEDIAPROCESSOR_FUNCTION(StartPlay);
INIT_MEDIAPROCESSOR_FUNCTION(WriteData);
INIT_MEDIAPROCESSOR_FUNCTION(Pause);
INIT_MEDIAPROCESSOR_FUNCTION(Resume);
INIT_MEDIAPROCESSOR_FUNCTION(Fast);
INIT_MEDIAPROCESSOR_FUNCTION(StopFast);
INIT_MEDIAPROCESSOR_FUNCTION(Stop);
INIT_MEDIAPROCESSOR_FUNCTION(Seek);
INIT_MEDIAPROCESSOR_FUNCTION(SetVolume);
INIT_MEDIAPROCESSOR_FUNCTION(GetVolume);
INIT_MEDIAPROCESSOR_FUNCTION(GetAudioMute);
INIT_MEDIAPROCESSOR_FUNCTION(SetAudioMute);
INIT_MEDIAPROCESSOR_FUNCTION(SetRatio);
INIT_MEDIAPROCESSOR_FUNCTION(GetAudioBalance);
INIT_MEDIAPROCESSOR_FUNCTION(SetAudioBalance);
INIT_MEDIAPROCESSOR_FUNCTION(GetVideoPixels);
INIT_MEDIAPROCESSOR_FUNCTION(IsSoftFit);
INIT_MEDIAPROCESSOR_FUNCTION(SetEPGSize);
INIT_MEDIAPROCESSOR_FUNCTION(SetSurface);
INIT_MEDIAPROCESSOR_FUNCTION(SwitchAudioTrack);
INIT_MEDIAPROCESSOR_FUNCTION(SwitchSubtitle);
INIT_MEDIAPROCESSOR_FUNCTION(SetProperty);
INIT_MEDIAPROCESSOR_FUNCTION(GetCurrentPlayTime);
INIT_MEDIAPROCESSOR_FUNCTION(leaveChannel);
INIT_MEDIAPROCESSOR_FUNCTION(playerback_register_evt_cb);
INIT_MEDIAPROCESSOR_FUNCTION(SetEos);
INIT_MEDIAPROCESSOR_FUNCTION(EnableSubtitle);

// sym function
#define SYM_MEDIAPROCESSOR_FUNCTION(function) \
    function##_fp = (function##_type) \
        (dlsym(symHandle, "MediaProcessor_"#function)); \
    if (function##_fp == 0) { \
        MediaProcessorLibraryLOGD("sym %s fail", "MediaProcessor_"#function); \
        break; \
    }

// static member
bool MediaProcessorLibraryFunction::libraryInitialized = false;
base::Lock MediaProcessorLibraryFunction::libraryOpenLock;

bool MediaProcessorLibraryFunction::ensureLibraryInit()
{
    base::AutoLock locked(libraryOpenLock);

    if (libraryInitialized) {
        return true;
    }

    void *symHandle = 0;
    bool result = false;
    do {
        symHandle = dlopen("/system/lib/libCTC_MediaProcessorCInterface.so", RTLD_LAZY);
        if (symHandle == 0) {
            break;
        }

        SYM_MEDIAPROCESSOR_FUNCTION(GetPlayMode);
        SYM_MEDIAPROCESSOR_FUNCTION(SetVideoWindow);
        SYM_MEDIAPROCESSOR_FUNCTION(VideoShow);
        SYM_MEDIAPROCESSOR_FUNCTION(VideoHide);
        SYM_MEDIAPROCESSOR_FUNCTION(InitVideo);
        SYM_MEDIAPROCESSOR_FUNCTION(InitAudio);
        SYM_MEDIAPROCESSOR_FUNCTION(StartPlay);
        SYM_MEDIAPROCESSOR_FUNCTION(WriteData);
        SYM_MEDIAPROCESSOR_FUNCTION(Pause);
        SYM_MEDIAPROCESSOR_FUNCTION(Resume);
        SYM_MEDIAPROCESSOR_FUNCTION(Fast);
        SYM_MEDIAPROCESSOR_FUNCTION(StopFast);
        SYM_MEDIAPROCESSOR_FUNCTION(Stop);
        SYM_MEDIAPROCESSOR_FUNCTION(Seek);
        SYM_MEDIAPROCESSOR_FUNCTION(SetVolume);
        SYM_MEDIAPROCESSOR_FUNCTION(GetVolume);
        SYM_MEDIAPROCESSOR_FUNCTION(GetAudioMute);
        SYM_MEDIAPROCESSOR_FUNCTION(SetAudioMute);
        SYM_MEDIAPROCESSOR_FUNCTION(SetRatio);
        SYM_MEDIAPROCESSOR_FUNCTION(GetAudioBalance);
        SYM_MEDIAPROCESSOR_FUNCTION(SetAudioBalance);
        SYM_MEDIAPROCESSOR_FUNCTION(GetVideoPixels);
        SYM_MEDIAPROCESSOR_FUNCTION(IsSoftFit);
        SYM_MEDIAPROCESSOR_FUNCTION(SetEPGSize);
        SYM_MEDIAPROCESSOR_FUNCTION(SetSurface);
        SYM_MEDIAPROCESSOR_FUNCTION(SwitchAudioTrack);
        SYM_MEDIAPROCESSOR_FUNCTION(SwitchSubtitle);
        SYM_MEDIAPROCESSOR_FUNCTION(SetProperty);
        SYM_MEDIAPROCESSOR_FUNCTION(GetCurrentPlayTime);
        SYM_MEDIAPROCESSOR_FUNCTION(leaveChannel);
        SYM_MEDIAPROCESSOR_FUNCTION(playerback_register_evt_cb);
        SYM_MEDIAPROCESSOR_FUNCTION(SetEos);
        SYM_MEDIAPROCESSOR_FUNCTION(EnableSubtitle);

        result = true;
    } while(false);

    libraryInitialized = result;
    return libraryInitialized;
}

}
