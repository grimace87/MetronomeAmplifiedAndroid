package com.grimace.metronomeamplified.state

import com.grimace.metronomeamplified.enums.NoteType
import com.grimace.metronomeamplified.extensions.writeUTFString4ByteAligned
import java.io.DataOutputStream

internal class Section private constructor(
    name: String,
    tempo: Double,
    beatsPerMeasure: Int,
    beatValue: NoteType,
    notes: List<Note>) {

    private val mName: String = name
    private val mTempo: Double = tempo
    private val mBeatsPerMeasure: Int = beatsPerMeasure
    private val mBeatValue: NoteType = beatValue
    private val mNotes: List<Note> = notes

    companion object Factory {
        fun newDefault(): Section {
            return Section(
                name = "New Section",
                tempo = 108.0,
                beatsPerMeasure = 4,
                beatValue = NoteType.QUARTER,
                notes = listOf(
                    Note(NoteType.QUARTER, accent = 1),
                    Note(NoteType.EIGHTH, accent = 0),
                    Note(NoteType.EIGHTH, accent = 0),
                    Note(NoteType.QUARTER, accent = 3),
                    Note(NoteType.QUARTER, accent = 0)
                )
            )
        }

        fun copy(other: Section): Section {
            return Section(
                name = other.mName,
                tempo = other.mTempo,
                beatsPerMeasure = other.mBeatsPerMeasure,
                beatValue = other.mBeatValue,
                notes = other.mNotes.map { Note.copy(it) }
            )
        }
    }

    fun writeToStream(dataStream: DataOutputStream) {
        dataStream.writeUTFString4ByteAligned(mName)
        dataStream.writeFloat(mTempo.toFloat())
        dataStream.writeInt(mBeatsPerMeasure)
        dataStream.writeInt(mBeatValue.mNotesPerWhole)
        dataStream.writeInt(mNotes.size)
        mNotes.forEach { note -> note.writeToStream(dataStream) }
    }
}