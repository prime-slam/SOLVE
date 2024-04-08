#version 330 core

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
                color = texture(uTextures[0], fTexCoords);
                break;
            case 1:
                color = texture(uTextures[1], fTexCoords);
                break;
            case 2:
                color = texture(uTextures[2], fTexCoords);
                break;
            case 3:
                color = texture(uTextures[3], fTexCoords);
                break;
            case 4:
                color = texture(uTextures[4], fTexCoords);
                break;
            case 5:
                color = texture(uTextures[5], fTexCoords);
                break;
            case 6:
                color = texture(uTextures[6], fTexCoords);
                break;
            case 7:
                color = texture(uTextures[7], fTexCoords);
                break;
        }

        if (color == vec4(0, 0, 0, 1)) {
            discard;
        }
    } else {
        discard;
    }
}
