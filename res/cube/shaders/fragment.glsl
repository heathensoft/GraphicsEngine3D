#version 330

in  vec3 v_out_color;
out vec4 f_out_color;

void main()
{
    f_out_color = vec4(v_out_color, 1.0);
}
