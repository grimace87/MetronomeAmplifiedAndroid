#include "songplayer.h"

SongPlayer::SongPlayer() :
    mSectionNumber(0),
    mSectionRepetition(0),
    mSectionProgress(0.0),
    mFileBuffers(),
    mSong(nullptr),
    mBufferHead(0) {
}

void SongPlayer::initialise(AAssetManager* assetManager) {
    mFileBuffers.createBuffers(assetManager);
}

void SongPlayer::release() {
    mFileBuffers.releaseBuffers();
}

void SongPlayer::setSong(Song* song) {
    mSong.reset(song);
}

void SongPlayer::fillFrames(int16_t* buffer, size_t length) {
    std::vector<int16_t>& fileBuffer = mFileBuffers.getBuffer(0);
    for (size_t n = 0; n < length; n++) {
        if (mBufferHead >= fileBuffer.size()) {
            mBufferHead = 0;
        }
        buffer[n] = fileBuffer[mBufferHead];
        mBufferHead++;
    }
}
