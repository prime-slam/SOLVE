#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];

out vec4 color;

void main()
{
    if (fTexId > 0) {
        int id = int(fTexId);
        switch (id) {
            case 0:
                color = fColor * texture(uTextures[0], fTexCoords);
                break;
            case 1:
                color = fColor * texture(uTextures[1], fTexCoords);
                break;
            case 2:
                color = fColor * texture(uTextures[2], fTexCoords);
                break;
            case 3:
                color = fColor * texture(uTextures[3], fTexCoords);
                break;
            case 4:
                color = fColor * texture(uTextures[4], fTexCoords);
                break;
            case 5:
                color = fColor * texture(uTextures[5], fTexCoords);
                break;
            case 6:
                color = fColor * texture(uTextures[6], fTexCoords);
                break;
            case 7:
                color = fColor * texture(uTextures[7], fTexCoords);
                break;
        }
    } else {
        color = fColor;
    }
}
