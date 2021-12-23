
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

vec3 a_source;
vec3 d_source;
vec3 s_source;
vec3 e_source;

float ec;
float shine;
float alpha;
float shadow;

vec3 a_sum = vec3(.0,.0,.0);
vec3 d_sum = vec3(.0,.0,.0);
vec3 s_sum = vec3(.0,.0,.0);
vec3 e_sum = vec3(.0,.0,.0);


float energyConservation(float shine) {
    return ( 16.0 + shine ) / ( 16.0 * 3.14159265 );
}

float calc_shadow(vec4 pos, sampler2D shadowMap) {
    vec3 coords = pos.xyz;
    float shadowFactor = 0.0;
    float bias = 0.05;
    vec2 inc = 1.0 / textureSize(shadowMap,0);
    coords = coords * 0.5 + 0.5;
    for(int r = -1; r <= 1; ++r){
        for(int c = -1; c <= 1; ++c){
            float textDepth = texture(shadowMap, coords.xy + vec2(r,c) * inc).r;
            shadowFactor += coords.z - bias > textDepth ? 1.0 : 0.0;
        }
    }
    shadowFactor /= 9.0;
    if(coords.z > 1.0) {
        shadowFactor = 1.0;
    }
    return (1 - shadowFactor);
}

void calc_dirlight(DirectionalLight l, vec3 eye, vec3 norm) {
    vec3 lightDir = normalize(l.direction);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 halfwayDir = normalize(lightDir + eye);
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * ec;
    a_sum += (l.color * a_source * l.ambient);
    d_sum += (l.color * d_source * l.diffuse * diff) * shadow;
    s_sum += (l.color * s_source * spec) * shadow;
}

void calc_pointlight(PointLight l, vec3 pos, vec3 eye, vec3 norm) {
    vec3 lightVec = l.position - pos;
    vec3 lightDir = normalize(lightVec);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 halfwayDir = normalize(lightDir + eye);
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * ec;
    float d = length(lightVec);
    float att = 1.0 / (l.constant + l.linear * d + l.quadratic * d * d);
    a_sum += (l.color * a_source * l.ambient) * att;
    d_sum += (l.color * d_source * l.diffuse * diff) * att;
    s_sum += (l.color * s_source * spec) * att;
}

void calc_spotlight(SpotLight l, vec3 pos, vec3 eye, vec3 norm) {
    vec3 lightVec = l.position - pos;
    vec3 lightDir = normalize(lightVec);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 halfwayDir = normalize(lightDir + eye);
    float spec = pow(max(dot(norm,halfwayDir),0.0),shine) * ec;
    float theta = dot(-lightDir, normalize(-l.coneDir));
    float epsilon = (l.innerCutoff - l.outerCutoff);
    float intensity = clamp((theta - l.outerCutoff) / epsilon, 0.0, 1.0);
    float d = length(lightVec);
    float att = 1.0 / (l.constant + l.linear * d + l.quadratic * d * d);
    a_sum += (l.color * a_source * l.ambient) * att * intensity;
    d_sum += (l.color * d_source * l.diffuse * diff) * att * intensity;
    s_sum += (l.color * s_source * spec) * att * intensity;
}


