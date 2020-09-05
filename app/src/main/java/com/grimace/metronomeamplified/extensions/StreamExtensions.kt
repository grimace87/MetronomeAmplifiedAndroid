package com.grimace.metronomeamplified.extensions

import java.io.DataOutputStream

fun DataOutputStream.writeUTFString4ByteAligned(string: String) {
    writeUTF(string)
    val postSize = size()
    val paddingBytes = (4 - postSize % 4) % 4
    for (i in 0.until(paddingBytes)) {
        writeByte(0)
    }
}
