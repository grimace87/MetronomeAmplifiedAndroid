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
