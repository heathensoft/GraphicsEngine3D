#version 330

in vec4 v_out_color;
in vec2 v_out_texCoords;
out vec4 f_out_color;


uniform sampler2D texture_sampler;

void main()
{

    f_out_color = texture(texture_sampler,v_out_texCoords);

    //f_out_color = vec4(1,1,1,1); //v_out_color;

}
