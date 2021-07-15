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


import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Class LineHighlight
 * Sysmlv2 edition
 * Creation: 15/07/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.0 15/07/2021
 */
class LineHighlight extends DefaultHighlighter.DefaultHighlightPainter implements
        CaretListener, MouseListener, MouseMotionListener, KeyListener {
    private JTextComponent component;
    private DefaultHighlighter highlighter;
    private Object lastHighlight;

    public LineHighlight(JTextComponent component, Color color) {
        super(color);
        this.component = component;
        // "highlighter" is responsible to make the "highlights"
        highlighter = (DefaultHighlighter) component.getHighlighter();
        /*
		with "true" the "highlights" will be painted in the method "paintLayer",
		before the text is painted.
		*/
        highlighter.setDrawsLayeredHighlights(true);
        //  Add listener so we know when to change highlighting
        component.addKeyListener(this);
        component.addCaretListener(this);
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        //  Initially highlight the first line
        addHighlight(0);
    }

    //"paintLayer" overrides the corresponding method of the class "DefaultHighlightPainter"
    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
                            JTextComponent c, View view) {
        try {
            // Only use the first offset to get the line to highlight
            Rectangle r = c.modelToView(offs0);
            r.x = 0;
            r.width = c.getSize().width;
            // --- render ---
            g.setColor(getColor());
            g.fillRect(r.x, r.y, r.width, r.height);
            return r;
        } catch (BadLocationException e) {
            return null;
        }
    }

    /*
     * Remove/add the highlight to make sure it gets repainted
     */
    private void resetHighlight() {
			/*
			"SwingUtilities.invokeLater(...)":
			The run() method contained in the "Runnable" is only called, when all pending
			events are processed (KeyEvent, MouseEvent, CaretEvent, ...).
			According to the dokumentation the mthode "invokeLater(...)" should be used when a
			programm modifies the GUI, which is here the case.
			*/
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                highlighter.removeHighlight(lastHighlight);
						/*
						The "Document" contains the data structure of the "JEditorPane".
						The structure is hierarchically and always has a"root"-element.
						*/
                Element root = component.getDocument().getDefaultRootElement();
						/*
						Takes the number from the Child-element(that is the line) which is next to the specified "offset".
						The "offset" is specified relative to the beginning of the Document. Here the "offset" is equal to the
						position of the "text insertion caret", that is the position where the text is inserted.
						In short: here the current linenumber is fetched.
						*/
                int line = root.getElementIndex(component.getCaretPosition());
                //Gets the Child-element(that is the line) with the specified number.
                Element lineElement = root.getElement(line);
						/*Gets the position where thos element(that is this line) begins,
						relative to the beginning of the Document*/
                int start = lineElement.getStartOffset();
                addHighlight(start);
            }
        });
    }

    private void addHighlight(int offset) {
        try {
            lastHighlight = highlighter.addHighlight(offset, offset + 1,
                    this);
        } catch (BadLocationException ble) {
        }
    }

    // Removes our private highlights
    public void removeHighlight() {
        highlighter.removeHighlight(lastHighlight);
    }

    //  Implement CaretListener
    public void caretUpdate(CaretEvent e) {
        resetHighlight();
    }

    //  Implement MouseListener
    public void mousePressed(MouseEvent e) {
        resetHighlight();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        component.repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        removeHighlight();
    }

    // Implement KeyListener
    public void keyReleased(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}

