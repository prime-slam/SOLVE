#version 330 core

uniform mat4 uProjection;
uniform mat4 uModel;
uniform int uGridWidth;
uniform ivec2 uBuffersSize;

in VS_OUT {
    uint frameID;
} gs_in[0];

out vec2 fTexCoords;
out int fTexID;

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

void main() {
    uint frameID = gs_in[0].frameID;
    int frameX = frameID / uGridWidth;
    int frameY = mod(frameID, uGridWidth);

    int bufferX = mod(frameID, uBuffersSize.x);
    int bufferY = mod(frameID, uBuffersSize.y);

    fTexID = bufferY * uBuffersSize.x + bufferX;

    gl_Position = uProjection * uModel * gl_in[0].gl_Position;
    fTexCoords = vec2(-1.0, 1.0);
    EmitVertex();

    gl_Position = uProjection * uModel * (gl_in[0].gl_Position + vec4(1.0, 0.0, 0.0, 0.0));
    fTexCoords = vec2(1.0, 1.0);
    EmitVertex();

    gl_Position = uProjection * uModel * (gl_in[0].gl_Position + vec4(0.0, 1.0, 0.0, 0.0));
    fTexCoords = vec2(-1.0, -1.0);
    EmitVertex();

    gl_Position = uProjection * uModel * (gl_in[0].gl_Position + vec4(1.0, 1.0, 0.0, 0.0));
    fTexCoords = vec2(1.0, -1.0);
    EmitVertex();

    EndPrimitive();
}
