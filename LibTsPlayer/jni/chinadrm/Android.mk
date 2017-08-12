LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE :=libtelecom_iptv
LOCAL_MODULE_PATH := $(TARGET_OUT)/lib
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
LOCAL_SRC_FILES :=CA/libtelecom_iptv.so
LOCAL_PROPRIETARY_MODULE := true
LOCAL_STRIP_MODULE := false

LOCAL_MODULE_TAGS := optional

include $(BUILD_PREBUILT)


ifeq ($(TARGET_USE_OPTEEOS),true)
include $(CLEAR_VARS)
TA_BINARY=9930f660-20ae-11e6-b68a0002a5d5c51b
TA_EXPORT_DIR :=$(TARGET_OUT)/lib/teetz
LOCAL_MODULE := $(TA_BINARY)
LOCAL_SRC_FILES :=TA/9930f660-20ae-11e6-b68a0002a5d5c51b.ta
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .ta
LOCAL_MODULE_PATH := $(TA_EXPORT_DIR)
LOCAL_STRIP_MODULE := false
include $(BUILD_PREBUILT)
endif