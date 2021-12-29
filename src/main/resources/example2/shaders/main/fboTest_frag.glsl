#version 430

out vec4 color;

in VS_OUT {
    vec2 texCoords;
} _in;

uniform sampler2D u_depthTexture;

void main()
{
    color = vec4(0.0,0.0,0.0,1.0);
    color.r = texture(u_depthTexture, _in.texCoords).r;
}
