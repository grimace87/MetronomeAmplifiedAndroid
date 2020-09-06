package com.grimace.metronomeamplified.state

import com.grimace.metronomeamplified.enums.NoteType
import com.grimace.metronomeamplified.enums.Tuplet
import java.io.DataOutputStream

internal class Note private constructor(
    noteType: NoteType,
    accent: Int,
    isSound: Boolean,
    isDotted: Boolean,
    tuplet: Tuplet,
    tieString: Int) {

    var mNoteType: NoteType = noteType
    var mAccent: Int = accent
    var mIsSound: Boolean = isSound
    var mIsDotted: Boolean = isDotted
    var mTuplet: Tuplet = tuplet
    var mTieString: Int = tieString

    var mNoteValue: Double = 0.0
    var mImageIndex: Int = 0

    init {
        calculateDerivedAttributes()
    }

    constructor(noteType: NoteType, accent: Int, isSound: Boolean = true) : this(
        noteType = noteType,
        accent = accent,
        isSound = isSound,
        isDotted = false,
        tuplet = Tuplet.NONE,
        tieString = 0
    )

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
                isSound = other.mIsSound,
                noteType = other.mNoteType,
                isDotted = other.mIsDotted,
                tuplet = other.mTuplet,
                tieString = other.mTieString,
                accent = other.mAccent
            )
        }
    }

    fun writeToStream(dataStream: DataOutputStream) {
        dataStream.writeInt(mNoteType.mNotesPerWhole)
        dataStream.writeInt(mAccent)
        dataStream.writeInt(if (mIsSound) 1 else 0)
        dataStream.writeInt(if (mIsDotted) 1 else 0)
        dataStream.writeInt(mTuplet.mId)
        dataStream.writeInt(mTieString)
        dataStream.writeDouble(mNoteValue)
    }

    fun calculateDerivedAttributes() {

        var noteValue: Double
        var imageIndex: Int
        when (mNoteType) {
            NoteType.WHOLE -> {
                noteValue = 4.0
                imageIndex = 0
            }
            NoteType.HALF -> {
                noteValue = 2.0
                imageIndex = 1
            }
            NoteType.QUARTER -> {
                noteValue = 1.0
                imageIndex = 2
            }
            NoteType.EIGHTH -> {
                noteValue = 0.5
                imageIndex = 3
            }
            NoteType.SIXTEENTH -> {
                noteValue = 0.25
                imageIndex = 4
            }
            NoteType.THIRTY_SECOND -> {
                noteValue = 0.125
                imageIndex = 5
            }
        }

        if (mIsDotted) {
            imageIndex += 6
            noteValue *= 1.5
        }

        if (!mIsSound) {
            imageIndex += 12
        }

        noteValue = when (mTuplet) {
            Tuplet.NONE -> noteValue
            Tuplet.PUT_3_INTO_2 -> noteValue * 2.0 / 3.0
            Tuplet.PUT_5_INTO_4 -> noteValue * 4.0 / 5.0
            Tuplet.PUT_6_INTO_4 -> noteValue * 4.0 / 6.0
            Tuplet.PUT_7_INTO_4 -> noteValue * 4.0 / 7.0
            Tuplet.PUT_7_INTO_8 -> noteValue * 8.0 / 7.0
            Tuplet.PUT_9_INTO_8 -> noteValue * 8.0 / 9.0
        }

        mNoteValue = noteValue
        mImageIndex = imageIndex
    }
}