#version 330 core

in vec2 fLocalPos;
in vec3 fColor;

uniform int uUseCommonColor;

out vec4 color;

void main()
{
    float localRadius = dot(fLocalPos, fLocalPos);
    if (length(localRadius) > 1) {
        discard;
    }

    color = vec4(fColor, 1.0);
}
