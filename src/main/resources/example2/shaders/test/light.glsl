
// author: Frederik Dahl

struct Attenuation {
    float constant;
    float linear;
    float quadratic;
};

struct PointLight {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    vec3 position;
    Attenuation att;
};

struct SpotLight {
    vec3 conedir;
    float cutoffInner;
    float cutoffOuter;
    PointLight light;
};

struct DirectionalLight {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    vec3 direction;
};

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    vec3 emissive;
    float shine;
    float alpha;
};


// vec3 viewDir = normalize(-fragPos);
// vec3 viewdir = normalize(camera_pos - fragpos); -> -fragpos (view space)

const float PI = 3.14159265;

vec3 calcDirLight(DirectionalLight light, Material material, vec3 fragPos, vec3 viewDir, vec3 normal) {

    vec3 lightDir = normalize(light.direction); // light.direction possibly needs to be negated
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading (blinn-phong)
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float energyConservation = ( 8.0 + material.shine ) / ( 8.0 * PI );
    float spec = pow(max(dot(normal,halfwayDir),0.0),material.shine) * energyConservation;
    // specular shading (phong)
    //vec3 reflectDir = normalize(reflect(-lightDir, normal)); // not sure if i need to normalize
    //float energyConservation = ( 2.0 + material.shine ) / ( 2.0 * PI );
    //float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shine) * energyConservation;
    // calc results
    vec3 ambient = light.ambient * material.ambient;
    vec3 diffuse = light.diffuse * material.diffuse * diff;
    vec3 specular = light.specular * material.specular * spec;
    return (ambient + diffuse + specular);
}

vec3 calcPointLight(PointLight light, Material material, vec3 fragPos, vec3 viewDir, vec3 normal) {

    vec3 lightVec = light.position - fragPos;
    vec3 lightDir = normalize(lightVec);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading (blinn-phong)
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float energyConservation = ( 8.0 + material.shine ) / ( 8.0 * PI );
    float spec = pow(max(dot(normal,halfwayDir),0.0),material.shine) * energyConservation;
    // specular shading (phong)
    //vec3 reflectDir = normalize(reflect(-lightDir, normal));
    //float energyConservation = ( 2.0 + material.shine ) / ( 2.0 * PI );
    //float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shine) * energyConservation;
    // attenuation
    float d = length(lightVec);
    float att = 1.0 / (light.att.constant + light.att.linear * d + light.att.quadratic * d * d);
    // combine results
    vec3 ambient = light.ambient * material.ambient;
    vec3 diffuse = light.diffuse * material.diffuse * diff;
    vec3 specular = light.specular * material.specular * spec;
    ambient *= att;
    diffuse *= att;
    specular *= att;
    return (ambient + diffuse + specular);
}

vec3 calcSpotLight(SpotLight spotlight, Material material, vec3 fragPos, vec3 viewDir, vec3 normal) {

    PointLight light = spotlight.light;
    vec3 lightVec = light.position - fragPos;
    vec3 lightDir = normalize(lightVec);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading (blinn-phong)
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float energyConservation = ( 8.0 + material.shine ) / ( 8.0 * PI );
    float spec = pow(max(dot(normal,halfwayDir),0.0),material.shine) * energyConservation;
    // specular shading (phong)
    //vec3 reflectDir = normalize(reflect(-lightDir, normal));
    //float energyConservation = ( 2.0 + material.shine ) / ( 2.0 * PI );
    //float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shine) * energyConservation;
    // spotlight intensity
    float theta = dot(-lightDir, normalize(-spotlight.conedir)); // might need to inverse this
    float epsilon = (spotlight.cutoffInner - spotlight.cutoffOuter);
    float intensity = clamp((theta - spotlight.cutoffOuter) / epsilon, 0.0, 1.0);
    // attenuation
    float d = length(lightVec);
    float att = 1.0 / (light.att.constant + light.att.linear * d + light.att.quadratic * d * d);
    // combine results
    vec3 ambient = light.ambient * material.ambient;
    vec3 diffuse = light.diffuse * material.diffuse * diff;
    vec3 specular = light.specular * material.specular * spec;
    ambient *= att * intensity;
    diffuse *= att * intensity;
    specular *= att * intensity;
    return (ambient + diffuse + specular);
}
