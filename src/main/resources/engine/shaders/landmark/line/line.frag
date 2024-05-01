#version 330 core

in float fIndex;
in vec3 fColor;

uniform int uUseCommonColor;

out vec4 color;

void main()
{
    color = vec4(fColor, 1.0);
}