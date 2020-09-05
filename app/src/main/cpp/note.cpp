#include "note.h"

#include "byteswap.h"

Note::Note() : mNoteType(NoteType::QUARTER), mAccent(1) {}

Note::Note(const jbyte* byteBuffer, size_t* bytesRead) {

    size_t totalBytesRead = 0;

    // Read primitive fields
    mNoteType = noteTypeFromNotesPerWhole(
            reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead)));
    totalBytesRead += sizeof(int32_t);
    mAccent = reverseBytesI32(*(int32_t*)(byteBuffer + totalBytesRead));
    totalBytesRead += sizeof(int32_t);

    // Output bytes read
    *bytesRead = totalBytesRead;
}
