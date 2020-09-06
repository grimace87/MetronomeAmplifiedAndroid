#pragma once

#include "note.h"
#include <vector>
#include <string>

class Section {
    std::string mName;
    int32_t mRepetitions;
    float mTempo;
    int32_t mBeatsPerMeasure;
    NoteType mBeatValue;
    std::vector<Note> mNotes;

public:
    Section();
    Section(const jbyte* byteBuffer, size_t* bytesRead);
};
