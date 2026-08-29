#version 330

#moj_import <minecraft:globals.glsl>

uniform sampler2D SceneSampler;

in vec2 plumeUv;
in vec4 parameters;

out vec4 fragColor;

void main() {
    vec2 plumePosition = plumeUv * 2.0 - 1.0;
    float plumeWidth = mix(0.55, 0.95, plumeUv.y);
    float sideFade = 1.0 - smoothstep(plumeWidth * 0.55, plumeWidth, abs(plumePosition.x));
    float bottomFade = smoothstep(0.0, 0.12, plumeUv.y);
    float topFade = 1.0 - smoothstep(0.55, 1.0, plumeUv.y);
    float mask = sideFade * bottomFade * topFade;
    if (mask < 0.001) {
        discard;
    }

    float time = GameTime * 6.28318530718;
    float phase = parameters.g * 6.28318530718;
    float waveA = sin(plumeUv.y * 24.0 - time * 120.0 + sin(plumeUv.x * 9.0 + time * 41.0 + phase));
    float waveB = sin(plumeUv.x * 17.0 + plumeUv.y * 31.0 - time * 83.0 + phase * 1.7);

    vec2 sceneSize = vec2(textureSize(SceneSampler, 0));
    vec2 pixelSize = 1.0 / sceneSize;
    float strength = parameters.r;
    vec2 offset = vec2(waveA + waveB * 0.45, waveB * 0.20);
    offset *= pixelSize * mix(4.0, 10.0, strength) * mask;

    vec2 screenUv = gl_FragCoord.xy / sceneSize;
    screenUv = clamp(screenUv + offset, pixelSize, vec2(1.0) - pixelSize);

    vec3 refracted = texture(SceneSampler, screenUv).rgb;
    float alpha = mask * mix(0.32, 0.68, strength) * parameters.a;
    fragColor = vec4(refracted, alpha);
}
