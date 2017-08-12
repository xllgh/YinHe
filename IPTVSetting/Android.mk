LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES := bouncycastle
LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += src/com/yinhe/securityguard/SecurityCat.aidl

LOCAL_PACKAGE_NAME := YinheSetting
LOCAL_CERTIFICATE := platform
LOCAL_DEX_PREOPT := false
LOCAL_AAPT_FLAGS += -c ldpi -c mdpi -c hdpi 

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_STATIC_JAVA_LIBRARIES += DisplaySetting
LOCAL_STATIC_JAVA_LIBRARIES += HiAoService

ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)

include $(BUILD_PACKAGE)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := android-support-v4:libs/android-support-v4.jar


# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
