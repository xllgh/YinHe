#include "UtilityCTC.h"
#include "jni/UtilityCTC_jni.h"
#include "device_interface.h"

namespace content {
namespace iptv {

using yinhe::DeviceInterface;

std::string UtilityCTC::getEvent()
{
    return DeviceInterface::getEvent();
}

void UtilityCTC::startLocalCfg()
{
    JNIEnv* env = base::android::AttachCurrentThread();
    Java_UtilityCTC_startLocalCfg(env);
}

int UtilityCTC::setValueByName(const std::string &name, const std::string &value)
{
    DeviceInterface::setValueByName(name, value);
    return 0;
}

std::string UtilityCTC::getValueByName(const std::string &name)
{
    return DeviceInterface::getValueByName(name);
}

}
}