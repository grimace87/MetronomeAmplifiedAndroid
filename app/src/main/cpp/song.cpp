#include "song.h"

#include "byteswap.h"

Song::Song() : mName(), mSection() {}

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

    // Read section
    size_t sectionBytes;
    mSection = Section(byteBuffer + totalBytesRead, &sectionBytes);
    totalBytesRead += sectionBytes;

    // Output bytes read
    *bytesRead = totalBytesRead;
}
