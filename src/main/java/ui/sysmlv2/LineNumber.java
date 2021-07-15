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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import myutil.*;

/**
 * Class LineNumber
 * Sysmlv2 edition
 * Creation: 15/07/2021
 * @version 1.0 15/07/2021
 * @author Ludovic APVRILLE
 */
public class LineNumber extends JComponent {
    private final static Color DEFAULT_BACKGROUND = new Color(235, 230, 225);
    private final static Color DEFAULT_FOREGROUND = Color.black;
    private final static Font  DEFAULT_FONT = new Font("monospaced", Font.PLAIN, 12);
	
    //  LineNumber height (abends when I use MAX_VALUE)
    private final static int HEIGHT = Integer.MAX_VALUE - 1000000;
    //  Set right/left margin
    private final static int MARGIN = 5;
    //  Variables for this LineNumber component
    private FontMetrics fontMetrics;
    private int lineHeight;
    private int currentDigits;
    //  Metrics of the component used in the constructor
    private JComponent component;
    private int componentFontHeight;
    private int componentFontAscent;
	
	private JPanelForEdition jpfe;
	
    /**
     * Convenience constructor for Text Components
     */
    public LineNumber(JComponent component, JPanelForEdition _jpfe) {
        if (component == null) {
            setFont( DEFAULT_FONT );
            this.component = this;
        } else {
            setFont( component.getFont() );
            this.component = component;
        }
		jpfe = _jpfe;
        setBackground( DEFAULT_BACKGROUND );
        setForeground( DEFAULT_FOREGROUND );
        setPreferredWidth( 99 );
    }
    /**
     *  Calculate the width needed to display the maximum line number
     */
    public void setPreferredWidth(int lines) {
        int digits = String.valueOf(lines).length();
        //  Update sizes when number of digits in the line number changes
        if (digits != currentDigits && digits > 1) {
            currentDigits = digits;
            int width = fontMetrics.charWidth( '0' ) * digits;
            Dimension d = getPreferredSize();
            d.setSize(2 * MARGIN + width, HEIGHT);
            setPreferredSize( d );
            setSize( d );
        }
    }
    /**
     *  Reset variables that are dependent on the font.
     */
    public void setFont(Font font) {
        super.setFont(font);
        fontMetrics = getFontMetrics( getFont() );
        componentFontHeight = fontMetrics.getHeight();
        componentFontAscent = fontMetrics.getAscent();
    }
    /**
     *  The line height defaults to the line height of the font for this
     *  component.
     */
    public int getLineHeight() {
        if (lineHeight == 0)
            return componentFontHeight;
        else
            return lineHeight;
    }
    /**
     *  Override the default line height with a positive value.
     *  For example, when you want line numbers for a JTable you could
     *  use the JTable row height.
     */
    public void setLineHeight(int lineHeight) {
        if (lineHeight > 0)
            this.lineHeight = lineHeight;
    }
	
    public int getStartOffset() {
        return component.getInsets().top + componentFontAscent;
    }
	

	
	public void paintComponent(Graphics g) {
        int lineHeight = getLineHeight();
        int startOffset = getStartOffset();
        Rectangle drawHere = g.getClipBounds();
        // Paint the background
        g.setColor( getBackground() );
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);
        //  Determine the number of lines to draw in the foreground.
        g.setColor( getForeground() );
        int startLineNumber = (drawHere.y / lineHeight) + 1;
        int endLineNumber = startLineNumber + (drawHere.height / lineHeight);
        int start = (drawHere.y / lineHeight) * lineHeight + startOffset;
		int selectedLine = jpfe.getSelectedLine();
		Color c = g.getColor();
		for (int i = startLineNumber; i <= endLineNumber; i++) {
			int lineNumber = jpfe.getIntCorrespondance(i);
			//TraceManager.addDev("LineNumber: start=" + startLineNumber + " end=" + endLineNumber + " result at " + i + " = " + lineNumber);
			if (lineNumber > -10) {
				int rowWidth = getSize().width;
				if (lineNumber == -4) {
					g.drawLine(rowWidth - (2*MARGIN), start-lineHeight+1, rowWidth - (2*MARGIN), start);
				} else if (lineNumber == -3) {
					g.drawLine(rowWidth - (2*MARGIN), start-lineHeight+1, rowWidth - (2*MARGIN), start-(lineHeight / 10));
					g.drawLine(rowWidth - (2*MARGIN), start-(lineHeight / 10), rowWidth - MARGIN, start-(lineHeight / 10));
				} else {
					//String lineNumber = String.valueOf(jpfe.getCorrespondance(i));
					//String lineNumber = String.valueOf(i);
					int stringWidth = fontMetrics.stringWidth(""+lineNumber);
					if (lineNumber==selectedLine) {
						g.setColor(Color.MAGENTA);
						g.fillRect(0, start-lineHeight+3, rowWidth, lineHeight);
						g.setColor(Color.GREEN);
					}
					g.drawString(""+lineNumber, rowWidth - stringWidth - MARGIN, start);
					g.setColor(c);
				}
			}
            start += lineHeight;
        }
        int rows = component.getSize().height / componentFontHeight;
        setPreferredWidth( rows );
    }
}

