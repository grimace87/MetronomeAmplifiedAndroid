#include "audiostreamer.h"
#include <string>
#include <android/log.h>
#include <android/asset_manager.h>

#define LOG_ERR(fmt, val) __android_log_print(ANDROID_LOG_ERROR, "AudioTest", fmt, val)
#define RET_ERR_RES(fmt) if (result != oboe::Result::OK) { LOG_ERR(fmt, oboe::convertToText(result)); return; }

AudioStreamer::AudioStreamer() noexcept : mIsPlaying(false), mStream(nullptr), mBufferHead(0) {}

AudioStreamer::~AudioStreamer() {
    if (mIsPlaying) {
        stop();
    }
}

void AudioStreamer::initialise(AAssetManager* assetManager) {
    mFileBuffers.createBuffers(assetManager);
}

void AudioStreamer::release() {
    stop();
    mFileBuffers.releaseBuffers();
}

oboe::DataCallbackResult
AudioStreamer::onAudioReady(oboe::AudioStream* oboeStream, void* audioData, int32_t numFrames) {
    if (mIsPlaying) {
        uint32_t frames = numFrames >= 0 ? (uint32_t)numFrames : 0;
        fillFrames((int16_t*)audioData, frames);
        return oboe::DataCallbackResult::Continue;
    }
    return oboe::DataCallbackResult::Stop;
}

void AudioStreamer::setStream(oboe::AudioStream* stream) {
    mStream = stream;
    mIsPlaying = true;
}

void AudioStreamer::start() {
    if (mIsPlaying) {
        return;
    }

    oboe::AudioStreamBuilder builder;
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
    builder.setSharingMode(oboe::SharingMode::Exclusive);
    builder.setCallback(this);
    builder.setFormat(oboe::AudioFormat::I16);
    builder.setChannelCount(2);
    builder.setSampleRate(48000);

    oboe::AudioStream* stream;
    oboe::Result result = builder.openStream(&stream);
    RET_ERR_RES("Error opening stream: %s")

    // Set buffer size, must be a multiple of the burst size (official video says 2 times burst
    // size is a sensible 'rule of thumb').
    // This function will return 'ErrorUnimplemented' if using OpenSL ES.
    if (stream->getAudioApi() == oboe::AudioApi::AAudio) {
        int32_t sensibleBufferSize = 2 * stream->getFramesPerBurst();
        auto bufferSetResult = stream->setBufferSizeInFrames(sensibleBufferSize);
        result = bufferSetResult.error();
        RET_ERR_RES("Error setting buffer size: %s")
    }

    mBufferHead = 0;
    setStream(stream);
    result = stream->requestStart();
    RET_ERR_RES("Error starting stream: %s")
}

void AudioStreamer::stop() {
    if (!mIsPlaying) {
        return;
    }
    mIsPlaying = false;
    if (mStream) {
        mStream->requestStop();
        mStream->close();
        mStream = nullptr;
    }
}

void AudioStreamer::fillFrames(int16_t* buffer, size_t length) {
    std::vector<int16_t>& fileBuffer = mFileBuffers.getBuffer(0);
    for (size_t n = 0; n < length; n++) {
        if (mBufferHead >= fileBuffer.size()) {
            mBufferHead = 0;
        }
        buffer[n] = fileBuffer[mBufferHead];
        mBufferHead++;
    }
}


