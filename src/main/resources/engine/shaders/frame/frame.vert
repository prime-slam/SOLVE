#version 330 core

layout (location=0) in vec2 aGridPos;

uniform mat4 uProjection;
uniform mat4 uModel;

out VS_OUT {
    uint frameID;
} vs_out;

void main()
{
    int i = gl_VertexID;

    gl_Position = uProjection * uModel * vec4(aGridPos, 1.0);
    vs_out.frameID = i;
}
