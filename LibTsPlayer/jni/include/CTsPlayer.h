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
#include <gui/Surface.h>

using namespace android;
extern "C" {
#include <amports/vformat.h>
#include <amports/aformat.h>
#include <amports/amstream.h>
#include <codec.h>
}

#include <string.h>
#include <utils/Timers.h>


#define lock_t          pthread_mutex_t
#define lp_lock_init(x,v)   pthread_mutex_init(x,v)
#define lp_lock(x)      pthread_mutex_lock(x)
#define lp_unlock(x)    pthread_mutex_unlock(x)



#define IN
#define OUT

typedef struct{
	void *(*init)(IN int flags);
  int (*read)(IN void *handle, IN unsigned char *buf, IN int size);
  int (*ready)(IN void *handle);
  int (*exit)(IN void *handle);
}wiptv_callback;


#define TRICKMODE_NONE       0x00
#define TRICKMODE_I          0x01
#define TRICKMODE_I_HEVC     0x07
#define TRICKMODE_FFFB       0x02
#define MAX_AUDIO_PARAM_SIZE 10
#define MAX_SUBTITLE_PARAM_SIZE 10

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

#ifndef AVCODEC_AVCODEC_H
typedef enum {
	/* subtitle codecs */
    CODEC_ID_DVD_SUBTITLE= 0x17000,
    CODEC_ID_DVB_SUBTITLE,
    CODEC_ID_TEXT,  ///< raw UTF-8 text
    CODEC_ID_XSUB,
    CODEC_ID_SSA,
    CODEC_ID_MOV_TEXT,
    CODEC_ID_HDMV_PGS_SUBTITLE,
    CODEC_ID_DVB_TELETEXT,
    CODEC_ID_SRT,
    CODEC_ID_MICRODVD,
	}SUB_TYPE;
#endif
typedef struct{
	unsigned short	pid;//pid
	int sub_type; 
	
}SUBTITLE_PARA_T, *PSUBTITLE_PARA_T;

typedef struct ST_LPbuffer{
    unsigned char *rp;
    unsigned char *wp;
    unsigned char *buffer;
    unsigned char *bufferend;
    int valid_can_read;
    bool enlpflag;
}LPBUFFER_T;



typedef enum{    
	IPTV_PLAYER_EVT_STREAM_VALID=0,    
	IPTV_PLAYER_EVT_FIRST_PTS,     //first frame decoded event    
	IPTV_PLAYER_EVT_VOD_EOS,       //VOD EOS event    
	IPTV_PLAYER_EVT_ABEND,         //under flow event    
	IPTV_PLAYER_EVT_PLAYBACK_ERROR,// playback error event    
	IPTV_PLAYER_EVT_VID_FRAME_ERROR =0x200,// 视频解码错误    
	IPTV_PLAYER_EVT_VID_DISCARD_FRAME,// 视频解码丢帧    
	IPTV_PLAYER_EVT_VID_DEC_UNDERFLOW,// 视频解码下溢    
	IPTV_PLAYER_EVT_VID_PTS_ERROR,// 视频解码Pts错误    
	IPTV_PLAYER_EVT_AUD_FRAME_ERROR,// 音频解码错误    
	IPTV_PLAYER_EVT_AUD_DISCARD_FRAME,// 音频解码丢弃    
	IPTV_PLAYER_EVT_AUD_DEC_UNDERFLOW,//音频解码下溢    
	IPTV_PLAYER_EVT_AUD_PTS_ERROR,// 音频PTS错误    
	IPTV_PLAYER_EVT_BUTT    
}IPTV_PLAYER_EVT_e;

typedef enum{    
	IPTV_PLAYER_ATTR_VID_ASPECT=0,  /* 视频宽高比 0--640*480，1--720*576，2--1280*720，3--1920*1080,4--3840*2160,5--others等标识指定分辨率*/    
	IPTV_PLAYER_ATTR_VID_RATIO,     //视频宽高比, 0代表4：3，1代表16：9    
	IPTV_PLAYER_ATTR_VID_SAMPLETYPE,     //帧场模式, 1代表逐行源，0代表隔行源    
	IPTV_PLAYER_ATTR_VIDAUDDIFF,     //音视频播放diff    
	IPTV_PLAYER_ATTR_VID_BUF_SIZE,     //视频缓冲区大小    
	IPTV_PLAYER_ATTR_VID_USED_SIZE,     //视频缓冲区使用大小    
	IPTV_PLAYER_ATTR_AUD_BUF_SIZE,     //音频缓冲区大小    
	IPTV_PLAYER_ATTR_AUD_USED_SIZE,     //音频缓冲区已使用大小    
	IPTV_PLAYER_ATTR_BUTT}
