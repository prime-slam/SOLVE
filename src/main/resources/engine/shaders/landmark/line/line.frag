#version 330 core

in vec4 fColor;

uniform int uUseCommonColor;

out vec4 color;

void main()
{
    vec4 premulitpliedColor = fColor;
    float alphaSquare = fColor.w * fColor.w;
    premulitpliedColor.x *= alphaSquare;
    premulitpliedColor.y *= alphaSquare;
    premulitpliedColor.z *= alphaSquare;
    color = premulitpliedColor;
}
