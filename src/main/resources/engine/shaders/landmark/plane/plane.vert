#version 330 core

layout (location=0) in vec2 aPos;
layout (location=1) in vec2 aTexCoords;
layout (location=2) in float aTexId;

uniform mat4 uProjection;

out vec2 fTexCoords;
out float fTexId;

void main()
{
    fTexCoords = aTexCoords;
    fTexId = aTexId;

    gl_Position = uProjection * vec4(aPos, 0.0, 1.0);
}
