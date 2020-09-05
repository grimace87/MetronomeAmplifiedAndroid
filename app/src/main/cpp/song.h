#pragma once

#include "section.h"
#include <jni.h>
#include <string>

class Song {
    std::string mName;
    Section mSection;

public:
    Song();
    Song(const jbyte* byteBuffer, size_t* bytesRead);
};
