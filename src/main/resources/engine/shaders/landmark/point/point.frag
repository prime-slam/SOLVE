#version 330 core

in vec2 fLocalPos;
in vec3 fColor;

uniform int uUseCommonColor;

out vec4 color;

void main()
{
    float localRadius = dot(fLocalPos, fLocalPos);
    if (localRadius > 1) {
        discard;
    }

    if (localRadius < 0.6)
        color = vec4(fColor, 1);
    else
        color = vec4(fColor, pow((1 - localRadius), 1.8));

    float alphaFactor = pow(color.w, 1.8);
    color.x *= alphaFactor;
    color.y *= alphaFactor;
    color.z *= alphaFactor;
}
