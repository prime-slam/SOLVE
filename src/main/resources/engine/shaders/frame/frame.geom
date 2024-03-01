#version 330 core

uniform mat4 uProjection;
uniform mat4 uModel;
uniform int uGridWidth;
uniform ivec2 uBuffersSize;
uniform float uTexturesRatio;

in VS_OUT {
    int frameID;
    bool isColored;
} gs_in[];

out float fTexID;
out vec2 fTexCoords;

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

void main() {
    bool isColored = gs_in[0].isColored;

    if (isColored) {
        int frameID = gs_in[0].frameID;
        int frameX = frameID % uGridWidth;
        int frameY = frameID / uGridWidth;

        int bufferX = frameX % uBuffersSize.x;
        int bufferY = frameY % uBuffersSize.y;
        float texID = float(bufferY * uBuffersSize.x + bufferX);

        vec4 initialPosition = vec4(
            gl_in[0].gl_Position.x * uTexturesRatio,
            gl_in[0].gl_Position.y,
            gl_in[0].gl_Position.z,
            gl_in[0].gl_Position.w
        );
        gl_Position = uProjection * uModel * initialPosition;
        fTexID = texID;
        fTexCoords = vec2(0.0, 1.0);
        EmitVertex();

        gl_Position = uProjection * uModel * (initialPosition + vec4(uTexturesRatio, 0.0, 0.0, 0.0));
        fTexID = texID;
        fTexCoords = vec2(1.0, 1.0);
        EmitVertex();

        gl_Position = uProjection * uModel * (initialPosition + vec4(0.0, 1.0, 0.0, 0.0));
        fTexID = texID;
        fTexCoords = vec2(0.0, 0.0);
        EmitVertex();

        gl_Position = uProjection * uModel * (initialPosition + vec4(uTexturesRatio, 1.0, 0.0, 0.0));
        fTexID = texID;
        fTexCoords = vec2(1.0, 0.0);
        EmitVertex();

        EndPrimitive();
    } else {
        gl_Position = vec4(0.0, 0.0, 0.0, 0.0);
        fTexID = -1.0;
        fTexCoords = vec2(0.0, 0.0);
        EmitVertex();

        EndPrimitive();
    }
}
