package com.grimace.metronomeamplified.state

import com.grimace.metronomeamplified.extensions.writeUTFString4ByteAligned
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Song private constructor(name: String, section: Section) {

    private val mName: String = name
    private val mSection: Section = section

    companion object Factory {
        fun newDefault(): Song = Song(
            name = "New Song",
            section = Section.newDefault()
        )
    }

    fun asByteBuffer(): ByteBuffer {
        val byteArrayStream = ByteArrayOutputStream()
        val dataStream = DataOutputStream(byteArrayStream)
        dataStream.writeUTFString4ByteAligned(mName)
        mSection.writeToStream(dataStream)
        val byteArray = byteArrayStream.toByteArray()
        val buffer = ByteBuffer.allocateDirect(byteArray.size).order(ByteOrder.nativeOrder())
        buffer.put(byteArray)
        buffer.position(0)
        return buffer
    }
}