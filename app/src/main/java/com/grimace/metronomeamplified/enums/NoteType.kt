package com.grimace.metronomeamplified.enums

enum class NoteType(val mNotesPerWhole: Int) {
    WHOLE(1),
    HALF(2),
    QUARTER(4),
    EIGHTH(8),
    SIXTEENTH(16),
    THIRTY_SECOND(32)
}