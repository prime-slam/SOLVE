#version 330 core

layout (location=0) in float aIndex;

uniform mat4 uProjection;
uniform mat4 uModel;
uniform int uGridWidth;

out VS_OUT {
    int frameID;
} vs_out;

void main()
{
    int index = int(aIndex);
    int xPos = index % uGridWidth;
    int yPos = index / uGridWidth;
    gl_Position = vec4(xPos, yPos, 0.0, 1.0);
    vs_out.frameID = index;
}
