#pragma once

#include "note.h"
#include <vector>
#include <string>

struct SectionNotePosition {
    int noteIndex;
    int64_t noteOffsetInSamples;
};

class Section {
    std::string mName;
    int32_t mRepetitions;
    double mTempo;
    int32_t mBeatsPerMeasure;
    NoteType mBeatValue;
    std::vector<Note> mNotes;

public:
    Section();
    Section(const jbyte* byteBuffer, size_t* bytesRead);
    inline int getNoteCount() { return mNotes.size(); }
    inline Note& getNote(int index) { return mNotes[index]; }
    inline int32_t getRepetitions() { return mRepetitions; }
    int64_t getLengthInSamples();
    int64_t getLengthOfNoteInSamples(int index);
    SectionNotePosition getNoteSampleOffsetForHeadPosition(int64_t headPosition);
};
