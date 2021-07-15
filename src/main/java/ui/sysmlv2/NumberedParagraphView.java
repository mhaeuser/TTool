
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
package ui.sysmlv2;
import javax.swing.text.*;

import java.awt.*;

import myutil.*;

/**
 * Class NumberedParagraphView
 * Sysmlv2 edition
 * Creation: 15/07/2021
 * @version 1.0 15/07/2021
 * @author Ludovic APVRILLE
 */
class NumberedParagraphView extends ParagraphView {
    public static short NUMBERS_WIDTH=25;
    public static short TEXT_DEC = 5;
    public static short NUMBER_DEC = 4;

    //private int nb = 0;
    private JPanelForEdition jpfe;


    public NumberedParagraphView(Element e, JPanelForEdition _jpfe) {
        super(e);
        jpfe = _jpfe;
        short left = 0;
        short bottom = 0;
        short right = 0;
    }

    public void paintChild(Graphics g, Rectangle r, int n) {
        super.paintChild(g, r, n);
        //TraceManager.addDev("paint child... n=" + n +  "nb=" + nb);
        //nb++;
        //if (n == 0) {
        int [] lines = getPreviousLineCounts();
        //int previousLineCount = getPreviousLineCount();
        jpfe.addCorrespondance(lines[0], lines[1], lines[2]);
        if (lines[3] == -1) {
            jpfe.setMaxIndex(lines[0]+1);
        }
        //g.drawString(Integer.toString(previousLineCount + n + 1),numberX, numberY);

        //}
    }

    public int[] getPreviousLineCounts() {
        int [] lines = new int[4];
        View parent = this.getParent();
        int count = parent.getViewCount();
        for (int i = 0; i < count; i++) {
            if (parent.getView(i) == this) {
                lines[0] += parent.getView(i).getViewCount();
                lines[2] = parent.getView(i).getViewCount();
                lines[1] += 1;
                // last view?
                if (i == count-1) {
                    lines[3] = -1;
                }
                //lines[2] = getViewCount();
                break;
            }
            else {
                lines[0] += parent.getView(i).getViewCount();
                lines[2] = parent.getView(i).getViewCount();
                lines[1] += 1;
                //jpfe.addCorrespondance(lines[0], lines[1], lines[2]);
            }
        }
        return lines;
    }


    public int getPreviousLineCount() {
        int lineCount = 0;
        View parent = this.getParent();
        int count = parent.getViewCount();
        for (int i = 0; i < count; i++) {
            if (parent.getView(i) == this) {
                break;
            }
            else {
                lineCount = lineCount + 1;
            }
        }
        return lineCount;
    }


} // Class
