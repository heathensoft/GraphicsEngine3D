#version 430

in vec4 fColor;

out vec4 color;

vec3 calc_normal(vec3 posViewSpace) {
    return normalize(cross(dFdx(posViewSpace), dFdy(posViewSpace)));
}

void main() {

    color = fColor;

}
