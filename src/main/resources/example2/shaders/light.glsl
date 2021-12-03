
struct Attenuation {
    float constant;
    float linear;
    float quadratic;
};

struct Phong {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct PointLight {
    Phong components;
    Attenuation att;
    vec3 position;
};

struct SpotLight {
    PointLight light;
    vec3 conedir;
    float cutoff;
    float cutoffOuter;
};

struct DirectionalLight {
    Phong components;
    vec3 direction;
};

struct Material {
    Phong components;
    float shine;
    float alpha;
};

// i can pass in view direction to all functions
//vec3 viewDir = normalize(-fragPos);
// vec3 viewdir = normalize(camera_pos - fragpos); -> -fragpos (view space)

vec3 calcDirLight(DirectionalLight light, Material material, vec3 fragPos, vec3 viewDir, vec3 normal) {
    vec3 ambient = light.components.ambient * material.components.ambient;

    vec3 lightDir = normalize(light.direction);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = light.components.diffuse * material.components.diffuse * diff;

    vec3 reflectDir = normalize(reflect(-lightDir, normal)); // not sure if i need to normalize
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shine);
    vec3 specular = light.components.specular * material.components.specular * spec;

    return (ambient + diffuse + specular);
}

vec3 calcPointLight(PointLight light, Material material, vec3 fragPos, vec3 viewDir, vec3 normal) {

    vec3 lightVec = light.position - fragPos;
    vec3 lightDir = normalize(lightVec);
    float diff = max(dot(normal, lightDir), 0.0);

    vec3 reflectDir = normalize(reflect(-lightDir, normal));
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shine);

    float d = length(lightVec);
    float att = 1.0 / (light.att.constant + light.att.linear * d + light.att.quadratic * d * d);

    vec3 ambient = light.components.ambient * material.components.ambient;
    vec3 diffuse = light.components.diffuse * material.components.diffuse * diff;
    vec3 specular = light.components.specular * material.components.specular * spec;

    ambient *= att;
    diffuse *= att;
    specular *= att;

    return (ambient + diffuse + specular);
}

vec3 calcSpotLight(SpotLight spotlight, Material material, vec3 fragPos, vec3 viewDir, vec3 normal) {

    PointLight light = spotlight.light;

    vec3 lightVec = light.position - fragPos;
    vec3 lightDir = normalize(lightVec);
    float diff = max(dot(normal, lightDir), 0.0);

    vec3 reflectDir = normalize(reflect(-lightDir, normal));
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shine);

    float theta = dot(lightDir, normalize(-spotight.direction)); // might need to inverse this
    float epsilon = (spotlight.cutoff - spotlight.cutoffOuter);
    float intensity = clamp((theta - spotlight.cutoffOuter) / epsilon, 0.0, 1.0);

    float d = length(lightVec);
    float att = 1.0 / (light.att.constant + light.att.linear * d + light.att.quadratic * d * d);

    vec3 ambient = light.components.ambient * material.components.ambient;
    vec3 diffuse = light.components.diffuse * material.components.diffuse * diff;
    vec3 specular = light.components.specular * material.components.specular * spec;

    ambient *= att * intensity;
    diffuse *= att * intensity;
    specular *= att * intensity;

    return (ambient + diffuse + specular);
}
