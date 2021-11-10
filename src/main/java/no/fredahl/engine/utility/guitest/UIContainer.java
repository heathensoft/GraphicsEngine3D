package no.fredahl.engine.utility.guitest;

import no.fredahl.engine.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frederik Dahl
 * 07/11/2021
 */


public abstract class UIContainer extends UIComponent {
    
    protected Color backgroundColor;
    protected List<UIComponent> children;
    
    public UIContainer() {
        super();
        children = new ArrayList<>();
        backgroundColor = Color.RED;
        calculatePosition();
        calculateSize();
    }
    
    protected abstract Size calculateContentSize();
    
    protected abstract void calculateContentPositions();
    
    @Override
    public void update(float dt) {
        for (UIComponent component : children)
            component.update(dt);
        calculatePosition();
        calculateSize();
    }
    
    private void calculateSize() {
        Size contentSize = calculateContentSize();
        size.set(
                padding.horizontal() + contentSize.width,
                padding.vertical() + contentSize.height
        );
    }
    
    private void calculatePosition() {
        position.set(margin.left, margin.top);
        calculateContentPositions();
    }
    
    public void getSprite() {
    
        //graphics.setColor(color);
        //graphics.fillRect(0,0,size.width, size.height);
        
        // then draw children here -->
    }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
    
    public void addComponent(UIComponent uiComponent) {
        children.add(uiComponent);
    }
    
}
