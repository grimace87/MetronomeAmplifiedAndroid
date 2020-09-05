#include "section.h"

#include "byteswap.h"

Section::Section() : mName(), mTempo(108.0), mBeatsPerMeasure(4), mBeatValue(NoteType::QUARTER), mNotes{} {}

Section::Section(const jbyte* byteBuffer, size_t* bytesRead) {

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

    // Read primitive fields
    mTempo = reverseBytesF32(*(float*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(float);
    mBeatsPerMeasure = reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(int32_t);
    mBeatValue = noteTypeFromNotesPerWhole(
            reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead)));
    totalBytesRead += sizeof(int32_t);

    // Read note count then notes themselves
    int32_t noteCount = reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(int32_t);
    mNotes.resize(noteCount);
    for (int note = 0; note < noteCount; note++) {
        size_t noteSizeBytes;
        mNotes[note] = Note(byteBuffer + totalBytesRead, &noteSizeBytes);
        totalBytesRead += noteSizeBytes;
    }

    // Output bytes read
    *bytesRead = totalBytesRead;
}
