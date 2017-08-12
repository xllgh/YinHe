/*
 * author: wei.liu@amlogic.com
 * date: 2012-07-12
 * wrap original source code for CTC usage
 */

#include "CTC_MediaControl.h"
#include "CTsOmxPlayer.h"
#include <cutils/properties.h>
// need single instance?
ITsPlayer* GetMediaControl()
{
    char value[PROPERTY_VALUE_MAX] = {0};
    property_get("iptv.decoder.omx", value, "0");
    int prop_use_omxdecoder = atoi(value);

    if (prop_use_omxdecoder)
        return new CTsOmxPlayer();
    else
        return new CTsPlayer();
}

int Get_MediaControlVersion()
{
	return 1;
}
