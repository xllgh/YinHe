#include "device_library_function.h"
#include <dlfcn.h>
#include "base/process/process.h"
#include "base/files/file_util.h"
#include "MediaProcessor_interface.h"
#include "device_callback.h"

#include "android/log.h"
#undef DeviceLibraryLOGD
#define DeviceLibraryLOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "DeviceLibrary", __VA_ARGS__)

namespace yinhe {

// init static member
#define INIT_DEVICE_FUNCTION(function) \
    DeviceLibraryFunction::function##_type DeviceLibraryFunction::function##_fp = 0;

INIT_DEVICE_FUNCTION(initCtc);
INIT_DEVICE_FUNCTION(deviceRead);
INIT_DEVICE_FUNCTION(deviceWrite);
INIT_DEVICE_FUNCTION(getAuthInfo);
INIT_DEVICE_FUNCTION(setVideoWindow);
INIT_DEVICE_FUNCTION(mediaDuration);
INIT_DEVICE_FUNCTION(getAudioTrack);
INIT_DEVICE_FUNCTION(getSubtitle);
INIT_DEVICE_FUNCTION(joinChannel);
INIT_DEVICE_FUNCTION(play);
INIT_DEVICE_FUNCTION(seek);
INIT_DEVICE_FUNCTION(pause);
INIT_DEVICE_FUNCTION(forward);
INIT_DEVICE_FUNCTION(rewind);
INIT_DEVICE_FUNCTION(resume);
INIT_DEVICE_FUNCTION(gotoEnd);
INIT_DEVICE_FUNCTION(stop);
INIT_DEVICE_FUNCTION(switchMode);
INIT_DEVICE_FUNCTION(setAudioPID);
INIT_DEVICE_FUNCTION(getAudioPID);
INIT_DEVICE_FUNCTION(getAudioPIDs);
INIT_DEVICE_FUNCTION(setSubtitlePID);
INIT_DEVICE_FUNCTION(getSubtitlePID);
INIT_DEVICE_FUNCTION(getSubtitlePIDs);
INIT_DEVICE_FUNCTION(setBrowserMediaProcessorHandle);
INIT_DEVICE_FUNCTION(regSendVKCallback);
INIT_DEVICE_FUNCTION(regSetVideoWindowCallback);

// sym function
#define SYM_DEVICE_FUNCTION(function) \
    function##_fp = (function##_type) \
        (dlsym(symHandle, "webkit_porting_"#function)); \
    if (function##_fp == 0) { \
        DeviceLibraryLOGD("sym %s fail", "webkit_porting_"#function); \
        break; \
    }

// static member
bool DeviceLibraryFunction::libraryInitialized = false;
base::Lock DeviceLibraryFunction::libraryOpenLock;

bool DeviceLibraryFunction::ensureLibraryInit()
{
    base::AutoLock locked(libraryOpenLock);

    if (libraryInitialized) {
        return true;
    }

    if (!checkBrowserProcess()) {
        return false;
    }

    void *symHandle = 0;
    bool result = false;
    do {
        symHandle = dlopen("/system/lib/libiptvMain.so", RTLD_LAZY);
        if (symHandle == 0) {
            break;
        }

        SYM_DEVICE_FUNCTION(initCtc);
        SYM_DEVICE_FUNCTION(deviceRead);
        SYM_DEVICE_FUNCTION(deviceWrite);
        SYM_DEVICE_FUNCTION(getAuthInfo);
        SYM_DEVICE_FUNCTION(setVideoWindow);
        SYM_DEVICE_FUNCTION(mediaDuration);
        SYM_DEVICE_FUNCTION(getAudioTrack);
        SYM_DEVICE_FUNCTION(getSubtitle);
        SYM_DEVICE_FUNCTION(joinChannel);
        SYM_DEVICE_FUNCTION(play);
        SYM_DEVICE_FUNCTION(seek);
        SYM_DEVICE_FUNCTION(pause);
        SYM_DEVICE_FUNCTION(forward);
        SYM_DEVICE_FUNCTION(rewind);
        SYM_DEVICE_FUNCTION(resume);
        SYM_DEVICE_FUNCTION(gotoEnd);
        SYM_DEVICE_FUNCTION(stop);
        SYM_DEVICE_FUNCTION(switchMode);
        SYM_DEVICE_FUNCTION(setAudioPID);
        SYM_DEVICE_FUNCTION(getAudioPID);
        SYM_DEVICE_FUNCTION(getAudioPIDs);
        SYM_DEVICE_FUNCTION(setSubtitlePID);
        SYM_DEVICE_FUNCTION(getSubtitlePID);
        SYM_DEVICE_FUNCTION(getSubtitlePIDs);
        SYM_DEVICE_FUNCTION(setBrowserMediaProcessorHandle);
        SYM_DEVICE_FUNCTION(regSendVKCallback);
        SYM_DEVICE_FUNCTION(regSetVideoWindowCallback);

        browserMediaProcessor mpHandle;
        mpHandle.GetPlayMode = MediaProcessorInterface::GetPlayMode;
        mpHandle.SetVideoWindow = MediaProcessorInterface::SetVideoWindow;
        mpHandle.VideoShow = MediaProcessorInterface::VideoShow;
        mpHandle.VideoHide = MediaProcessorInterface::VideoHide;
        mpHandle.InitVideo = MediaProcessorInterface::InitVideo;
        mpHandle.InitAudio = MediaProcessorInterface::InitAudio;
        mpHandle.StartPlay = MediaProcessorInterface::StartPlay;
        mpHandle.WriteData = MediaProcessorInterface::WriteData;
        mpHandle.Pause = MediaProcessorInterface::Pause;
        mpHandle.Resume = MediaProcessorInterface::Resume;
        mpHandle.Fast = MediaProcessorInterface::Fast;
        mpHandle.StopFast = MediaProcessorInterface::StopFast;
        mpHandle.Stop = MediaProcessorInterface::Stop;
        mpHandle.Seek = MediaProcessorInterface::Seek;
        mpHandle.SetVolume = MediaProcessorInterface::SetVolume;
        mpHandle.GetVolume = MediaProcessorInterface::GetVolume;
        mpHandle.GetAudioMute = MediaProcessorInterface::GetAudioMute;
        mpHandle.SetAudioMute = MediaProcessorInterface::SetAudioMute;
        mpHandle.SetRatio = MediaProcessorInterface::SetRatio;
        mpHandle.GetAudioBalance = MediaProcessorInterface::GetAudioBalance;
        mpHandle.SetAudioBalance = MediaProcessorInterface::SetAudioBalance;
        mpHandle.GetVideoPixels = MediaProcessorInterface::GetVideoPixels;
        mpHandle.IsSoftFit = MediaProcessorInterface::IsSoftFit;
        mpHandle.SetEPGSize = MediaProcessorInterface::SetEPGSize;
        mpHandle.SwitchAudioTrack = MediaProcessorInterface::SwitchAudioTrack;
        mpHandle.SwitchSubtitle = MediaProcessorInterface::SwitchSubtitle;
        mpHandle.SetProperty = MediaProcessorInterface::SetProperty;
        mpHandle.GetCurrentPlayTime = MediaProcessorInterface::GetCurrentPlayTime;
        mpHandle.leaveChannel = MediaProcessorInterface::LeaveChannel;
        mpHandle.playerback_register_evt_cb = MediaProcessorInterface::Playerback_register_evt_cb;
        mpHandle.SetEos = MediaProcessorInterface::SetEos;
        mpHandle.EnableSubtitle = MediaProcessorInterface::EnableSubtitle;

        setBrowserMediaProcessorHandle_fp(&mpHandle);
        regSendVKCallback_fp(content::iptv::DeviceCallback::SendVK);
        regSetVideoWindowCallback_fp(content::iptv::DeviceCallback::SetVideoWindow);

        initCtc_fp();
        result = true;
    } while(false);

    libraryInitialized = result;
    return libraryInitialized;
}

bool DeviceLibraryFunction::checkBrowserProcess()
{
    base::ProcessId pid = base::Process::Current().Pid();

    std::ostringstream oss;
    oss << "/proc/" << pid << "/cmdline";

    std::string procName;
    base::ReadFileToString(base::FilePath(oss.str()), &procName);
    DeviceLibraryLOGD("proc %s", procName.c_str());

    if (procName.find(":sandboxed_process") != std::string::npos
        || procName.find(":privileged_process") != std::string::npos) {
        return false;
    }

    return true;
}

}
