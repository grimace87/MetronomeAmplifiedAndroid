#pragma once

#include "song.h"
#include "filebuffers.h"
#include <memory>
#include <cstdint>
#include <android/asset_manager.h>

class SongPlayer {
    int32_t mSectionNumber;
    int32_t mSectionRepetition;
    double mSectionProgress;
    FileBuffers mFileBuffers;
    std::unique_ptr<Song> mSong;
    int64_t mSectionReaderHead;

    static void fillWithAudio(int16_t* buffer, std::vector<int16_t>& source, size_t sourceOffset, size_t samples);
    static void fillWithSilence(int16_t* buffer, size_t samples);

public:
    SongPlayer();
    void initialise(AAssetManager* assetManager);
    void release();
    void setSong(Song* song);
    void fillFrames(int16_t* buffer, size_t length);
};
