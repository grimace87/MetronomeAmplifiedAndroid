
precision mediump float;

varying vec2 vTextureCoord;

uniform sampler2D uTextureSampler;
uniform vec4 uPaintColor;

void main() {
    float sampleAlpha = texture2D(uTextureSampler, vTextureCoord).a;
    gl_FragColor = vec4(uPaintColor.rgb, uPaintColor.a * sampleAlpha);
}
