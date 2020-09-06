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

enum class Tuplet {
    NONE = 0,
    PUT_3_INTO_2 = 1,
    PUT_5_INTO_4 = 2,
    PUT_6_INTO_4 = 3,
    PUT_7_INTO_4 = 4,
    PUT_7_INTO_8 = 5,
    PUT_9_INTO_8 = 6
};

NoteType noteTypeFromNotesPerWhole(int32_t notesPerWhole);

Tuplet tupletFromCommonId(int32_t id);
