
precision mediump float;

varying vec2 vTextureCoord;

uniform sampler2D uTextureSampler;
uniform sampler2D uNormalMapSampler;
uniform float uProgress;

void main() {
    vec3 rawNormal = texture2D(uNormalMapSampler, vTextureCoord).rgb;
    vec3 normalisedNormal = 2.0 * rawNormal - 1.0;

    float alpha = 1.0 - pow(uProgress, 2.0 - 1.5 * normalisedNormal.z);

    float yGain;
    if (normalisedNormal.y < 0.0) {
        yGain = -0.6 * normalisedNormal.y;
    } else {
        yGain = 0.2 * normalisedNormal.y;
    }

    float distortionX = 0.1 * normalisedNormal.r * uProgress;
    float distortionY = yGain * normalisedNormal.g * uProgress;
    vec2 adjustedSampleCoord = vec2(distortionX, distortionY) + vTextureCoord;
    gl_FragColor = vec4(texture2D(uTextureSampler, adjustedSampleCoord).rgb, alpha);
}
