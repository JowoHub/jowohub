#include <jni.h>

#ifndef _Included_NativeManager
#define _Included_NativeManager



extern "C" {
JNIEXPORT void JNICALL
Java_c_plus_plan_common_NativeManager_initNativeLib(JNIEnv *env, jobject thiz,
                                                              jobject context, jboolean isDebug);

JNIEXPORT jint JNICALL
Java_c_plus_plan_common_NativeManager_checkAuth(JNIEnv *env, jobject thiz);

JNIEXPORT jstring JNICALL
Java_c_plus_plan_common_NativeManager_getEncryptByKey(JNIEnv *env, jobject thiz,
                                                                jstring key);

JNIEXPORT jstring JNICALL
Java_c_plus_plan_common_NativeManager_getUrlByKey(JNIEnv *env, jobject thiz, jstring key);
}
#endif