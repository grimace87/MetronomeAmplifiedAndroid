#include "byteswap.h"

int16_t reverseBytesI16(int16_t from) {
    union {
        int16_t num;
        unsigned char bytes[2];
    } u{from}, v{0};
    v.bytes[0] = u.bytes[1];
    v.bytes[1] = u.bytes[0];
    return v.num;
}

int32_t reverseBytesI32(int32_t from) {
    union {
        int32_t num;
        unsigned char bytes[4];
    } u{from}, v{0};
    v.bytes[0] = u.bytes[3];
    v.bytes[1] = u.bytes[2];
    v.bytes[2] = u.bytes[1];
    v.bytes[3] = u.bytes[0];
    return v.num;
}

float reverseBytesF32(float from) {
    union {
        float num;
        unsigned char bytes[4];
    } u{from}, v{0.0f};
    v.bytes[0] = u.bytes[3];
    v.bytes[1] = u.bytes[2];
    v.bytes[2] = u.bytes[1];
    v.bytes[3] = u.bytes[0];
    return v.num;
}

double reverseBytesF64(double from) {
    union {
        double num;
        unsigned char bytes[8];
    } u{from}, v{0.0f};
    v.bytes[0] = u.bytes[7];
    v.bytes[1] = u.bytes[6];
    v.bytes[2] = u.bytes[5];
    v.bytes[3] = u.bytes[4];
    v.bytes[4] = u.bytes[3];
    v.bytes[5] = u.bytes[2];
    v.bytes[6] = u.bytes[1];
    v.bytes[7] = u.bytes[0];
    return v.num;
}
