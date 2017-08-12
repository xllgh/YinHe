/**
 * @file 				Proxy_MediaProcessor.h
 * @author    	zhouyj
 * @date      	2012/9/12
 * @version   	ver 1.0
 * @brief     	定义CTC_MediaControl类中方法的代理接口。
 * @attention
*/
#ifndef _PROXY_MEDIA_PROCESSOR_H_
#define _PROXY_MEDIA_PROCESSOR_H_

#include <CTC_MediaControl.h>
#include "vformat.h"
#include "aformat.h"
//#include "AudioBufferProvider.h"
//#include "Common.h"
#include "android_runtime/AndroidRuntime.h"
#include <gui/Surface.h> 
#include "android_runtime/android_view_Surface.h"
//#include "CTC_MediaProcessor.h"
using namespace android;

class Proxy_MediaProcessor
{
	protected:
		ITsPlayer* ctc_MediaControl;
	public:
		Proxy_MediaProcessor();
		~Proxy_MediaProcessor();
	public:
	//	CTC_MediaControl* Proxy_GetMediaControl();
		int  Proxy_GetMediaControlVersion();//获取版本
		int  Proxy_GetPlayMode();//取得播放模式
		int  Proxy_SetVideoWindow(int x,int y,int width,int height);//设置视频显示的位置，以及视频显示的宽高
		int  Proxy_VideoShow(void);//显示视频图像
		int  Proxy_VideoHide(void);//隐藏视频图像
		void Proxy_InitVideo(PVIDEO_PARA_T pVideoPara);//初始化视频参数
		void Proxy_InitAudio(PAUDIO_PARA_T pAudioPara);//初始化音频参数
		bool Proxy_StartPlay();//开始播放
		int  Proxy_WriteData(unsigned char* pBuffer, unsigned int nSize);//将ts流写入缓冲区
		bool Proxy_Pause();//暂停
		bool Proxy_Resume();//暂停后的恢复
		bool Proxy_Fast();//快进或者快退
		bool Proxy_StopFast();//停止快进或者快退
		bool Proxy_Stop();//停止
		bool Proxy_Seek();//定位
		bool Proxy_SetVolume(int volume);//设定音量
		int  Proxy_GetVolume();//获取音量
		bool Proxy_SetRatio(int nRatio);//设定视频显示比例
		int  Proxy_GetAudioBalance();//获取当前声道
		bool Proxy_SetAudioBalance(int nAudioBalance);//设置声道
		void Proxy_GetVideoPixels(int& width, int& height);//获取视频分辩率
		bool Proxy_IsSoftFit();//判断是否由软件拉伸
		void Proxy_SetEPGSize(int w, int h);//设置EPG大小
		void Proxy_SetSurface(Surface* pSurface);//设置显示用的surface

		int Proxy_GetCurrentPlayTime();
		void Proxy_InitSubtitle(PSUBTITLE_PARA_T sParam);
		void Proxy_SwitchSubtitle(int pid);//设置显示用的surface		
		void Proxy_playerback_register_evt_cb(IPTV_PLAYER_EVT_CB pfunc, void *hander);
};

#endif
