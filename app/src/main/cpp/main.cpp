
#include "audiostreamer.h"
#include <jni.h>

static AudioStreamer audioStreamer;

extern "C" {

void JNIEXPORT JNICALL
Java_com_grimace_metronomeamplified_MainActivity_nativeStartAudio(JNIEnv *env, jobject thiz) {
    audioStreamer.start();
}

void JNIEXPORT JNICALL
Java_com_grimace_metronomeamplified_MainActivity_nativeStopAudio(JNIEnv *env, jobject thiz) {
    audioStreamer.stop();
}

}
