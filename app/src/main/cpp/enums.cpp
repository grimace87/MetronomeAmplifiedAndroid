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

double noteValueOf(NoteType noteType) {
    switch (noteType) {
        case NoteType::WHOLE:
            return 4.0;
        case NoteType::HALF:
            return 2.0;
        case NoteType::QUARTER:
            return 1.0;
        case NoteType::EIGHTH:
            return 0.5;
        case NoteType::SIXTEENTH:
            return 0.25;
        case NoteType::THIRTY_SECOND:
            return 0.125;
        default:
            return 1.0;
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
