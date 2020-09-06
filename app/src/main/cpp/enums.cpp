#include "enums.h"

NoteType noteTypeFromNotesPerWhole(int32_t notesPerWhole) {
    switch (notesPerWhole) {
        case 1:
            return NoteType::WHOLE;
        case 2:
            return NoteType::HALF;
        case 4:
            return NoteType::QUARTER;
        case 8:
            return NoteType::EIGHTH;
        case 16:
            return NoteType::SIXTEENTH;
        case 32:
            return NoteType::THIRTY_SECOND;
        default:
            return NoteType::WHOLE;
    }
}

Tuplet tupletFromCommonId(int32_t id) {
    switch (id) {
        case 0:
            return Tuplet::NONE;
        case 1:
            return Tuplet::PUT_3_INTO_2;
        case 2:
            return Tuplet::PUT_5_INTO_4;
        case 3:
            return Tuplet::PUT_6_INTO_4;
        case 4:
            return Tuplet::PUT_7_INTO_4;
        case 5:
            return Tuplet::PUT_7_INTO_8;
        case 6:
            return Tuplet::PUT_9_INTO_8;
        default:
            return Tuplet::NONE;
    }
}
