#pragma once

#include <oboe/Oboe.h>

class AudioStreamer : public oboe::AudioStreamCallback {

    bool mIsPlaying;
    oboe::AudioStream* mStream;

    oboe::DataCallbackResult
    onAudioReady(oboe::AudioStream* oboeStream, void* audioData, int32_t numFrames) override;

    void setStream(oboe::AudioStream* stream);
    void fillFrames(int16_t* buffer, size_t length);

public:
    explicit AudioStreamer() noexcept;
    ~AudioStreamer();
    void start();
    void stop();

};
