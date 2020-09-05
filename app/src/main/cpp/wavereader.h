#pragma once

#include <android/asset_manager.h>
#include <vector>

std::vector<int16_t> readWaveFile(AAssetManager* assetManager, const char* assetFileName);
