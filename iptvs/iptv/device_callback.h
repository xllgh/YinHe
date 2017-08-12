#ifndef CONTENT_IPTV_DEVICE_CALLBACK_H_
#define CONTENT_IPTV_DEVICE_CALLBACK_H_

#include <vector>

namespace content {
namespace iptv {

class DeviceCallback {
private:
    typedef void (*VKEventListener)(void);
    typedef void (*RefreshDisplayListener)(int, int, int, int);
public:
    static void SendVK();
    static void addVKEventListener(VKEventListener listener);

    static void SetVideoWindow(int x, int y, int w, int h);
    static void addRefreshVideoDisplayListener(RefreshDisplayListener listener);
private:
    static std::vector<VKEventListener> VKEventListenerList;
    static std::vector<RefreshDisplayListener> RefreshDisplayListenerList;
};

}
}

#endif