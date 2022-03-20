#version 430

out vec4 FragColor;

in vec2 texCoords;

uniform sampler2D uTexture;

void main()
{
    FragColor = texture(uTexture, texCoords);
}
