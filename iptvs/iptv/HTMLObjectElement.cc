#include "HTMLObjectElement.h"
#include "jni/HTMLObjectElement_jni.h"
#include "base/android/jni_string.h"
#include "base/android/scoped_java_ref.h"
#include "base/json/json_reader.h"
//==
//#include "base/memory/ptr_util.h"
//#include "base/values.h"
//==
#include "android/log.h"
#undef LOGD
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "chromium", __VA_ARGS__)

using base::android::ScopedJavaLocalRef;
using base::android::ConvertUTF8ToJavaString;
using base::android::ConvertJavaStringToUTF8;

namespace content {
namespace iptv {

void HTMLObjectElement::bagJson(std::string &json)
{
    JNIEnv* env = base::android::AttachCurrentThread();
    ScopedJavaLocalRef<jstring> j_json =
        ConvertUTF8ToJavaString(env, json);
    LOGD("getJson");
    LOGD(json.c_str());
    Java_HTMLObjectElement_bagJson(env, j_json);
}

}
}

