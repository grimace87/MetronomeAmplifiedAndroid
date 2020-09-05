#pragma once

#include <android/asset_manager.h>
#include <vector>

class FileBuffers {
    std::vector<std::vector<int16_t>> mBuffers;

public:
    void createBuffers(AAssetManager* assetManager);
    void releaseBuffers();
    std::vector<int16_t>& getBuffer(int index);
};
