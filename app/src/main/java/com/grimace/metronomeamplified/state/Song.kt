package com.grimace.metronomeamplified.state

import com.grimace.metronomeamplified.extensions.writeUTFString4ByteAligned
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Song private constructor(name: String, sections: List<Section>) {

    private val mName: String = name
    private val mSections: List<Section> = sections

    companion object Factory {
        fun newDefault(): Song = Song(
            name = "New Song",
            sections = listOf(
                Section.stockSection1(),
                Section.stockSection2())
        )
    }

    fun asByteBuffer(): ByteBuffer {
        val byteArrayStream = ByteArrayOutputStream()
        val dataStream = DataOutputStream(byteArrayStream)
        dataStream.writeUTFString4ByteAligned(mName)
        dataStream.writeInt(mSections.size)
        mSections.forEach { section -> section.writeToStream(dataStream) }
        val byteArray = byteArrayStream.toByteArray()
        val buffer = ByteBuffer.allocateDirect(byteArray.size).order(ByteOrder.nativeOrder())
        buffer.put(byteArray)
        buffer.position(0)
        return buffer
    }
}