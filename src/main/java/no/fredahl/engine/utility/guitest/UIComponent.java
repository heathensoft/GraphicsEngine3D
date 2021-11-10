package no.fredahl.engine.utility.guitest;


/**
 * @author Frederik Dahl
 * 07/11/2021
 */


public abstract class UIComponent {

    protected int id;
    protected Size size;
    protected Position position;
    protected Spacing margin;
    protected Spacing padding;
    
    public UIComponent() {
        position = new Position(0,0);
        size = new Size(1,1);
        margin = new Spacing(5);
        padding = new Spacing(5);
    }
    
    public abstract void update(float dt);
    
    public Size size() {
        return size;
    }
    
    public void setSize(Size size) {
        this.size = size;
    }
    
    public Position position() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    public void setPosition(int x, int y) {
        this.position.set(x,y);
    }
    
    public Spacing margin() {
        return margin;
    }
    
    public void setMargin(Spacing margin) {
        this.margin = margin;
    }
    
    public Spacing padding() {
        return padding;
    }
    
    public void setPadding(Spacing padding) {
        this.padding = padding;
    }
}
