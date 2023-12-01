#version 330 core

layout (location=0) in float aIndex;

uniform mat4 uProjection;
uniform mat4 uModel;
uniform int uGridWidth;
uniform ivec2 uBuffersSize;
uniform vec2 uCameraPosition;
uniform float uTexturesRatio;

out VS_OUT {
    int frameID;
    bool isColored;
} vs_out;

void main()
{
    int index = int(aIndex);
    vs_out.frameID = index;

    vec2 cameraGridPosition = vec2(uCameraPosition.x / uTexturesRatio, uCameraPosition.y);
    vec2 framePosition = vec2(index % uGridWidth, index / uGridWidth);
    vec2 frameCameraPosition = framePosition - cameraGridPosition;
    if (frameCameraPosition.x < 0 ||
        frameCameraPosition.x >= uBuffersSize.x ||
        frameCameraPosition.y < 0 ||
        frameCameraPosition.y >= uBuffersSize.y) {
        vs_out.isColored = false;
    } else {
        vs_out.isColored = true;
    }
    vs_out.isColored = true;
    gl_Position = vec4(framePosition, 0.0, 1.0);
}
