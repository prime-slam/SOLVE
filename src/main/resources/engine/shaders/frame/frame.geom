#version 330 core

uniform mat4 uProjection;
uniform mat4 uModel;
uniform int uGridWidth;
uniform ivec2 uBuffersSize;

in VS_OUT {
    int frameID;
} gs_in[];

out float fTexID;
out vec2 fTexCoords;

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

void main() {
    int frameID = gs_in[0].frameID;
    int frameX = int(mod(frameID, uGridWidth));
    int frameY = frameID / uGridWidth;

    int bufferX = int(mod(frameX, uBuffersSize.x));
    int bufferY = int(mod(frameY, uBuffersSize.y));
    float texID = float(bufferY * uBuffersSize.x + bufferX);

    gl_Position = uProjection * uModel * gl_in[0].gl_Position;
    fTexID = texID;
    fTexCoords = vec2(0.0, 1.0);
    EmitVertex();

    gl_Position = uProjection * uModel * (gl_in[0].gl_Position + vec4(1.0, 0.0, 0.0, 0.0));
    fTexID = texID;
    fTexCoords = vec2(1.0, 1.0);
    EmitVertex();

    gl_Position = uProjection * uModel * (gl_in[0].gl_Position + vec4(0.0, 1.0, 0.0, 0.0));
    fTexID = texID;
    fTexCoords = vec2(0.0, 0.0);
    EmitVertex();

    gl_Position = uProjection * uModel * (gl_in[0].gl_Position + vec4(1.0, 1.0, 0.0, 0.0));
    fTexID = texID;
    fTexCoords = vec2(1.0, 0.0);
    EmitVertex();

    EndPrimitive();
}
