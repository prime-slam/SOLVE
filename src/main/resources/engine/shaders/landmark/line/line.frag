#version 330 core

in vec4 fColor;

uniform int uUseCommonColor;

out vec4 color;

void main()
{
    vec4 premulitpliedColor = fColor;
    float alpha = fColor.w;
    premulitpliedColor.x *= alpha;
    premulitpliedColor.y *= alpha;
    premulitpliedColor.z *= alpha;
    color = premulitpliedColor;
}
