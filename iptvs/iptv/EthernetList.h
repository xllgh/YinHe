#include <string>
#include "base/json/json_writer.h"
#include "base/memory/ptr_util.h"
#include "base/values.h"
#include "build/build_config.h"
namespace content {
namespace iptv {

class EthernetList {
public:
    static unsigned int length();
    static std::string getNetCardInfo();
};

}
}