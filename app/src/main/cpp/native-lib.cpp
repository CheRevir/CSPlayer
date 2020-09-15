#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_myapplication_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_cere_csplayer_activity_MainActivity_string(JNIEnv *env, jobject thiz, jint i) {
    std::string s = "Hello from C++";
    return env->NewStringUTF(s.c_str());
}