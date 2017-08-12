// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// This file is autogenerated by
//     base/android/jni_generator/jni_generator.py
// For
//     com/yinhe/android/iptv/DeviceCallback

#ifndef com_yinhe_android_iptv_DeviceCallback_JNI
#define com_yinhe_android_iptv_DeviceCallback_JNI

#include <jni.h>

#include "../../../../../../../../../base/android/jni_generator/jni_generator_helper.h"

#include "base/android/jni_int_wrapper.h"

// Step 1: forward declarations.
namespace {
const char kDeviceCallbackClassPath[] = "com/yinhe/android/iptv/DeviceCallback";
// Leaking this jclass as we cannot use LazyInstance from some threads.
base::subtle::AtomicWord g_DeviceCallback_clazz __attribute__((unused)) = 0;
#define DeviceCallback_clazz(env) base::android::LazyGetClass(env, kDeviceCallbackClassPath, &g_DeviceCallback_clazz)

}  // namespace

namespace content {
namespace iptv {

// Step 2: method stubs.

static base::subtle::AtomicWord g_DeviceCallback_SendVK = 0;
static jboolean Java_DeviceCallback_SendVK(JNIEnv* env) {
  CHECK_CLAZZ(env, DeviceCallback_clazz(env),
      DeviceCallback_clazz(env), false);
  jmethodID method_id =
      base::android::MethodID::LazyGet<
      base::android::MethodID::TYPE_STATIC>(
      env, DeviceCallback_clazz(env),
      "SendVK",

"("
")"
"Z",
      &g_DeviceCallback_SendVK);

  jboolean ret =
      env->CallStaticBooleanMethod(DeviceCallback_clazz(env),
          method_id);
  jni_generator::CheckException(env);
  return ret;
}

// Step 3: RegisterNatives.

}  // namespace iptv
}  // namespace content

#endif  // com_yinhe_android_iptv_DeviceCallback_JNI
