#include "CTsPlayer.h"
#include "CTC_MediaControl.h"
#include "CTC_MediaProcessor.h"

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <android/log.h>  
	  

/*
FILE *fopen(const char *path, const char *mode);
size_t fread(void *ptr, size_t size, size_t nmemb, FILE *stream);

*/

int osd_blank(char *path,int cmd)
{
    int fd;
    char  bcmd[16];
    fd = open(path, O_CREAT|O_RDWR | O_TRUNC, 0644);

    if(fd>=0) {
        sprintf(bcmd,"%d",cmd);
        write(fd,bcmd,strlen(bcmd));
        close(fd);
        return 0;
    }

    return -1;
}

unsigned char buffer[1024*64];
int main(int argc,char **argv)
{
	CTC_MediaProcessor *player=new CTC_MediaProcessor();
	VIDEO_PARA_T VideoPara;
	AUDIO_PARA_T AudioPara;
	FILE *file;
	char *filename=NULL;
	
	int ret;
	int bufdatalen=0;
	int writelen;
	int w_test=0,h_test=0;
	if(argc<2){
		printf("usage %s filenme\n",argv[0]);
	//	return 0;
	}
	memset(&VideoPara,0,sizeof(VideoPara));
	memset(&AudioPara,0,sizeof(AudioPara));
	filename=argv[1];
	VideoPara.vFmt=VFORMAT_H264;
	VideoPara.pid=2064;
	AudioPara.aFmt=AFORMAT_MPEG;
	AudioPara.pid=2068;
	player->GetVideoPixels(w_test,h_test);
	__android_log_print(ANDROID_LOG_INFO, "TsPlayer_test", "GetVideoPixels----width:%d,height:%d",w_test,h_test);
	player->InitVideo(&VideoPara);
	player->InitAudio(&AudioPara);
	if(!player->StartPlay()){
		printf("Player start failed\n");
		return 0;
	}
	file=fopen(filename,"r");
	if(file==NULL){
		printf("open file %s failed\n",filename);
		return 0;
	}
	player->SetVideoWindow(0,0,-1,-1);//
	player->SetColorKey(1,0);
	//player->VideoShow();
	osd_blank("/sys/class/graphics/fb0/blank",1);//clear all osd0 ,don't need it on APK
    osd_blank("/sys/class/graphics/fb1/blank",1);//clear all osd1 ,don't need it on APK
	while(!feof(file)){
		
		if(bufdatalen<=0){
			ret=fread(buffer,1024*32,1,file);
			if(ret>0)
				bufdatalen=ret*1024*32;
			else{
				printf("read data failed %d\n",ret);
				break;;
			}
				
		}
		writelen=player->WriteData(buffer,bufdatalen);
		printf("WriteData bufdatalen=%d,writelen=%d\n",bufdatalen,writelen);
		if(writelen>0 && writelen!=bufdatalen ){
			memcpy(buffer,buffer+writelen,bufdatalen-writelen);
			usleep(10000);
		}
		if(writelen>0)
			bufdatalen-=writelen;
	}
	//player->VideoHide();
	printf("playfile %s end\n",filename);
}


