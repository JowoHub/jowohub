#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/inotify.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/system_properties.h>


#include "log.h"
#include "Md5.h"
#include "NativeUtils.h"


/**
 * 包相关信息工具类
 *
 */


int CheckAuth(APK_INFO apkInfo) {
    // TODO check your keystore signature
    return 1;
}

char *GetPackageInfo(JNIEnv *env, jobject thiz, int *len, APK_INFO *apkInfo) {
    jclass cls_Context = env->GetObjectClass(thiz);
    jmethodID getPackageManager = env->GetMethodID(cls_Context,
                                                   "getPackageManager",
                                                   "()Landroid/content/pm/PackageManager;");
    jobject pm = env->CallObjectMethod(thiz, getPackageManager);

    jmethodID getPackageName = env->GetMethodID(cls_Context,
                                                "getPackageName", "()Ljava/lang/String;");
    jstring jpackagename = (jstring) env->CallObjectMethod(thiz,
                                                           getPackageName);
    apkInfo->packageName = env->GetStringUTFChars(jpackagename, 0);
    jclass cls_PackageManager = env->GetObjectClass(pm);
    jint GET_SIGNATURES = 64;
    jmethodID getPackageInfo = env->GetMethodID(cls_PackageManager,
                                                "getPackageInfo",
                                                "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jobject info = env->CallObjectMethod(pm, getPackageInfo,
                                         jpackagename, GET_SIGNATURES);

    jclass cls_PackageInfo = env->GetObjectClass(info);
    jfieldID signatures_id = env->GetFieldID(cls_PackageInfo,
                                             "signatures", "[Landroid/content/pm/Signature;");
    jobjectArray signatures = (jobjectArray) env->GetObjectField(info,
                                                                 signatures_id);
    jfieldID versionCode_id = env->GetFieldID(cls_PackageInfo,
                                              "versionName", "Ljava/lang/String;");
    jstring versionCode = (jstring) env->GetObjectField(info,
                                                        versionCode_id);
    apkInfo->versionCode = env->GetStringUTFChars(versionCode, 0);
    jobject signature_0 = env->GetObjectArrayElement(signatures, 0);

    jclass cls_Signature = env->GetObjectClass(signature_0);
    jmethodID toByteArray = env->GetMethodID(cls_Signature,
                                             "toByteArray", "()[B");
    jbyteArray signature_0_bytearray = (jbyteArray) env->CallObjectMethod(
            signature_0, toByteArray);
    jsize sign_len = env->GetArrayLength(signature_0_bytearray);
    *len = sign_len;
    char *sign = (char *) malloc(sign_len);
    env->GetByteArrayRegion(signature_0_bytearray, 0, sign_len,
                            (jbyte *) sign);
    memset(apkInfo->signer, 0, sizeof(apkInfo->signer));
    md5hexa(sign, sign_len, apkInfo->signer);
    free(sign);
    return sign;
}

/**
 *  get the android version code
 */
int get_version() {
    char value[8] = "";
    __system_property_get("ro.build.version.sdk", value);
    return atoi(value);
}

/**
 *  stitch three string to one
 */
char *str_stitching(const char *str1, const char *str2, const char *str3) {
    char *result;
    result = (char *) malloc(strlen(str1) + strlen(str2) + strlen(str3) + 1);
    if (!result) {
        return NULL;
    }
    strcpy(result, str1);
    strcat(result, str2);
    strcat(result, str3);
    return result;
}

/**
 * get android context
 */
jobject get_context(JNIEnv *env, jobject jobj) {
    jclass thiz_cls = env->GetObjectClass(jobj);
    jfieldID context_field = env->GetFieldID(thiz_cls, "mContext", "Landroid/content/Context;");
    return env->GetObjectField(jobj, context_field);
}


char *get_package_name(JNIEnv *env, jobject jobj) {
    jobject context_obj = get_context(env, jobj);
    jclass context_cls = env->GetObjectClass(context_obj);
    jmethodID getpackagename_method = env->GetMethodID(context_cls, "getPackageName",
                                                       "()Ljava/lang/String;");
    jstring package_name = (jstring) env->CallObjectMethod(context_obj, getpackagename_method);
    return (char *) env->GetStringUTFChars(package_name, 0);
}


/**
 * call java callback
 */
void java_callback(JNIEnv *env, jobject jobj, char *method_name) {
    jclass cls = env->GetObjectClass(jobj);
    jmethodID cb_method = env->GetMethodID(cls, method_name, "()V");
    env->CallVoidMethod(jobj, cb_method);
}

/**
 * start a android service
 */
void start_service(char *package_name, char *service_name) {
    pid_t pid = fork();
    if (pid < 0) {
        //error, do nothing...
    } else if (pid == 0) {
        if (package_name == NULL || service_name == NULL) {
            exit(EXIT_SUCCESS);
        }
        int version = get_version();
        char *pkg_svc_name = str_stitching(package_name, "/", service_name);
        if (version >= 17 || version == 0) {
            execlp("am", "am", "startservice", "--user", "0", "-n", pkg_svc_name, (char *) NULL);
        } else {
            execlp("am", "am", "startservice", "-n", pkg_svc_name, (char *) NULL);
        }
        exit(EXIT_SUCCESS);
    } else {
        waitpid(pid, NULL, 0);
    }
}
