/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package ui;

import myutil.Conversion;
import myutil.GraphicLib;
import ui.util.IconManager;

import javax.swing.*;
import java.awt.*;

/**
 * Class TGCOneLineText
 * Internal component that is a onle line text
 * Creation: 21/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.0 21/12/2003
 */
public class TGCOneLineText extends TGCWithoutInternalComponent implements ColorCustomizable {

    protected boolean emptyText;

    public TGCOneLineText(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

        nbConnectingPoint = 0;
        minWidth = scale(10);
        nbInternalTGComponent = 0;

        moveable = true;
        editable = true;
        removable = false;

        emptyText = false;

        name = "value ";

        myImageIcon = IconManager.imgic302;
    }

    @Override
    protected void internalDrawing(Graphics g) {
        if (!tdp.isScaled()) {
            width = g.getFontMetrics().stringWidth(value);
            height = g.getFontMetrics().getHeight();
        }

        Color c = g.getColor();
        if (getCurrentColor() != null) {
            g.setColor(getCurrentColor());
        }

        boolean drawn = true;
        if (hasFather()) {
            if (getFather() instanceof FatherDependencyDrawingInterface) {
                drawn = ((FatherDependencyDrawingInterface)getFather()).shallBeDrawn(this);
            }
        }
        if (drawn) {
            if (value.isEmpty()) {
                g.drawString("value?", x, y);
            } else {
                g.drawString(value, x, y);
            }
        }

        g.setColor(c);
    }

    @Override
    public TGComponent isOnMe(int _x, int _y) {
        if (GraphicLib.isInRectangle(_x, _y, x, y - height, Math.max(width, minWidth), height)) {
            return this;
        }
        return null;
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String oldValue = value;
        String text = getName() + ": ";
        if (hasFather()) {
            text = getTopLevelName() + " / " + text;
        }
        String s = (String) JOptionPane.showInputDialog(frame, text,
                "setting value", JOptionPane.PLAIN_MESSAGE, IconManager.imgic101,
                null,
                getValue());

        if (s != null) {
            s = Conversion.removeFirstSpaces(s);
        }

        //

        if ((s != null) && ((emptyText) || s.length() > 0) && (!s.equals(oldValue))) {
            setValue(s);
            //
            return true;
        }


        return false;
    }

    /* Issue #69
     *  (non-Javadoc)
     * @see ui.TGComponent#canBeDisabled()
     */
    @Override
    public boolean canBeDisabled() {
        return getFather() != null && getFather().canLabelBeDisabled(this);
    }

    // Color management
    public Color getMainColor() {
        return Color.BLACK;
    }

}
