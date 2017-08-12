#define LOG_NDEBUG 0
#define LOG_TAG "ctc_omx_player"

#include "CTsOmxPlayer.h"
#include <ui/DisplayInfo.h>
#include <sys/system_properties.h>
#include <cutils/properties.h>

#define _GNU_SOURCE
#define F_SETPIPE_SZ        (F_LINUX_SPECIFIC_BASE + 7)
#define F_GETPIPE_SZ        (F_LINUX_SPECIFIC_BASE + 8)
#include <fcntl.h>
#include "ffextractor.h"

#define LOG_LINE() ALOGV("[%s:%d] >", __FUNCTION__, __LINE__);

using namespace android;

static int s_nDumpTs = 0;
static int pipe_fd[2] = { -1, -1 };
static bool ffextractor_inited = false;
int read_cb(void *opaque, uint8_t *buf, int size) {
	int ret = read(pipe_fd[0], buf, size);
	return ret;
}

#ifdef USE_OPTEEOS
CTsOmxPlayer::CTsOmxPlayer():CTsPlayer(false, true) {
#else
CTsOmxPlayer::CTsOmxPlayer():CTsPlayer(true) {
#endif
    LOG_LINE();
    mFp = NULL;
    mIsOmxPlayer = true;
    mSoftDecoder = true;
    mExtract = NULL;
    mFirstDisplayTime = -1;
	mFirstIDRPTS = -1;
	mSecondIDRPTS = -1;
	mIDRInterval = 0;
	mIDRStart = false;
	mFrameDuration = 33;
	mFirst_Pts = 0;
	mAccumPTS = 0;
	mPTSDrift = -15;
	ALOGD("mPTSDrift=%d", mPTSDrift);
	mFPSProbeSize = 0;
	mSoftRenderer = NULL;
	mSoftRenderWidth = 0;
	mSoftRenderHeight = 0;
	mForceStop = false;
	mIsPlaying = false;
	mLastRenderPTS = 0;
	mInputQueueSize = 0;
	mFormatMsg = new AMessage;

	ALOGI("CTsOmxPlayer::creat end\n");
    return;
}

CTsOmxPlayer::~CTsOmxPlayer() {
    LOG_LINE();
    ALOGD("disposing surface");
    mSoftComposerClient->dispose();
    return;
}

bool CTsOmxPlayer::StartPlay() {
	LOG_LINE();
    char value[PROPERTY_VALUE_MAX] = {0};

	mYUVFrameQueue.clear();

    mRunWorkerDecoder = new RunWorker(this, RunWorker::MODULE_DECODER);
    mRunWorkerExtract = new RunWorker(this, RunWorker::MODULE_EXTRACT);
    mRunWorkerVsync   = new RunWorker(this, RunWorker::MODULE_VSYNC);

    if (pipe(pipe_fd) == -1) {
        perror("pipe");
        exit(1);
    } else {
        fcntl(pipe_fd[0], F_SETPIPE_SZ, 1048576);
        fcntl(pipe_fd[1], F_SETPIPE_SZ, 1048576);
		ALOGD("pipe opened!");
    }

	if (mIsPlaying == true) {
		ALOGD("mIsPlaying is true");
		return true;
	}
    mIsPlaying = true;

    property_get("iptv.omx.dumpfile", value, "0");
    s_nDumpTs = atoi(value);
    if (s_nDumpTs == 1) {
        char tmpfilename[1024]="";
        static int s_nDumpCnt=0;
        sprintf(tmpfilename,"/storage/external_storage/sda1/Live%d.ts",s_nDumpCnt);
        s_nDumpCnt++;
        mFp = fopen(tmpfilename, "wb+");
    }
    if (mSoftComposerClient == NULL) {
        ALOGD("mSoftComposerClient is NULL, create one");
        if (!createWindowSurface())
            return false;
    }
    else
        ALOGD("mSoftComposerClient already created");
	// start extractor\decoer\vsync threads
    if (!createOmxDecoder())
        return false;
		
    return true;
}

int CTsOmxPlayer::SetVideoWindow(int x,int y,int width,int height) {
    LOG_LINE();
    mSoftNativeX = x;
    mSoftNativeY = y;
    mSoftNativeWidth = width;
    mSoftNativeHeight = height;
    ALOGI("CTsOmxPlayer::SetVideoWindow: %d, %d, %d, %d\n", x, y, width, height);
    return 0;
}
bool CTsOmxPlayer::createWindowSurface() {
	LOG_LINE();
    mSoftComposerClient = new SurfaceComposerClient;
    CHECK_EQ(mSoftComposerClient->initCheck(), (status_t)OK);
    mSoftControl = mSoftComposerClient->createSurface(
                String8("ctc_Surface"),
                mSoftNativeWidth,
                mSoftNativeHeight,
                PIXEL_FORMAT_RGBA_8888,
                0);

    CHECK(mSoftControl != NULL);
    CHECK(mSoftControl->isValid());
    SurfaceComposerClient::openGlobalTransaction();
    CHECK_EQ(mSoftControl->setLayer(INT_MAX-100), (status_t)OK);

    sp<IBinder> dpy = mSoftComposerClient->getBuiltInDisplay(0);
    if (dpy == NULL) {
        ALOGE("SurfaceComposer::getBuiltInDisplay failed");
        return false;
    }

    DisplayInfo info;
    status_t err = mSoftComposerClient->getDisplayInfo(dpy, &info);
    if (err != NO_ERROR) {
        ALOGE("SurfaceComposer::getDisplayInfo failed\n");
        return false;
    }

    float scaleX = float(info.w) / float(mApkUiWidth);
    float scaleY = float(info.h) / float(mApkUiHeight);

    mSoftControl->setPosition(mSoftNativeX * scaleX, mSoftNativeY * scaleY);
    err = mSoftControl->setMatrix(scaleX, 0.0f, 0.0f, scaleY);
    if (err != NO_ERROR) {
       ALOGE("SurfaceComposer::setMatrix error");
       return false;
    }

    CHECK_EQ(mSoftControl->show(), (status_t)OK);
    SurfaceComposerClient::closeGlobalTransaction();

    mSoftSurface = mSoftControl->getSurface();
    CHECK(mSoftSurface != NULL);
    return true;
}

bool CTsOmxPlayer::createOmxDecoder() {
	LOG_LINE();
    if ((mRunWorkerExtract.get() != NULL)
        && (mRunWorkerExtract.get()->mTaskStatus != RunWorker::STATUE_RUNNING)) {
        mRunWorkerExtract->start();
    }

    if ((mRunWorkerDecoder.get() != NULL)
        && (mRunWorkerDecoder.get()->mTaskStatus != RunWorker::STATUE_RUNNING)) {
        mRunWorkerDecoder->start();
    }

    if ((mRunWorkerVsync.get() != NULL)
        && (mRunWorkerVsync.get()->mTaskStatus != RunWorker::STATUE_RUNNING)) {
        mRunWorkerVsync->start();
    }

    return true;
}

void CTsOmxPlayer::InitVideo(PVIDEO_PARA_T pVideoPara) {
    LOG_LINE();
    mSoftVideoPara = *pVideoPara;
    ALOGI("nVideoWidth =%d, nVideoHeight=%d, pid=%u", mSoftVideoPara.nVideoWidth, mSoftVideoPara.nVideoHeight);
    if (mSoftVideoPara.nVideoWidth == 0 || mSoftVideoPara.nVideoHeight == 0) {
        ALOGE("nVideoWidth =%d, nVideoHeight=%d", mSoftVideoPara.nVideoWidth, mSoftVideoPara.nVideoHeight);
        mSoftVideoPara.nVideoWidth = 1920;
        mSoftVideoPara.nVideoHeight = 1088;
    }
    return;
}

void CTsOmxPlayer::InitAudio(PAUDIO_PARA_T pAudioPara) {
    LOG_LINE();
    return;
}

void CTsOmxPlayer::InitSubtitle(PSUBTITLE_PARA_T pSubtitlePara) {
    LOG_LINE();
    return;
}

int CTsOmxPlayer::WriteData(unsigned char* pBuffer, unsigned int nSize) {
    if(!mIsPlaying)
        return 0;

	int ret = write(pipe_fd[1], pBuffer, nSize);
    if (mFp != NULL) {
        fwrite(pBuffer, 1, nSize, mFp);
    }

    return ret;
}

bool CTsOmxPlayer::Stop() {
    ALOGI("CTsOmxPlayer Stop\n");
    LOG_LINE();
    if (mFp != NULL) {
        fclose(mFp);
        mFp = NULL;
    }
	uint8_t *tmp_buf = (uint8_t *)malloc(1024*32);
	if (tmp_buf == NULL) {
		ALOGE("malloc tmp_buf failed");
		return false;
	}
	close(pipe_fd[1]);
	while(read(pipe_fd[0], tmp_buf, 1024*32)>0);
	free(tmp_buf);
    close(pipe_fd[0]);
    
	ALOGD("pipe closed");
    mIsPlaying = false;

	mRunWorkerVsync->stop();
    ALOGD("vsync thread stopped");
	
    mRunWorkerDecoder->stop();
	ALOGD("decoder thread stopped");
	
	mRunWorkerExtract->stop();
	ALOGD("extractor thread stopped");

    if (mRunWorkerDecoder.get() != NULL) {
        mRunWorkerDecoder->stop();
        mRunWorkerDecoder = NULL;
    }
    if (mRunWorkerExtract.get() != NULL) {
        mRunWorkerExtract->stop();
        mRunWorkerExtract = NULL;
    }
    if (mRunWorkerVsync.get() != NULL) {
        mRunWorkerVsync->stop();
        mRunWorkerVsync = NULL;
    }
	LOG_LINE();
	delete mSoftRenderer;
	mSoftRenderer = NULL;
    ffextractor_deinit();
    ffextractor_inited = false;
	ALOGD("ffmpeg denited");
	releaseYUVFrames();
	mYUVFrameQueue.clear();
    LOG_LINE();
    ALOGI("CTsOmxPlayer Stop end\n");
    return true;
}

void CTsOmxPlayer::SetEPGSize(int w, int h) {
    LOG_LINE();
    ALOGV("SetEPGSize: w=%d, h=%d\n", w, h);
    mApkUiWidth = w;
    mApkUiHeight = h;
}

void CTsOmxPlayer::readExtractor() {
	if (ffextractor_inited == false) {
		MediaInfo mi;
		int try_count = 0;
		while (try_count++ < 3) {
			ALOGD("try to init ffmepg %d times", try_count);
			int ret = ffextractor_init(read_cb, &mi);
			if (ret == -1) {
				ALOGD("ffextractor_init return -1");
				continue;
			}
			if (mi.width ==0 || mi.height == 0) {
				ALOGD("invalid dimensions: %d:%d", mi.width, mi.height);
			}
			ffextractor_inited = true;
			break;
		}
	}

	if (ffextractor_inited == false) {
		ALOGE("3 Attempts to init ffextractor all failed");
		mForceStop = true;
		return;
	}
	ffextractor_read_packet(&mInputQueueSize);
}

void CTsOmxPlayer::readDecoder() {
	if (ffextractor_inited == false) {
		return;
	}
	
	{
		YUVFrame *frame = ff_decode_frame();
		if (frame == NULL || frame->data == NULL) {
			return;
		}

		{
			AutoMutex l(mYUVFrameQueueLock);
			mYUVFrameQueue.push_back(frame);
		}
	}
}

void CTsOmxPlayer::releaseYUVFrames() {
	AutoMutex l(mYUVFrameQueueLock);
	while (!mYUVFrameQueue.empty()) {
		YUVFrame *frame = *mYUVFrameQueue.begin();
		free(frame->data);
		free(frame);
		mYUVFrameQueue.erase(mYUVFrameQueue.begin());
	}
	mYUVFrameQueue.clear();
}

void CTsOmxPlayer::renderFrame() {
	if (ffextractor_inited == false) {
		return;
	}
	AutoMutex l(mYUVFrameQueueLock);
	if (!mYUVFrameQueue.empty()) {
		YUVFrame *frame = *mYUVFrameQueue.begin();
		if (mSoftRenderer == NULL || mSoftRenderWidth != frame->width || mSoftRenderHeight != frame->height) {
			delete mSoftRenderer;
			mSoftRenderer = NULL;
			sp<MetaData> meta = new MetaData;
			ALOGD("before mSoftRenderer, mSoftNativeWidth=%d, mSoftNativeHeight=%d", frame->width, frame->height);
			meta->setInt32(kKeyWidth, frame->width);
			meta->setInt32(kKeyHeight, frame->height);
			meta->setInt32(kKeyColorFormat, OMX_COLOR_FormatYUV420Planar);

			mFormatMsg->setInt32("stride",       frame->width);
			mFormatMsg->setInt32("slice-height", frame->height);
			mFormatMsg->setInt32("color-format", OMX_COLOR_FormatYUV420Planar);
#ifdef ANDROID4
			mSoftRenderer = new SoftwareRenderer(mSoftSurface, meta);
#endif

#ifdef ANDROID5
			mSoftRenderer = new SoftwareRenderer(mSoftSurface);
#endif

			mSoftRenderWidth = frame->width;
			mSoftRenderHeight = frame->height;
		}

		mFPSProbeSize++;
		if (mFPSProbeSize == kPTSProbeSize) {
			mFPSProbeSize = 0;
			if (mInputQueueSize < 25) {
				mPTSDrift += 1;
				if (mPTSDrift >= 15)
					mPTSDrift = 15;
			} else if (mInputQueueSize > 50) {
				mPTSDrift -= 1;
				if (mFrameDuration + mPTSDrift <= 0)
					mPTSDrift = -mFrameDuration;
			}
		}

		char levels_value[92];
		int soft_sync_mode = 1;
		if(property_get("iptv.soft.sync_mode",levels_value,NULL)>0)
			sscanf(levels_value, "%d", &soft_sync_mode);

		if (soft_sync_mode == 0) {//sync by pts
			if (mFirstDisplayTime == -1 || (abs(frame->pts - mLastRenderPTS) >= 5000000)) {
				mFirstDisplayTime = getCurrentTimeUs();
				mFirst_Pts = frame->pts;
			}

			if ((getCurrentTimeUs() - mFirstDisplayTime) >= (frame->pts - mFirst_Pts)) {
				mLastRenderTime = getCurrentTimeUs();
#ifdef ANDROID4
				mSoftRenderer->render(frame->data, frame->size, NULL);
#endif

#ifdef ANDROID5
				mSoftRenderer->render(frame->data, frame->size, 0,  NULL, mFormatMsg);
#endif
				free(frame->data);
				free(frame);
				mLastRenderPTS = frame->pts;
				mYUVFrameQueue.erase(mYUVFrameQueue.begin());
			}
		} else { //sync by duration
			uint64_t start = getCurrentTimeUs();
#ifdef ANDROID4
			mSoftRenderer->render(frame->data, frame->size, NULL);
#endif

#ifdef ANDROID5
			mSoftRenderer->render(frame->data, frame->size, 0,  NULL, mFormatMsg);
#endif
			free(frame->data);
			free(frame);
			while((getCurrentTimeUs() - start) / 1000 <= mFrameDuration + mPTSDrift)
			{
				usleep(2 * 1000);
			}
			mYUVFrameQueue.erase(mYUVFrameQueue.begin());
		}
	}
}

CTsOmxPlayer::RunWorker::RunWorker(CTsOmxPlayer* player, dt_module_t module) :
		Thread(false), mPlayer(player),mTaskStatus(STATUE_STOPPED),mTaskModule(module) {
}

int32_t CTsOmxPlayer::RunWorker::start () {
	if(mTaskStatus == STATUE_STOPPED)
	{
		Thread::run("RunWorker");
		mTaskStatus = STATUE_RUNNING;
	} 
	else if(mTaskStatus == STATUE_PAUSE) 
	{
		mTaskStatus = STATUE_RUNNING;
	}
	return 0;
}

int32_t CTsOmxPlayer::RunWorker::stop () {
	LOG_LINE();
	requestExitAndWait();
	mTaskStatus = STATUE_STOPPED;
	return 0;
}

bool CTsOmxPlayer::RunWorker::threadLoop() {
    switch (mTaskModule) {
        case MODULE_EXTRACT:
        {
			mPlayer->readExtractor();//  read av packet
			usleep(5*1000);
            break;
        }
        case MODULE_DECODER:
        {
            mPlayer->readDecoder();//get decoded frame
			usleep(5*1000);
            break;
        }
		case MODULE_VSYNC:
		{
			mPlayer->renderFrame();
			usleep(1000);
			break;
		}
        default:
            ALOGE("RunWorker::threadLoop__error");
    }
    return true;
}
