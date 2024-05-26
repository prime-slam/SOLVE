#version 330 core

in float fTexID;
in vec2 fTexCoords;

uniform sampler2DArray uTextures;

out vec4 color;

void main()
{
    if (fTexID == -1) {
        color = vec4(0.0, 0.0, 0.0, 0.0);
    } else {
        color = texture(uTextures, vec3(fTexCoords, fTexID));
    }
}
