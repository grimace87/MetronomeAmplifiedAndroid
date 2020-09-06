#include "note.h"

#include "byteswap.h"

Note::Note() :
    mNoteType(NoteType::QUARTER),
    mAccent(1),
    mIsSound(true),
    mIsDotted(false),
    mTuplet(Tuplet::NONE),
    mTieString(0),
    mNoteValue(1.0) {}

Note::Note(const jbyte* byteBuffer, size_t* bytesRead) {

    size_t totalBytesRead = 0;

    // Read primitive fields
    mNoteType = noteTypeFromNotesPerWhole(
            reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead)));
    totalBytesRead += sizeof(int32_t);
    mAccent = reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(int32_t);
    mIsSound = (bool)reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(int32_t);
    mIsDotted = (bool)reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(int32_t);
    mTuplet = tupletFromCommonId(
            reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead)));
    totalBytesRead += sizeof(int32_t);
    mTieString = reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(int32_t);
    mNoteValue = reverseBytesF64(*(double*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(double);

    // Output bytes read
    *bytesRead = totalBytesRead;
}
