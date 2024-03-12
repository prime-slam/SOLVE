#version 330 core

layout (location=0) in vec2 aPos;
layout (location=1) in vec2 aLocalPos;

uniform mat4 uProjection;

out vec2 fLocalPos;

void main()
{
    fLocalPos = aLocalPos.xy;
    gl_Position = uProjection * vec4(aPos, 0.0, 1.0);
}
