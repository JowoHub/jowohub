#include <stdlib.h>
#include <stdio.h>
#include <jni.h>

#ifndef _Included_NativeUtils
#define _Included_NativeUtils

typedef struct _APK_INFO {
    /**
     * apk包名
     */
    const char *packageName;
    /**
     * apk versionCode
     */
    const char *versionCode;
    /**
     * apk签名
     */
    char signer[33];
} APK_INFO;

typedef struct _GESTURE_DATA {
    char loc_userid[33];
    char loc_password[33];
} GESTURE_DATA;

char *GetPackageInfo(JNIEnv *env, jobject thiz, int *len, APK_INFO *apkInfo);

int CheckAuth(APK_INFO apkInfo);

int get_version();

char *str_stitching(const char *str1, const char *str2, const char *str3);

jobject get_context(JNIEnv* env, jobject jobj);

char* get_package_name(JNIEnv* env, jobject jobj);

void java_callback(JNIEnv* env, jobject jobj, char* method_name);

void start_service(char* package_name, char* service_name);

#endif
