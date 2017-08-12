#include "STBAppManagerCTC.h"
#include "jni/STBAppManagerCTC_jni.h"
#include "base/android/jni_string.h"
#include "base/android/scoped_java_ref.h"

using base::android::ScopedJavaLocalRef;
using base::android::ConvertUTF8ToJavaString;
using base::android::ConvertJavaStringToUTF8;

namespace content {
namespace iptv {

bool STBAppManagerCTC::isAppInstalled(const std::string &appName)
{
    JNIEnv* env = base::android::AttachCurrentThread();
    ScopedJavaLocalRef<jstring> j_appName =
        ConvertUTF8ToJavaString(env, appName);
    return Java_STBAppManagerCTC_isAppInstalled(env, j_appName);
}

std::string STBAppManagerCTC::getAppVersion(const std::string &appName)
{
    JNIEnv* env = base::android::AttachCurrentThread();
    ScopedJavaLocalRef<jstring> j_appName =
        ConvertUTF8ToJavaString(env, appName);
    ScopedJavaLocalRef<jstring> j_version =
        Java_STBAppManagerCTC_getAppVersion(env, j_appName);
    return ConvertJavaStringToUTF8(env, j_version);
}

void STBAppManagerCTC::startAppByName(const std::string &appName)
{
    JNIEnv* env = base::android::AttachCurrentThread();
    ScopedJavaLocalRef<jstring> j_appName =
        ConvertUTF8ToJavaString(env, appName);
    Java_STBAppManagerCTC_startAppByName(env, j_appName);
}

void STBAppManagerCTC::restartAppByName(const std::string &appName)
{
    JNIEnv* env = base::android::AttachCurrentThread();
    ScopedJavaLocalRef<jstring> j_appName =
        ConvertUTF8ToJavaString(env, appName);
    Java_STBAppManagerCTC_restartAppByName(env, j_appName);
}

void STBAppManagerCTC::startAppByIntent(const std::string &intentMessage)
{
    JNIEnv* env = base::android::AttachCurrentThread();
    ScopedJavaLocalRef<jstring> j_intentMessage =
        ConvertUTF8ToJavaString(env, intentMessage);
    Java_STBAppManagerCTC_startAppByIntent(env, j_intentMessage);
}

void STBAppManagerCTC::installApp(const std::string &apkUrl)
{
    JNIEnv* env = base::android::AttachCurrentThread();
    ScopedJavaLocalRef<jstring> j_apkUrl =
        ConvertUTF8ToJavaString(env, apkUrl);
    Java_STBAppManagerCTC_installApp(env, j_apkUrl);
}

}
}
