LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

ifeq ($(BUILD_SH_TELECOM_APKS),true)
LOCAL_CFLAGS += -DSH_TELCOM_SUPPORT
endif

LOCAL_CFLAGS += -D__STDC_CONSTANT_MACROS

LOCAL_ARM_MODE := arm
LOCAL_MODULE    := libFFExtractor
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := \
	amffplayer.cpp \


LIBPLAYER_PATH := $(TOP)/packages/amlogic/LibPlayer
LOCAL_C_INCLUDES := \
	$(TOP)/external/ffmpeg \
	$(LOCAL_PATH)/../include

LOCAL_SHARED_LIBRARIES += libamffmpeg liblog libcutils libutils

include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_EXECUTABLE)
