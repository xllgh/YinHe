#ifndef CONTENT_IPTV_UTILITYCTC_H_
#define CONTENT_IPTV_UTILITYCTC_H_

#include <string>

namespace content {
namespace iptv {

class UtilityCTC {
public:
    static std::string getEvent();
    static void startLocalCfg();
    static int setValueByName(const std::string &name, const std::string &value);
    static std::string getValueByName(const std::string &name);
};

}
}

#endif