#pragma once

#include "enums.h"
#include <jni.h>

class Note {
    NoteType mNoteType;
    int32_t mAccent;

public:
    Note();
    Note(const jbyte* byteBuffer, size_t* bytesRead);
};
