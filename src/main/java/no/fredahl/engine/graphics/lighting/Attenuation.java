package no.fredahl.engine.graphics.lighting;

/**
 * @author Frederik Dahl
 * 02/12/2021
 */


public class Attenuation {
    
    // https://wiki.ogre3d.org/tiki-index.php?page=-Point+Light+Attenuation
    
    public static final Attenuation ATT_7 = new Attenuation(1.0f,0.7f,1.8f);
    public static final Attenuation ATT_13 = new Attenuation(1.0f,0.35f,0.44f);
    public static final Attenuation ATT_20 = new Attenuation(1.0f,0.22f,0.20f);
    public static final Attenuation ATT_32 = new Attenuation(1.0f,0.14f,0.07f);
    public static final Attenuation ATT_50 = new Attenuation(1.0f,0.09f,0.032f);
    public static final Attenuation ATT_65 = new Attenuation(1.0f,0.07f,0.017f);
    public static final Attenuation ATT_100 = new Attenuation(1.0f,0.045f,0.0075f);
    public static final Attenuation ATT_160 = new Attenuation(1.0f,0.027f,0.0028f);
    public static final Attenuation ATT_200 = new Attenuation(1.0f,0.022f,0.0019f);
    public static final Attenuation ATT_325 = new Attenuation(1.0f,0.014f,0.0007f);
    public static final Attenuation ATT_600 = new Attenuation(1.0f,0.007f,0.0002f);
    public static final Attenuation ATT_3250 = new Attenuation(1.0f,0.0014f,0.000007f);
    
    private float constant;
    private float linear;
    private float quadratic;
    
    public Attenuation(float c, float l, float q) {
        this.constant = c;
        this.linear = l;
        this.quadratic = q;
    }
    
    public Attenuation(Attenuation attenuation) {
        if (attenuation == null) {
            this.constant = ATT_65.constant;
            this.linear = ATT_65.linear;
            this.quadratic = ATT_65.quadratic;
        }
        else {
            this.constant = attenuation.constant;
            this.linear = attenuation.linear;
            this.quadratic = attenuation.quadratic;
        }
    }
    
    public void set(Attenuation attenuation) {
        if (attenuation != null) {
            this.constant = attenuation.constant;
            this.linear = attenuation.linear;
            this.quadratic = attenuation.quadratic;
        }
    }
    
    public void set(float c, float l, float q) {
        this.constant = c;
        this.linear = l;
        this.quadratic = q;
    }
    
    public float constant() {
        return constant;
    }
    
    public float linear() {
        return linear;
    }
    
    public float quadratic() {
        return quadratic;
    }
    
    public void setConstant(float constant) {
        this.constant = constant;
    }
    
    public void setLinear(float linear) {
        this.linear = linear;
    }
    
    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }
    
}
