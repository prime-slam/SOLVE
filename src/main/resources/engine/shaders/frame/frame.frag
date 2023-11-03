#version 330 core

in vec2 fTexCoords;

uniform sampler2D uTextures[2];

out vec4 color;

void main()
{
    color = texture(uTextures[1], fTexCoords);
}