IPTV_ATTR_TYPE_e;


typedef void (*IPTV_PLAYER_EVT_CB)(IPTV_PLAYER_EVT_e evt, void *handler);

typedef struct {
    int abuf_size;
    int abuf_data_len;
    int abuf_free_len;
    int vbuf_size;
    int vbuf_data_len;
    int vbuf_free_len;
}AVBUF_STATUS, *PAVBUF_STATUS;

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
    virtual void SetSurface(Surface* pSurface)=0;
	
	//16位色深需要设置colorkey来透出视频；
     virtual void SwitchAudioTrack(int pid) = 0;
     virtual void SwitchSubtitle(int pid) = 0;
     virtual void SetProperty(int nType, int nSub, int nValue) = 0;
     virtual int64_t GetCurrentPlayTime() = 0;
     virtual void leaveChannel() = 0;
	virtual void playerback_register_evt_cb(IPTV_PLAYER_EVT_CB pfunc, void *hander) = 0;
	virtual int playerback_getStatusInfo(IPTV_ATTR_TYPE_e enAttrType, int *value)=0;
    virtual int GetRealTimeFrameRate() = 0;
    virtual int GetVideoFrameRate() = 0;
    virtual int GetVideoDropNumber() = 0;
	virtual int GetVideoTotalNumber() = 0;
	virtual void InitSubtitle(PSUBTITLE_PARA_T pSubtitlePara)=0;
	virtual bool SubtitleShowHide(bool bShow) = 0;
};

class CTsPlayer : public ITsPlayer
{
public:
	CTsPlayer();
#ifdef USE_OPTEEOS
	CTsPlayer(bool DRMMode);
	CTsPlayer(bool DRMMode, bool omx_player);
#else
	CTsPlayer(bool omx_player);
#endif
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
	//
	virtual void InitSubtitle(PSUBTITLE_PARA_T pSubtitlePara);
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
    
    virtual int64_t GetCurrentPlayTime() ;
    
    virtual void leaveChannel() ;
	virtual void playerback_register_evt_cb(IPTV_PLAYER_EVT_CB pfunc, void *hander);
	virtual int playerback_getStatusInfo(IPTV_ATTR_TYPE_e enAttrType, int *value);
    virtual int GetRealTimeFrameRate();
    virtual int GetVideoFrameRate();
	virtual bool SubtitleShowHide(bool bShow);
    virtual int GetVideoDropNumber();
    virtual void Report_video_paramters();
    //virtual void Report_Audio_paramters();
    virtual int GetVideoTotalNumber();
    virtual void readExtractor();
    virtual int updateCTCInfo();
    /*end add*/
    bool mIsOmxPlayer;
	
protected:
	int		m_bLeaveChannel;
	
private:
	AUDIO_PARA_T a_aPara[MAX_AUDIO_PARAM_SIZE];
	SUBTITLE_PARA_T sPara[MAX_SUBTITLE_PARAM_SIZE];
	VIDEO_PARA_T vPara;	
	int player_pid;
	codec_para_t codec;
	codec_para_t *pcodec;
	codec_para_t    *vcodec;
    codec_para_t    *acodec;
    codec_para_t    *scodec;
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
	unsigned int writecount ;
	int64_t  m_StartPlayTimePoint;
	bool    m_isSoftFit;
	FILE*	m_fp;
    lock_t mutex;
    pthread_t mThread;
	pthread_t readThread;
    virtual void checkAbend();
    virtual void checkBuffLevel();
    virtual void checkBuffstate();
    static void *threadCheckAbend(void *pthis);
	static void *threadReadPacket(void *pthis);
    bool    m_isBlackoutPolicy;
    bool m_bchangeH264to4k;
    lock_t mutex_lp;
    void checkVdecstate();
    bool        m_bIsPause;
    virtual bool  iStartPlay( );
    virtual bool  iStop( );
    int64_t m_PreviousOverflowTime;
	size_t mInputQueueSize;
};

#endif
