#include <jni.h>
#include <string>
#include <android/log.h>

#define MAXCHARACTERS 48

static void copyToRunes(jbyte* bytes, int* runes, int size)
{
    for (int inx = 0; inx < size; inx++)
    {
        runes[ inx ] = bytes[ inx ];
    }
}
extern "C" JNIEXPORT jint JNICALL
Java_com_aura_aosp_aura_nat_levenshtein_Levenshtein_levenshtein
        (JNIEnv *env, jclass self, jbyteArray s1, jint s1len, jbyteArray s2, jint s2len)
{
    //
    // Check arguments.
    //

    //__android_log_print(ANDROID_LOG_DEBUG, "levenshtein", "s1len=%d s2len=%d", s1len, s2len);

    if ((s1len > MAXCHARACTERS) || (s2len > MAXCHARACTERS))
    {
        return -1;
    }

    jsize s1siz = env->GetArrayLength(s1);
    jsize s2siz = env->GetArrayLength(s2);

    //__android_log_print(ANDROID_LOG_DEBUG, "levenshtein", "s1siz=%d s2siz=%d", s1siz, s2siz);

    if ((s1siz < s1len) || (s2siz < s2len))
    {
        return -1;
    }

    //
    // Dereference byte arrays.
    //

    jbyte* s1ptr = env->GetByteArrayElements(s1, 0);
    jbyte* s2ptr = env->GetByteArrayElements(s2, 0);

    //
    // Convert byte character sequence into UTF-8 runes.
    //

    int r1[ MAXCHARACTERS ];
    int r2[ MAXCHARACTERS ];

    copyToRunes(s1ptr, r1, s1len);
    copyToRunes(s2ptr, r2, s2len);

    //
    // Start computing the distance.
    //

    int rows = s1len;
    int cols = s2len;
    int rown = rows + 1;
    int coln = cols + 1;

    int d1;
    int d2;
    int d3;
    int m1;
    int i;
    int j;

    int* dist = (int*) malloc(rown * coln * sizeof(int));

    for (i = 0; i < rown; i++)
    {
        dist[i * coln] = i;
    }

    for (j = 0; j < coln; j++)
    {
        dist[j] = j;
    }

    for (j = 0; j < cols; j++)
    {
        for (i = 0; i < rows; i++)
        {
            if (r1[i] == r2[j])
            {
                dist[((i + 1) * coln) + (j + 1)] = dist[(i * coln) + j];
            }
            else
            {
                d1 = dist[(i * coln) + (j + 1)] + 1;
                d2 = dist[((i + 1) * coln) + j] + 1;
                d3 = dist[(i * coln) + j] + 1;

                m1 = (d2 < d3) ? d2 : d3;

                dist[((i + 1) * coln) + (j + 1)] = (d1 < m1) ? d1 : m1;
            }
        }
    }

    int levdist = dist[(coln * rown) - 1];

    free(dist);

    //
    // Release bytes arrays with abort, no copy back.
    //

    env->ReleaseByteArrayElements(s1, s1ptr, JNI_ABORT);
    env->ReleaseByteArrayElements(s2, s2ptr, JNI_ABORT);

    //
    // Job done.
    //

    //__android_log_print(ANDROID_LOG_DEBUG, "levenshtein", "levdist=%d", levdist);

    return levdist;
}
