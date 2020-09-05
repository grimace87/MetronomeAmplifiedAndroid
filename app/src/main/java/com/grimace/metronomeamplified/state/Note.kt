package com.grimace.metronomeamplified.state

import com.grimace.metronomeamplified.enums.NoteType
import java.io.DataOutputStream

internal class Note(
    noteType: NoteType,
    accent: Int) {

    var mNoteType: NoteType = noteType
    var mAccent: Int = accent

    /*
     * The indices used for the image file array:
     * 0-5:   semibreve ... demisemiquaver
     * 6-11:  dottedsemibreve ... dotteddemisemiquaver
     * 12-17: semibreverest ... demisemibreverest
     * 18-23: dottedsemibreverest ... dotteddemisemiquaverrest
     */

    companion object Factory {
        fun copy(other: Note): Note {
            return Note(
                noteType = other.mNoteType,
                accent = other.mAccent
            )
        }
    }

    fun writeToStream(dataStream: DataOutputStream) {
        dataStream.writeInt(mNoteType.mNotesPerWhole)
        dataStream.writeInt(mAccent)
    }
}