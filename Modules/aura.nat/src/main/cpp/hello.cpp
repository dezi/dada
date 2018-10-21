#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_aura_aosp_aura_nat_levenshtein_Levenshtein_helloFromJNI
        (JNIEnv *env, jclass self)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
