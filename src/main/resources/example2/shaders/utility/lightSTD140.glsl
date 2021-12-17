

// author: Frederik Dahl

// Fitted to the STD140 format for uniform blocks (ORDERING IS CRITICAL)
// https://www.oreilly.com/library/view/opengl-programming-guide/9780132748445/app09lev1sec2.html
// Each struct is tightly packed to fit 16-byte blocks.

// with this setup, light only has a vec3 for color, not 3 seperate vec3's for ambient, diffuse and specular.
// Instead it has ambient and diffuse (intensity) as floats.
// light has no specular specific component. It's treated to always be 1.0 (or 1.0, 1.0, 1.0)
//


struct DirectionalLight { // 32 byte
    vec3 color;
    float ambient;
    vec3 direction;
    float diffuse;
};

struct PointLight {  // 48 byte
    vec3 color;
    float intensity; // padding really
    vec3 position;
    float ambient;
    float diffuse;
    float constant;
    float linear;
    float quadratic;
};

struct SpotLight { // 64 byte
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

struct Material { // 48 bytes
    vec3 ambient;
    float emissive;
    vec3 diffuse;
    float alpha;
    vec3 specular;
    float shine;
};

vec3 a_color;
vec3 d_color;
vec3 s_color;
vec3 e_color;
float shine;
float alpha;

const float PI = 3.14159265;

vec3 calc_dir_light(DirectionalLight l, vec3 eye, vec3 norm) {
    vec3 lightDir = normalize(l.direction);
    // diffuse
    float diff = max(dot(norm, lightDir), 0.0);
    // specular
    vec3 halfwayDir = normalize(lightDir + eye);
    float energyConservation = ( 8.0 + shine ) / ( 8.0 * PI );
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * energyConservation;
    // apply
    vec3 a = l.ambient * a_color;
    vec3 d = l.diffuse * d_color * diff;
    vec3 s = s_color * spec;

    return (a + d + s);
}

vec3 calc_point_light(PointLight l, vec3 pos, vec3 eye, vec3 norm) {
    vec3 lightVec = l.position - pos;
    vec3 lightDir = normalize(lightVec);
    // diffuse
    float diff = max(dot(norm, lightDir), 0.0);
    // specular
    vec3 halfwayDir = normalize(lightDir + eye);
    float energyConservation = ( 8.0 + shine ) / ( 8.0 * PI );
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * energyConservation;
    // attenuation
    float d = length(lightVec);
    float att = 1.0 / (l.constant + l.linear * d + l.quadratic * d * d);
    // apply
    vec3 a = l.ambient * a_color;
    vec3 d = l.diffuse * d_color * diff;
    vec3 s = s_color * spec;

    a *= att;
    d *= att;
    d *= att;

    return (a + d + s);
}

vec3 calc_spot_light(SpotLight l, vec3 pos, vec3 eye, vec3 norm) {
    vec3 lightVec = l.position - pos;
    vec3 lightDir = normalize(lightVec);
    // diffuse
    float diff = max(dot(norm, lightDir), 0.0);
    // specular
    vec3 halfwayDir = normalize(lightDir + eye);
    float energyConservation = ( 8.0 + shine ) / ( 8.0 * PI );
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * energyConservation;
    // light intensity based on cone parameters
    float theta = dot(-lightDir, normalize(-l.coneDir)); // might need to inverse this
    float epsilon = (l.innerCutoff - l.outerCutoff);
    float intensity = clamp((theta - l.outerCutoff) / epsilon, 0.0, 1.0);
    // attenuation
    float d = length(lightVec);
    float att = 1.0 / (l.constant + l.linear * d + l.quadratic * d * d);
    // apply
    vec3 a = l.ambient * a_color;
    vec3 d = l.diffuse * d_color * diff;
    vec3 s = s_color * spec;

    a *= att * intensity;
    d *= att * intensity;
    s *= att * intensity;

    return (a + d + s);
}


