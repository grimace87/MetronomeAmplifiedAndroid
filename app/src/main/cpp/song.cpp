#include "song.h"

#include "byteswap.h"

Song::Song() : mName(), mSections{} {}

Song::Song(const jbyte* byteBuffer, size_t* bytesRead) {

    size_t totalBytesRead = 0;

    // Read size info for name (16-bit integer, value is size of bytes including the size itself)
    uint16_t nameFieldSize = reverseBytesI16(*(const uint16_t*)byteBuffer);
    auto nameSize = (size_t)nameFieldSize;
    totalBytesRead += sizeof(uint16_t);

    // Read name string, and skip padding until 4-byte alignment
    mName.resize(nameSize);
    memcpy((void*)mName.data(), (void*)(byteBuffer + totalBytesRead), nameSize);
    totalBytesRead += nameSize;
    size_t padding = (4 - totalBytesRead % 4) % 4;
    totalBytesRead += padding;

    // Read sections
    int32_t noOfSections = reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(int32_t);
    mSections.resize(noOfSections);
    for (int section = 0; section < noOfSections; section++) {
        size_t sectionBytes;
        mSections[section] = Section(byteBuffer + totalBytesRead, &sectionBytes);
        totalBytesRead += sectionBytes;
    }

    // Output bytes read
    *bytesRead = totalBytesRead;
}

Section& Song::getSection(int index) {
    return mSections[index];
}
