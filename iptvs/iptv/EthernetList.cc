#include "EthernetList.h"
#include <sys/ioctl.h>
#include <net/if.h>
#include <unistd.h>
#include <netinet/in.h>
#include <string.h>

#include <stdio.h> 
#include <iostream>  
#include <stdio.h>  
#include <stdlib.h>  
#include <unistd.h>  
#include <string.h>  
#include <sys/socket.h>  
#include <netinet/in.h>  
#include <net/if.h>  
#include <netdb.h>  
#include <arpa/inet.h>  
#include <sys/ioctl.h>
#include "android/log.h"
#undef LOGD
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "chromium", __VA_ARGS__)

namespace content {

namespace iptv {

std::string EthernetList::getNetCardInfo()
{
    std::string output_js;
    struct ifreq ifr;
    struct ifconf ifc;
    char buf[2048];
    int count = 0;
    char szMac[64];
    
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == -1) {
        LOGD("socket error");
        return "socket error";
    }
 
    ifc.ifc_len = sizeof(buf);
    ifc.ifc_buf = buf;
    if (ioctl(sock, SIOCGIFCONF, &ifc) == -1) {
        LOGD("ioctl error");
        return "ioctl error";
    }

    struct ifreq* it = ifc.ifc_req;
    const struct ifreq* const end = it + (ifc.ifc_len / sizeof(struct ifreq));
    
    base::DictionaryValue netCardInfo;
    std::unique_ptr<base::ListValue> list(new base::ListValue());
    
    for (; it != end; ++it) {
        std::unique_ptr<base::DictionaryValue> inner_dict(new base::DictionaryValue());
        strcpy(ifr.ifr_name, it->ifr_name);
        if (ioctl(sock, SIOCGIFFLAGS, &ifr) == 0) {
            if (ifr.ifr_flags & IFF_RUNNING){
                        inner_dict->SetInteger("LANStatus", 1);
                    }else{
				        inner_dict->SetInteger("LANStatus", 0);
			        }
                if (! (ifr.ifr_flags & IFF_LOOPBACK)) { // don't count loopback
                    if (ioctl(sock, SIOCGIFHWADDR, &ifr) == 0) {
                        count ++ ;
                        unsigned char * ptr;
                        ptr = (unsigned char  *)&ifr.ifr_ifru.ifru_hwaddr.sa_data[0];
                        snprintf(szMac,64,"%02X-%02X-%02X-%02X-%02X-%02X",*ptr,*(ptr+1),*(ptr+2),*(ptr+3),*(ptr+4),*(ptr+5));
                        
		                inner_dict->SetString("name", ifr.ifr_name);
                        inner_dict->SetString("address", szMac);
         
                        list->Append(std::move(inner_dict));
               }
            }
        }else{
            LOGD("get mac info error");
            return "get mac info error";
        }
    }
    netCardInfo.SetInteger("CardNum", count);
    netCardInfo.Set("list", std::move(list));
    // Test the pretty-printer.
    base::JSONWriter::Write(netCardInfo, &output_js); 
    return output_js;
}

unsigned int EthernetList::length()
{
    return 0;
}

}
}