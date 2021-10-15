/**
 * OutputUtils.java
 *
 * Created on 15. 10. 2021, 20:41:44 by burgetr
 */
package cz.vutbr.fit.layout.demo;

import cz.vutbr.fit.layout.model.Box;

/**
 * 
 * @author burgetr
 */
public class OutputUtils
{

    public static void printTextBoxes(Box box)
    {
        if (box.getType() == Box.Type.TEXT_CONTENT)
        {
            // box coordinates
            System.out.print("[" + box.getBounds().getX1() + ":" + box.getBounds().getY1() + "] ");
            // box text
            System.out.print(box.getText());
            // font size
            System.out.println(" (" + box.getTextStyle().getFontSize() + "px font)");
        }
        else if (box.getType() == Box.Type.ELEMENT)
        {
            for (Box child : box.getChildren())
            {
                printTextBoxes(child);
            }
        }
    }
    
}
