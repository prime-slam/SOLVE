#version 330 core

uniform mat4 uProjection;
uniform mat4 uModel;
uniform int uGridWidth;
uniform ivec2 uBuffersSize;

in VS_OUT {
    int frameID;
} gs_in[];

out vec2 fTexCoords;

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

void main() {
    int frameID = gs_in[0].frameID;
    int frameX = frameID / uGridWidth;
    int frameY = int(mod(frameID, uGridWidth));

    int bufferX = int(mod(frameID, uBuffersSize.x));
    int bufferY = int(mod(frameID, uBuffersSize.y));

    gl_Position = uProjection * uModel * gl_in[0].gl_Position;
    fTexCoords = vec2(0.0, 1.0);
    EmitVertex();

    gl_Position = uProjection * uModel * (gl_in[0].gl_Position + vec4(1.0, 0.0, 0.0, 0.0));
    fTexCoords = vec2(1.0, 1.0);
    EmitVertex();

    gl_Position = uProjection * uModel * (gl_in[0].gl_Position + vec4(0.0, 1.0, 0.0, 0.0));
    fTexCoords = vec2(0.0, 0.0);
    EmitVertex();

    gl_Position = uProjection * uModel * (gl_in[0].gl_Position + vec4(1.0, 1.0, 0.0, 0.0));
    fTexCoords = vec2(1.0, 0.0);
    EmitVertex();

    EndPrimitive();
}
