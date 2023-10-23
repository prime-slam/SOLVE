#version 330 core

in vec4 fColor;
in vec2 fTexCoords;

out vec4 color;

uniform sampler2D uTex;

void main()
{
    color = texture(uTex, fTexCoords);
}
