/*
 * author: bo.cao@amlogic.com
 * date: 2012-11-02
 * wrap original source code for CTC usage
 */

#ifndef _CTC_CFG_SETTING_H_
#define _CTC_CFG_SETTING_H_

#include <android/log.h>    
#include <pthread.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <pthread.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>

class SettingUtils
{
public:
	SettingUtils(){}
	virtual ~SettingUtils(){}
public:
	virtual int SetIpMode(int flag)=0;
	//设置IP模式
	virtual int GetIpMode()=0;
	//获取IP模式
	virtual int SetAudioMode(int flag)=0;
	//设置音频模式
	virtual int GetAudioMode()=0;
	//获取音频模式
	virtual int GetSaveMode()=0;
	//获取存储模式
};


#endif  // _CTC_CFG_SETTING_H_
