#include "device_callback.h"
#include "jni/DeviceCallback_jni.h"
#include "algorithm"

namespace content {
namespace iptv {

std::vector<DeviceCallback::VKEventListener> DeviceCallback::VKEventListenerList;
std::vector<DeviceCallback::RefreshDisplayListener> DeviceCallback::RefreshDisplayListenerList;

void DeviceCallback::SendVK()
{
    for (std::vector<VKEventListener>::const_iterator iter = VKEventListenerList.begin();
        iter != VKEventListenerList.end(); ++iter) {
        (*iter)();
    }

    JNIEnv* env = base::android::AttachCurrentThread();
    Java_DeviceCallback_SendVK(env);
}

void DeviceCallback::addVKEventListener(VKEventListener listener)
{
    if (listener == NULL) {
        return;
    }

    if (std::find(VKEventListenerList.begin(), VKEventListenerList.end(), listener)
             == VKEventListenerList.end()) {
        VKEventListenerList.push_back(listener);
    }
}

void DeviceCallback::SetVideoWindow(int x, int y, int w, int h)
{
    for (std::vector<RefreshDisplayListener>::const_iterator iter =
            RefreshDisplayListenerList.begin();
            iter != RefreshDisplayListenerList.end(); ++iter) {
        (*iter)(x, y, w, h);
    }
}

void DeviceCallback::addRefreshVideoDisplayListener(RefreshDisplayListener listener)
{
    if (listener == NULL) {
        return;
    }

    if (std::find(RefreshDisplayListenerList.begin(), RefreshDisplayListenerList.end(), listener)
             == RefreshDisplayListenerList.end()) {
        RefreshDisplayListenerList.push_back(listener);
    }
}

}
}
