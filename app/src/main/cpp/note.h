#pragma once

#include "enums.h"
#include <jni.h>

class Note {
    NoteType mNoteType;
    int32_t mAccent;
    bool mIsSound;
    bool mIsDotted;
    Tuplet mTuplet;
    int32_t mTieString;
    double mNoteValue;

public:
    Note();
    Note(const jbyte* byteBuffer, size_t* bytesRead);
    inline double getUnitValue() { return mNoteValue; }
    inline int32_t getAccentNumber() { return mAccent; }
};
