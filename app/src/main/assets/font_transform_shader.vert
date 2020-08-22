
attribute vec4 aPosition;
attribute vec2 aTextureCoord;

uniform mat4 uTransform;

varying vec2 vTextureCoord;

void main() {
    gl_Position = uTransform * aPosition;
    vTextureCoord = aTextureCoord;
}
