#version 330 core

in vec2 fLocalPos;

out vec4 color;

void main()
{
    float localRadius = dot(fLocalPos, fLocalPos);
    if (length(localRadius) > 1) {
        discard;
    }

    color = vec4(1, 0, 0, 1);
}
