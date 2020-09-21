#include <jni.h>
#include <string>

// std::string hello = "Hello from C++";
// extern "C" JNIEXPORT jstring JNICALL
// Java_com.gamatechno.chato.sdk_app_main_MainActivity_stringFromJNI(
//     JNIEnv* env,
//   jobject /* this */) {
// return env->NewStringUTF(hello.c_str());
// }

std::string stringAPPKEY = "cSNOBVMHFgU8exSIxS";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringAPPKEY(
        JNIEnv *env,
        jclass type) {
        return env->NewStringUTF(stringAPPKEY.c_str());
}

std::string stringLogin = "login";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringLogin(
        JNIEnv *env,
        jclass type) {
        return env->NewStringUTF(stringLogin.c_str());
}

std::string stringRegister = "register";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringRegister(
        JNIEnv *env,
        jclass type) {
        return env->NewStringUTF(stringRegister.c_str());
}

std::string stringForgotPassword = "forgot_password";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringForgotPassword(
        JNIEnv *env,
        jclass type) {
        return env->NewStringUTF(stringForgotPassword.c_str());
}

std::string stringLocation = "location";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringLocation(
        JNIEnv *env,
        jclass type) {
        return env->NewStringUTF(stringLocation.c_str());
}

std::string stringContacts = "contacts";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringContacts(
        JNIEnv *env,
        jclass type) {
        return env->NewStringUTF(stringContacts.c_str());
}


std::string stringUpdateProfile = "updateProfile";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringUpdateProfile(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringUpdateProfile.c_str());
}

std::string stringUpdatePassword = "updatePassword";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringUpdatePassword(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringUpdatePassword.c_str());
}

std::string stringLogout = "logout";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringLogout(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringLogout.c_str());
}

std::string stringRefresh = "refresh";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringRefresh(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringRefresh.c_str());
}

std::string stringAccessPermission = "set-access-permissions";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringAccessPermission(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringAccessPermission.c_str());
}

std::string stringMessages = "messages";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringMessages(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringMessages.c_str());
}

std::string stringblockMessage = "blockMessage";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringblockMessage(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringblockMessage.c_str());
}

std::string stringFiles = "files";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringFiles(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringFiles.c_str());
}

std::string stringFilesTest = "files-test";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringFilesTest(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringFilesTest.c_str());
}

std::string stringDownloadfiles = "downloadfiles";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringDownloadfiles(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringDownloadfiles.c_str());
}

std::string stringUpdateSMSStatus = "outBoxMessageStatus";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringUpdateSMSStatus(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringUpdateSMSStatus.c_str());
}

std::string stringQueueFileSend = "queuefilesend";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringQueueFileSend(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringQueueFileSend.c_str());
}

std::string stringSingleFileDownload = "singlefiledownload";
extern "C"
JNIEXPORT jstring JNICALL
Java_com_mobile_isecurity_data_Api_stringSingleFileDownload(
        JNIEnv *env,
        jclass type) {
    return env->NewStringUTF(stringSingleFileDownload.c_str());
}