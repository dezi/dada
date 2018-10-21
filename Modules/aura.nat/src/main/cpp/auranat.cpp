#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_aura_aosp_aura_nat_levenshtein_Levenshtein_stringFromJNI
        (JNIEnv *env, jclass self)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_com_aura_aosp_aura_nat_levenshtein_Levenshtein_levenshtein
        (JNIEnv *env, jclass self, jbyteArray s1, jbyteArray s2)
{
    return 0;
}


