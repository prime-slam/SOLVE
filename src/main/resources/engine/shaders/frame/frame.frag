#version 330 core

in vec2 fTexCoords;
in int fTexID;

uniform sampler2DArray uTextures;

out vec4 color;

void main()
{
    color = texture(uTextures, vec3(fTexCoords, fTexID));
}
