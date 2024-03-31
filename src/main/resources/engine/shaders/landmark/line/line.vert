#version 330 core

layout (location=0) in vec2 aPos;
layout (location=1) in float aIndex;

uniform mat4 uProjection;

out float fIndex;

void main()
{
    fIndex = aIndex;
    gl_Position = uProjection * vec4(aPos, 0.0, 1.0);
}
