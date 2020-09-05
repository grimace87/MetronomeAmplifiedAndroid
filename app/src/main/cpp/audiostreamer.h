#pragma once

#include "filebuffers.h"
#include <oboe/Oboe.h>
#include <android/asset_manager.h>

class AudioStreamer : public oboe::AudioStreamCallback {

    bool mIsPlaying;
    oboe::AudioStream* mStream;
    FileBuffers mFileBuffers;

    size_t mBufferHead;

    oboe::DataCallbackResult
    onAudioReady(oboe::AudioStream* oboeStream, void* audioData, int32_t numFrames) override;

    void setStream(oboe::AudioStream* stream);
    void fillFrames(int16_t* buffer, size_t length);

public:
    explicit AudioStreamer() noexcept;
    ~AudioStreamer();
    void initialise(AAssetManager* assetManager);
    void release();
    void start();
    void stop();

};
