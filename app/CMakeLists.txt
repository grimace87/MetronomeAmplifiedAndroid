
cmake_minimum_required(VERSION 3.4.1)

add_library(nativeaudio
            SHARED
            src/main/cpp/main.cpp
            src/main/cpp/audiostreamer.cpp)

include_directories(src/main/cpp/oboe/include)

set (OBOE_DIR src/main/cpp/oboe)
add_subdirectory (${OBOE_DIR} ./oboe)
include_directories (${OBOE_DIR}/include)
target_link_libraries(nativeaudio oboe)
