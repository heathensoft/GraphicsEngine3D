

// author: Frederik Dahl

// Fitted to the STD140 format for uniform blocks (ORDERING IS CRITICAL)
// https://www.oreilly.com/library/view/opengl-programming-guide/9780132748445/app09lev1sec2.html
// Each struct is tightly packed to fit 16-byte blocks.

// with this setup, light only has a vec3 for color, not 3 seperate vec3's for ambient, diffuse and specular.
// Instead it has ambient and diffuse (intensity) as floats.
// light has no specular specific component. It's treated to always be 1.0 (or 1.0, 1.0, 1.0)
//

struct Material {
    vec3 ambient;
    float emission;
    vec3 diffuse;
    float alpha;
    vec3 specular;
    float shine;
};

struct DirectionalLight {
    vec3 color;
    float ambient;
    vec3 direction;
    float diffuse;
};

struct PointLight {
    vec3 color;
    float intensity;
    vec3 position;
    float ambient;
    float diffuse;
    float constant;
    float linear;
    float quadratic;
};

struct SpotLight {
    vec3 color;
    float ambient;
    vec3 position;
    float diffuse;
    vec3 coneDir;
    float constant;
    float linear;
    float quadratic;
    float innerCutoff;
    float outerCutoff;
};

const float PI = 3.14159265;

vec3 calc_dir_light(DirectionalLight l, vec3 eye, vec3 norm) {

    vec3 lightDir = normalize(l.direction);
    float diff = max(dot(norm, lightDir), 0.0);

    vec3 halfwayDir = normalize(lightDir + eye);
    float energyConservation = ( 32.0 + shine ) / ( 32.0 * PI );
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * energyConservation;

    vec3 a = l.color * a_color * l.ambient;
    vec3 d = l.color * d_color * l.diffuse * diff;
    vec3 s = l.color * s_color * spec;

    return (a + d + s);
}

vec3 calc_point_light(PointLight l, vec3 pos, vec3 eye, vec3 norm) {

    vec3 lightVec = l.position - pos;
    vec3 lightDir = normalize(lightVec);
    float diff = max(dot(norm, lightDir), 0.0);

    vec3 halfwayDir = normalize(lightDir + eye);
    float energyConservation = ( 32.0 + shine ) / ( 32.0 * PI );
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * energyConservation;

    float dist = length(lightVec);
    float att = 1.0 / (l.constant + l.linear * dist + l.quadratic * dist * dist);

    vec3 a = l.color * a_color * l.ambient;
    vec3 d = l.color * d_color * l.diffuse * diff;
    vec3 s = l.color * s_color * spec;

    a *= att;
    d *= att;
    s *= att;

    return (a + d + s);
}

vec3 calc_spot_light(SpotLight l, vec3 pos, vec3 eye, vec3 norm) {

    vec3 lightVec = l.position - pos;
    vec3 lightDir = normalize(lightVec);
    float diff = max(dot(norm, lightDir), 0.0);

    vec3 halfwayDir = normalize(lightDir + eye);
    float energyConservation = ( 32.0 + shine ) / ( 32.0 * PI );
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * energyConservation;

    float theta = dot(-lightDir, normalize(-l.coneDir));
    float epsilon = (l.innerCutoff - l.outerCutoff);
    float intensity = clamp((theta - l.outerCutoff) / epsilon, 0.0, 1.0);

    float dist = length(lightVec);
    float att = 1.0 / (l.constant + l.linear * dist + l.quadratic * dist * dist);

    vec3 a = l.color * a_color * l.ambient;
    vec3 d = l.color * d_color * l.diffuse * diff;
    vec3 s = l.color * s_color * spec;

    a *= att * intensity;
    d *= att * intensity;
    s *= att * intensity;

    return (a + d + s);
}


