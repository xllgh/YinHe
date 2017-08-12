#include "AuthenticationCTC.h"
#include "jni/AuthenticationCTC_jni.h"
#include "device_interface.h"
#include <map>

namespace content {
namespace iptv {

using yinhe::DeviceInterface;

std::string AuthenticationCTC::CTCGetConfig(const std::string &fieldName)
{
    return DeviceInterface::deviceRead(fieldName);
}

void AuthenticationCTC::CTCSetConfig(const std::string &fieldName,
                                     const std::string &fieldValue)
{
    if (fieldName == "page-view-size") {
        CTCSetConfig("SetEpgMode", size2mode(fieldValue));
        return;
    }

    DeviceInterface::deviceWrite(fieldName, fieldValue);
}

std::string AuthenticationCTC::CTCGetAuthInfo(const std::string &encryToken)
{
    return DeviceInterface::getAuthInfo(encryToken);
}

void AuthenticationCTC::CTCStartUpdate()
{
    JNIEnv* env = base::android::AttachCurrentThread();
    Java_AuthenticationCTC_CTCStartUpdate(env);
}

void AuthenticationCTC::CTCWritelog(const std::string &log)
{
}

std::string AuthenticationCTC::size2mode(const std::string &size)
{
    static std::map<std::string, std::string> sizeModeMap;
    if (sizeModeMap.empty()) {
        // PAL
        sizeModeMap["644*534"] = "PAL";
        sizeModeMap["640*530"] = "PAL";
        sizeModeMap["640*526"] = "PAL";

        // 720P
        sizeModeMap["1280*720"] = "720P";
        sizeModeMap["1279*719"] = "720P";

        // 1080I
        sizeModeMap["1920*1080"] = "1080I";
    }

    std::map<std::string, std::string>::const_iterator iter =
        sizeModeMap.find(size);
    if (iter == sizeModeMap.end()) {
        return std::string();
    }

    return iter->second;
}

}
}