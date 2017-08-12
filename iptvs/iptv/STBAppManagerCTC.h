#ifndef CONTENT_IPTV_STBAPPMANAGERCTC_H_
#define CONTENT_IPTV_STBAPPMANAGERCTC_H_

#include <string>

namespace content {
namespace iptv {

class STBAppManagerCTC {
public:
    static bool isAppInstalled(const std::string &appName);
    static std::string getAppVersion(const std::string &appName);
    static void startAppByName(const std::string &appName);
    static void restartAppByName(const std::string &appName);
    static void startAppByIntent(const std::string &intentMessage);
    static void installApp(const std::string &apkUrl);
};

}
}

#endif