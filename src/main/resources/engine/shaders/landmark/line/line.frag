#version 330 core

in float fIndex;

uniform int uUseCommonColor;
uniform vec3 uCommonColor;

out vec4 color;

float random(float index)
{
    vec2 seedVector = vec2(index, 133);

    return mod(fract(sin(dot(seedVector.xy, vec2(12.9898, 78.233))) * 43758.5453123), 1.0);
}

void main()
{
    if (uUseCommonColor == 1) {
        color = vec4(uCommonColor, 1);
    } else {
        color = vec4(random(fIndex), random(fIndex + 7), random(fIndex + 19), 1.0);
    }
}