#include "filebuffers.h"

#include "wavereader.h"

static const char* FILE_NAMES[] = {
        "hat.wav",
        "kick.wav",
        "snare.wav",
        "tom.wav",
        "wood_gentle.wav",
        "wood_high.wav",
        "wood_low.wav",
        "wood_mid.wav"
};

constexpr int NO_OF_FILES = 8;

void FileBuffers::createBuffers(AAssetManager* assetManager) {

    if (!mBuffers.empty()) {
        return;
    }

    mBuffers.resize(NO_OF_FILES);
    for (int fileNo = 0; fileNo < NO_OF_FILES; fileNo++) {
        mBuffers[fileNo] = readWaveFile(assetManager, FILE_NAMES[fileNo]);
    }
}

void FileBuffers::releaseBuffers() {
    mBuffers.clear();
}

std::vector<int16_t>& FileBuffers::getBuffer(int index) {
    return mBuffers[index];
}
