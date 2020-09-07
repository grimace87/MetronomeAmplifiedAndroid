#pragma once

#include "song.h"
#include "filebuffers.h"
#include <memory>
#include <cstdint>
#include <android/asset_manager.h>

class SongPlayer {
    int mSectionNumber;
    int mSectionRepetition;
    double mSectionProgress;
    FileBuffers mFileBuffers;
    std::unique_ptr<Song> mSong;
    size_t mBufferHead;

public:
    SongPlayer();
    void initialise(AAssetManager* assetManager);
    void release();
    void setSong(Song* song);
    void fillFrames(int16_t* buffer, size_t length);
};
