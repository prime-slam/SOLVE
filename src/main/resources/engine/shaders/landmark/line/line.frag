#version 330 core

in vec4 fColor;

uniform int uUseCommonColor;

out vec4 color;

void main()
{
    vec4 premulitpliedColor = fColor;
    float alphaFactor = pow(fColor.w, 2);
    premulitpliedColor.x *= alphaFactor;
    premulitpliedColor.y *= alphaFactor;
    premulitpliedColor.z *= alphaFactor;
    color = premulitpliedColor;
}
