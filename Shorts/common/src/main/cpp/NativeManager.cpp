#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "NativeManager.h"
#include "NativeUtils.h"
#include "EncryptManager.h"
#include "UrlManager.h"

APK_INFO apkInfo;

/**
 * 跟Java交互管理类
 *
 */
extern "C" {

jboolean mIsDebug;    //debug 模式下不需要签名校验

JNIEXPORT void JNICALL
Java_c_plus_plan_common_NativeManager_initNativeLib(JNIEnv *env, jobject thiz,
                                                              jobject context, jboolean isDebug) {
    int len = -1;
    mIsDebug = isDebug;
    GetPackageInfo(env, context, &len, &apkInfo);
}

JNIEXPORT jint JNICALL
Java_c_plus_plan_common_NativeManager_checkAuth(JNIEnv *env, jobject thiz) {
    if (CheckAuth(apkInfo) == 1 || mIsDebug) {
        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}


JNIEXPORT jstring JNICALL
Java_c_plus_plan_common_NativeManager_getEncryptByKey(JNIEnv *env, jobject thiz,
                                                                jstring key) {
    if (CheckAuth(apkInfo) == 1 || mIsDebug) {
        // Check path userid
        const char *_key = env->GetStringUTFChars(key, 0);
        if (_key != NULL) {
            char *outRsa = getEncryptByKey(_key);
            if (outRsa != NULL) {
                return env->NewStringUTF(outRsa);
            } else {
                return env->NewStringUTF("find fail");
            }
        } else {
            return env->NewStringUTF("find fail");
        }

    } else {
        return env->NewStringUTF("app fail");
    }
}

JNIEXPORT jstring JNICALL
Java_c_plus_plan_common_NativeManager_getUrlByKey(JNIEnv *env, jobject thiz,
                                                            jstring key) {
    if (CheckAuth(apkInfo) == 1 || mIsDebug) {
        // Check path userid
        const char *_key = env->GetStringUTFChars(key, 0);
        if (_key != NULL) {
            char *outRsa = getUrlByKey(_key);
            if (outRsa != NULL) {
                return env->NewStringUTF(outRsa);
            } else {
                return env->NewStringUTF("find fail");
            }
        } else {
            return env->NewStringUTF("find fail");
        }

    } else {
        return env->NewStringUTF("app fail");
    }
}
}