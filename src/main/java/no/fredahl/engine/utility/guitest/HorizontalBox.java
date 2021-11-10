package no.fredahl.engine.utility.guitest;

/**
 * @author Frederik Dahl
 * 07/11/2021
 */


public class HorizontalBox extends UIContainer {
    
    
    @Override
    protected Size calculateContentSize() {
        int combinedChildWidth = 0;
        int tallestChildHeight = 0;
        for (UIComponent component : children) {
            combinedChildWidth += component.size.width;
            combinedChildWidth += component.margin.horizontal();
            if (component.size.height > tallestChildHeight) {
                tallestChildHeight = component.size.height;
            }
        }
        return size.set(combinedChildWidth,tallestChildHeight);
    }
    
    @Override
    protected void calculateContentPositions() {
        int currentX = padding.left;
        for (UIComponent component : children) {
            currentX += component.margin.left;
            component.setPosition(currentX,padding.top);
            currentX += component.size.width;
            currentX += component.margin.right;
        }
    }
    
}
