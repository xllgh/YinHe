#ifndef CONTENT_IPTV_AUTHENTICATIONCTC_H_
#define CONTENT_IPTV_AUTHENTICATIONCTC_H_

#include <string>

namespace content {
namespace iptv {

class AuthenticationCTC {
public:
    static std::string CTCGetConfig(const std::string &fieldName);
    static void CTCSetConfig(const std::string &fieldName, const std::string &fieldValue);
    static std::string CTCGetAuthInfo(const std::string &encryToken);
    static void CTCStartUpdate();
    static void CTCWritelog(const std::string &log);
private:
    static std::string size2mode(const std::string &size);
};

}
}

#endif