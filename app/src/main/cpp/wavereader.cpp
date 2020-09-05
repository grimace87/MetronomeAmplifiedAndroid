#include "wavereader.h"

struct RiffDescriptor {
    uint32_t ChunkId; // Big endian
    uint32_t ChunkSize; // Little endian
    uint32_t Format; // Big endian
};

struct FmtDescriptor {
    uint32_t Subchunk1Id; // Big endian
    uint32_t Subchunk1Size; // Little endian
    uint16_t AudioFormat; // Little endian
    uint16_t NumChannels; // Little endian
    uint32_t SampleRate; // Little endian
    uint32_t ByteRate; // Little endian
    uint16_t BlockAlign; // Little endian
    uint16_t BitsPerSample; // Little endian
};

struct DataHeader {
    uint32_t Subchunk2Id; // Big endian
    uint32_t Subchunk2Size; // Little endian
};

std::vector<int16_t> readWaveFile(AAssetManager* assetManager, const char* assetFileName) {

    // Open and validate asset
    auto minLength = sizeof(RiffDescriptor) + sizeof(FmtDescriptor) + sizeof(DataHeader);
    AAsset* asset = AAssetManager_open(assetManager, assetFileName, AASSET_MODE_BUFFER);
    off_t assetLength = AAsset_getLength(asset);
    assert(asset);
    assert(assetLength >= minLength);

    // Read headers
    RiffDescriptor riffDescriptor {};
    FmtDescriptor fmtDescriptor {};
    DataHeader dataHeader {};
    AAsset_read(asset, (void*)&riffDescriptor, sizeof(RiffDescriptor));
    AAsset_read(asset, (void*)&fmtDescriptor, sizeof(FmtDescriptor));
    AAsset_read(asset, (void*)&dataHeader, sizeof(DataHeader));

    // Validate headers
    assert(dataHeader.Subchunk2Size > 0);
    assert(assetLength >= minLength + dataHeader.Subchunk2Size);
    assert(fmtDescriptor.NumChannels == 1);
    assert(fmtDescriptor.BitsPerSample == 16);

    // Create buffer and read data in
    std::vector<int16_t> audioData;
    audioData.resize(dataHeader.Subchunk2Size / sizeof(int16_t));
    AAsset_read(asset, (void*)audioData.data(), dataHeader.Subchunk2Size);

    // Close asset and return
    AAsset_close(asset);
    return audioData;
}
