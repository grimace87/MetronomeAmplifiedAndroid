
#include "audiostreamer.h"
#include <android/asset_manager_jni.h>

static AudioStreamer audioStreamer;

extern "C" {

void JNIEXPORT JNICALL
Java_com_grimace_metronomeamplified_MainActivity_nativeInitialiseAudio(JNIEnv* env, jobject thiz, jobject assetManager) {
    AAssetManager* nativeAssetManager = AAssetManager_fromJava(env, assetManager);
    audioStreamer.initialise(nativeAssetManager);
}

void JNIEXPORT JNICALL
Java_com_grimace_metronomeamplified_MainActivity_nativeReleaseAudio(JNIEnv* env, jobject thiz) {
    audioStreamer.release();
}

void JNIEXPORT JNICALL
Java_com_grimace_metronomeamplified_MainActivity_nativeStartAudio(JNIEnv* env, jobject thiz) {
    audioStreamer.start();
}

void JNIEXPORT JNICALL
Java_com_grimace_metronomeamplified_MainActivity_nativeStopAudio(JNIEnv *env, jobject thiz) {
    audioStreamer.stop();
}

}
