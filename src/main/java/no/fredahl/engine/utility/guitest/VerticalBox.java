package no.fredahl.engine.utility.guitest;

/**
 * @author Frederik Dahl
 * 07/11/2021
 */


public class VerticalBox extends UIContainer {
    
    
    @Override
    protected Size calculateContentSize() {
        int combinedChildHeight = 0;
        int widestChildWidth = 0;
        for (UIComponent component : children) {
            combinedChildHeight += component.size.height;
            combinedChildHeight += component.margin.vertical();
            if (component.size.width > widestChildWidth) {
                widestChildWidth = component.size.width;
            }
        }
        return size.set(widestChildWidth,combinedChildHeight);
    }
    
    @Override
    protected void calculateContentPositions() {
        int currentY = padding.top;
        for (UIComponent component : children) {
            currentY += component.margin.top;
            component.setPosition(padding.left,currentY);
            currentY += component.size.height;
            currentY += component.margin.bottom;
        }
    }
    
}
