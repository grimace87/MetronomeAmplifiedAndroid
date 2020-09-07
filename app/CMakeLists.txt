
cmake_minimum_required(VERSION 3.4.1)

# Library from project source files
add_library(nativeaudio
            SHARED
            src/main/cpp/main.cpp
            src/main/cpp/audiostreamer.cpp
            src/main/cpp/filebuffers.cpp
            src/main/cpp/wavereader.cpp
            src/main/cpp/songplayer.cpp
            src/main/cpp/byteswap.cpp
            src/main/cpp/enums.cpp
            src/main/cpp/song.cpp
            src/main/cpp/section.cpp
            src/main/cpp/note.cpp)

# Find NDK APIs
find_library(android-lib android)
find_library(log-lib log)

# Build Oboe from sources
include_directories(src/main/cpp/oboe/include)
set (OBOE_DIR src/main/cpp/oboe)
add_subdirectory (${OBOE_DIR} ./oboe)
include_directories (${OBOE_DIR}/include)

# Link project library with NDK APIs and Oboe
target_link_libraries(nativeaudio oboe ${android-lib} ${log-lib})
