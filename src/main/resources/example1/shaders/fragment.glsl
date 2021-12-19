#version 420

//in vec4 v_out_color;
//in vec2 v_out_texCoords;


out vec4 f_out_color;

in VS_OUT {
    vec2 texCoords;
    vec4 color;
} fs_in;

uniform sampler2D texture_sampler;

void main()
{

    f_out_color = texture(texture_sampler,fs_in.texCoords);

    //f_out_color = vec4(1,1,1,1); //v_out_color;

}
