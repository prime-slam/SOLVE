#version 330 core

layout (location=0) in vec2 aPos;
layout (location=1) in float aIndex;
layout (location=2) in vec3 aColor;

uniform mat4 uProjection;

out float fIndex;
out vec3 fColor;

void main()
{
    fIndex = aIndex;
    fColor = aColor;
    gl_Position = uProjection * vec4(aPos, 0.0, 1.0);
}
