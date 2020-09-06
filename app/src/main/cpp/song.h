#pragma once

#include "section.h"
#include <jni.h>
#include <string>

class Song {
    std::string mName;
    std::vector<Section> mSections;

public:
    Song();
    Song(const jbyte* byteBuffer, size_t* bytesRead);
};
