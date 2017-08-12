#ifndef _TSPLAYER_H_
#define _TSPLAYER_H_
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
#include <surfaceflinger/Surface.h>
#include <surfaceflinger/ISurface.h>
#include <gui/ISurfaceTexture.h>
#include <gui/SurfaceTextureClient.h>
#include <surfaceflinger/ISurfaceComposer.h>
using namespace android;


extern "C" {
#include <amports/vformat.h>
#include <amports/aformat.h>
#include <codec.h>
}
#include <string.h>
#include <utils/Timers.h>




#define WF

#define TRICKMODE_NONE       0x00
#define TRICKMODE_I          0x01
#define TRICKMODE_FFFB       0x02
#define MAX_AUDIO_PARAM_SIZE 10

typedef struct{
	unsigned short	pid;//pid
	int				nVideoWidth;//视频宽度
	int				nVideoHeight;//视频高度
	int				nFrameRate;//帧率
	vformat_t		vFmt;//视频格式
	unsigned long	cFmt;//编码格式
}VIDEO_PARA_T, *PVIDEO_PARA_T;
typedef struct{
	unsigned short	pid;//pid
	int				nChannels;//声道数
	int				nSampleRate;//采样率
	aformat_t		aFmt;//音频格式
	int				nExtraSize;
	unsigned char*	pExtraData;	
}AUDIO_PARA_T, *PAUDIO_PARA_T;
typedef enum
{
    IPTV_PLAYER_EVT_STREAM_VALID=0,
    IPTV_PLAYER_EVT_FIRST_PTS,   //解出第一帧
    IPTV_PLAYER_EVT_VOD_EOS,    //VOD播放完毕
    IPTV_PLAYER_EVT_ABEND,         //为上报下溢事件而增加的类型
    IPTV_PLAYER_EVT_PLAYBACK_ERROR,	// 播放错误
}IPTV_PLAYER_EVT_e;

typedef void (*IPTV_PLAYER_EVT_CB)(IPTV_PLAYER_EVT_e evt, void *handler);


int enable_gl_2xscale(const char *);

int Active_osd_viewport(int , int );

class CTsPlayer;
class ITsPlayer{
public:
	ITsPlayer(){}
	virtual ~ITsPlayer(){}
public:
	virtual int  GetPlayMode()=0;
	//显示窗口
	virtual int  SetVideoWindow(int x,int y,int width,int height)=0;
	//x显示视频
	virtual int  VideoShow(void)=0;
	//隐藏视频
	virtual int  VideoHide(void)=0;
	//初始化视频参数
	virtual void InitVideo(PVIDEO_PARA_T pVideoPara)=0;
	//初始化音频参数
	virtual void InitAudio(PAUDIO_PARA_T pAudioPara)=0;
	//开始播放
	virtual bool StartPlay()=0;
	//把ts流写入
	virtual int WriteData(unsigned char* pBuffer, unsigned int nSize)=0;
	//暂停
	virtual bool Pause()=0;
	//继续播放
	virtual bool Resume()=0;
	//快进快退
	virtual bool Fast()=0;
	//停止快进快退
	virtual bool StopFast()=0;
	//停止
	virtual bool Stop()=0;
    //定位
    virtual bool Seek()=0;
    //设定音量
	//设定音量
	virtual bool SetVolume(int volume)=0;
	//获取音量
	virtual int GetVolume()=0;
	//设定视频显示比例
	virtual bool SetRatio(int nRatio)=0;
	//获取当前声道
	virtual int GetAudioBalance()=0;
	//设置声道
	virtual bool SetAudioBalance(int nAudioBalance)=0;
	//获取视频分辩率
	virtual void GetVideoPixels(int& width, int& height)=0;
	virtual bool IsSoftFit()=0;
	virtual void SetEPGSize(int w, int h)=0;
	
	virtual void SetSurface(Surface* pSurface);
	//16位色深需要设置colorkey来透出视频；
     virtual void SwitchAudioTrack(int pid) = 0;
     virtual void SwitchSubtitle(int pid) = 0;
     virtual void SetProperty(int nType, int nSub, int nValue) = 0;
     virtual long GetCurrentPlayTime() = 0;
     virtual void leaveChannel() = 0;
	virtual void playerback_register_evt_cb(IPTV_PLAYER_EVT_CB pfunc, void *hander) = 0;
};

class CTsPlayer : public ITsPlayer
{
public:
	CTsPlayer();
	virtual ~CTsPlayer();
public:
	//取得播放模式
	virtual int  GetPlayMode();
	//显示窗口
	virtual int  SetVideoWindow(int x,int y,int width,int height);
	//x显示视频
	virtual int  VideoShow(void);
	//隐藏视频
	virtual int  VideoHide(void);
	//初始化视频参数
	virtual void InitVideo(PVIDEO_PARA_T pVideoPara);
	//初始化音频参数
	virtual void InitAudio(PAUDIO_PARA_T pAudioPara);
	//开始播放
	virtual bool StartPlay();
	//把ts流写入
	virtual int WriteData(unsigned char* pBuffer, unsigned int nSize);
	//暂停
	virtual bool Pause();
	//继续播放
	virtual bool Resume();
	//快进快退
	virtual bool Fast();
	//停止快进快退
	virtual bool StopFast();
	//停止
	virtual bool Stop();
	//定位
    	virtual bool Seek();
        //设定音量
	//设定音量
	virtual bool SetVolume(int volume);
	//获取音量
	virtual int GetVolume();
	//设定视频显示比例
	virtual bool SetRatio(int nRatio);
	//获取当前声道
	virtual int GetAudioBalance();
	//设置声道
	virtual bool SetAudioBalance(int nAudioBalance);
	//获取视频分辩率
	virtual void GetVideoPixels(int& width, int& height);
	virtual bool IsSoftFit();
	virtual void SetEPGSize(int w, int h);

	virtual void SetSurface(Surface* pSurface);
	//16位色深需要设置colorkey来透出视频；

    virtual void SwitchAudioTrack(int pid) ;

    virtual void SwitchSubtitle(int pid) ;
    
    virtual void SetProperty(int nType, int nSub, int nValue) ;
    
    virtual long GetCurrentPlayTime() ;
    
    virtual void leaveChannel() ;
	virtual void playerback_register_evt_cb(IPTV_PLAYER_EVT_CB pfunc, void *hander);
	
protected:
	int		m_bLeaveChannel;
	
private:
	AUDIO_PARA_T a_aPara[MAX_AUDIO_PARAM_SIZE];
	VIDEO_PARA_T vPara;	
	int player_pid;
	codec_para_t codec;
	codec_para_t *pcodec;
	bool		m_bIsPlay;
	int			m_nOsdBpp;
	int			m_nAudioBalance;
	int			m_nVolume;
	int	m_nEPGWidth;
	int 	m_nEPGHeight;
	bool	m_bFast;
	bool 	m_bSetEPGSize;
    bool    m_bWrFirstPkg;
	int	m_nMode;
    IPTV_PLAYER_EVT_CB pfunc_player_evt;
    void *player_evt_hander;
#ifdef WF
	FILE*	m_fp;
#endif

};

#endif
