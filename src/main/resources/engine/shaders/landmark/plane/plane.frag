#version 330 core

#define PLANES_MAX_NUMBER 128

in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];

uniform int uInteractingPlanesUIDs[PLANES_MAX_NUMBER];
uniform float uInteractingPlanesOpacity[PLANES_MAX_NUMBER];
uniform int uInteractingPlanesNumber;

out vec4 color;

int getIntegerColor(vec3 rgbColor) {
    int red = int(rgbColor.x * 255.0);
    int green = int(rgbColor.y * 255.0);
    int blue = int(rgbColor.z * 255.0);

    return red * 256 * 256 + green * 256 + blue;
}

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

        int intColor = getIntegerColor(color.xyz);
        for (int i = 0; i < uInteractingPlanesNumber; ++i) {
            if (intColor == uInteractingPlanesUIDs[i]) {
                float alpha = uInteractingPlanesOpacity[i];
                color.w = uInteractingPlanesOpacity[i];
                color.x *= uInteractingPlanesOpacity[i];
                color.y *= uInteractingPlanesOpacity[i];
                color.z *= uInteractingPlanesOpacity[i];
                break;
            }
        }

        if (color == vec4(0, 0, 0, 1)) {
            discard;
        }
    } else {
        discard;
    }
}
