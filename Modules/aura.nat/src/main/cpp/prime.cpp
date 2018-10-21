#include <jni.h>
#include <string>
#include <cmath>

extern "C" JNIEXPORT jboolean JNICALL
Java_com_aura_aosp_aura_nat_prime_Prime_isPrimeCpp
        (JNIEnv *env, jclass self, jlong a)
{
    if (a == 2)
    {
        return 1;
    }

    if (a <= 1 || a % 2 == 0)
    {
        return 0;
    }

    long max = (long) sqrt(a);

    for(long n = 3; n <= max; n += 2)
    {
        if(a % n == 0)
        {
            return 0;
        }
    }

    return 1;
}