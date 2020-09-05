#pragma once

#include <cstdint>

enum class NoteType {
    WHOLE = 1,
    HALF = 2,
    QUARTER = 4,
    EIGHTH = 8,
    SIXTEENTH = 16,
    THIRTY_SECOND = 32
};

NoteType noteTypeFromNotesPerWhole(int32_t notesPerWhole);
