#version 330 core

in vec4 fColor;

out vec4 color;

void main()
{
    color = fColor;
    float alphaSquare = color.w * color.w;
    color.x *= alphaSquare;
    color.y *= alphaSquare;
    color.z *= alphaSquare;
}