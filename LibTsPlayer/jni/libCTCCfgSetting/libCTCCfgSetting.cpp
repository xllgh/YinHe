/*
 * author: bo.cao@amlogic.com
 * date: 2012-11-02
 * wrap original source code for CTC usage
 */

#include "libCTCCfgSetting.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <cutils/log.h>
/*
SettingUtils::SettingUtils()
{
	
}

SettingUtils::~SettingUtils()
{
	
}
*/
int SettingUtils::SetIpMode(int flag)
{
		return 0;
}

int SettingUtils::GetIpMode()
{
		return 0;
}

int SettingUtils::SetAudioMode(int flag)
{
		int fd, ret;
    fd = open("/sys/class/audiodsp/digital_raw", O_RDWR);
    if(fd < 0) {
        LOGE("=CTCCfgSetting=> open /sys/class/audiodsp/digital_raw ERROR(%s)!!\n",strerror(errno));
        return -1;
    }
		switch(flag)
		{
			case 0:			//pcm
        ret = write(fd, "0", sizeof("0"));
				break;
			case 3:     //PASS-THROUGH
				ret = write(fd, "2", sizeof("2"));
				break;
		}
    close(fd);
    if(ret >= 0)
			return 0;
		else
			return -1;
}

int SettingUtils::GetAudioMode()
{
		int fd;
		char audiomode_str[32];
		int audioMode = 0;
		memset(audiomode_str,0,32);	
    fd = open("/sys/class/audiodsp/digital_raw", O_RDWR);
    if(fd < 0) {
        LOGE("=CTCCfgSetting=> open /sys/class/audiodsp/digital_raw ERROR(%s)!!\n",strerror(errno));
        return -1;
    }
		read(fd, audiomode_str, 32);
		audioMode = atoi(audiomode_str);
		switch(audioMode)
		{
			case 0:
				return 0;
			case 2:
				return 3;
		}
		return -1;
}

int SettingUtils::GetSaveMode()
{
		return 0;
}
