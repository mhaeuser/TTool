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

package ui.eln.sca_eln;

import myutil.GraphicLib;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.eln.*;
import ui.window.JDialogELNComponentCapacitor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class ELNComponentCapacitor 
 * Capacitor to be used in ELN diagrams 
 * Creation: 12/06/2018
 * @version 1.0 12/06/2018
 * @author Irina Kit Yan LEE
 */

public class ELNComponentCapacitor extends TGCScalableWithInternalComponent implements ActionListener, SwallowedTGComponent, ELNPrimitiveComponent {
	protected Color myColor;
	protected int orientation;
	private int maxFontSize = 14;
	private int minFontSize = 4;
	private int currentFontSize = -1;

	private int textX = 15;
	private double dtextX = 0.0;
	protected int decPoint = 3;

	private double val, q0;
	private String unit0, unit1;

	private int position = 0;
	private boolean fv_0_2 = false, fv_1_3 = false, fh_0_2 = false, fh_1_3 = false;
	private int old;
	private boolean first;

	public ELNComponentCapacitor(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);

		initScaling(100, 40);

		dtextX = textX * oldScaleFactor;
		textX = (int) dtextX;
		dtextX = dtextX - textX;

		minWidth = 1;
		minHeight = 1;

		initPortTerminal(2);

		addTGConnectingPointsComment();

		moveable = true;
		editable = true;
		removable = true;
		userResizable = false;
		value = tdp.findELNComponentName("C");

		setVal(1.0);
		setQ0(0.0);
		setUnit0("F");
		setUnit1("C");

		old = width;
		width = height;
		height = old;
	}

	public void initPortTerminal(int nb) {
		nbConnectingPoint = nb;
		connectingPoint = new TGConnectingPoint[nb];
		connectingPoint[0] = new ELNConnectingPoint(this, 0, 0, true, true, 0.0, 0.0, "p");
		connectingPoint[1] = new ELNConnectingPoint(this, 0, 0, true, true, 0.0, 0.0, "n");
	}

	public Color getMyColor() {
		return myColor;
	}

	public void internalDrawing(Graphics g) {
		Font f = g.getFont();
		Font fold = f;
		MainGUI mgui = getTDiagramPanel().getMainGUI();

		if (this.rescaled && !this.tdp.isScaled()) {
			this.rescaled = false;
			int maxCurrentFontSize = Math.max(0, Math.min(this.height, (int) (this.maxFontSize * this.tdp.getZoom())));
			f = f.deriveFont((float) maxCurrentFontSize);

			while (maxCurrentFontSize > (this.minFontSize * this.tdp.getZoom() - 1)) {
				if (g.getFontMetrics().stringWidth(value) < (width - (2 * textX))) {
					break;
				}
				maxCurrentFontSize--;
				f = f.deriveFont((float) maxCurrentFontSize);
			}

			if (this.currentFontSize < this.minFontSize * this.tdp.getZoom()) {
				maxCurrentFontSize++;
				f = f.deriveFont((float) maxCurrentFontSize);
			}
			g.setFont(f);
			this.currentFontSize = maxCurrentFontSize;
		} else {
			f = f.deriveFont(this.currentFontSize);
		}

		Color c = g.getColor();

		if (position == 0) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
				resizeWithFather();
			}

			rotateTopBottom(g);

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("p");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("n");
			int sh1 = g.getFontMetrics().getAscent();
			g.setFont(f.deriveFont((float) attributeFontSize));
			g.setFont(f);
			g.setFont(f.deriveFont(Font.BOLD));
			int w = g.getFontMetrics().stringWidth(value);
			g.drawString(value, x + (width - w) / 2, y - height / 2);
			g.setFont(f.deriveFont(Font.PLAIN));

			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.0);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setW(1.0);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.5);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - sw0,
							y + height / 2 + height / 4 + sh0);
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width,
							y + height / 2 + height / 4 + sh1);
				}
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(1.0);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.0);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.5);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - sw1,
							y + height / 2 + height / 4 + sh1);
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width,
							y + height / 2 + height / 4 + sh0);
				}
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.0);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setW(1.0);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.5);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - sw0,
							y + height / 2 + height / 4 + sh0);
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width,
							y + height / 2 + height / 4 + sh1);
				}
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(1.0);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.0);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.5);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - sw1,
							y + height / 2 + height / 4 + sh1);
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width,
							y + height / 2 + height / 4 + sh0);
				}
			}
		} else if (position == 1) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
				resizeWithFather();
			}

			rotateRightLeft(g);

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sh0 = g.getFontMetrics().getAscent();
			int sh1 = g.getFontMetrics().getAscent();
			g.setFont(f.deriveFont((float) attributeFontSize));
			g.setFont(f);
			g.setFont(f.deriveFont(Font.BOLD));
			int h = g.getFontMetrics().getAscent();
			g.drawString(value, x + width + width / 2, y + (height + h) / 2);
			g.setFont(f.deriveFont(Font.PLAIN));

			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.0);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setH(1.0);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width / 2 + width / 4, y);
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width / 2 + width / 4,
							y + height + sh1);
				}
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[0]).setH(1.0);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.0);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width / 2 + width / 4, y);
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width / 2 + width / 4,
							y + height + sh0);
				}
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.0);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setH(1.0);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width / 2 + width / 4, y);
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width / 2 + width / 4,
							y + height + sh1);
				}
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[0]).setH(1.0);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.0);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width / 2 + width / 4, y);
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width / 2 + width / 4,
							y + height + sh0);
				}
			}
		} else if (position == 2) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
				resizeWithFather();
			}

			rotateTopBottom(g);

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sw0 = g.getFontMetrics().stringWidth("p");
			int sh0 = g.getFontMetrics().getAscent();
			int sw1 = g.getFontMetrics().stringWidth("n");
			int sh1 = g.getFontMetrics().getAscent();
			g.setFont(f.deriveFont((float) attributeFontSize));
			g.setFont(f);
			g.setFont(f.deriveFont(Font.BOLD));
			int w = g.getFontMetrics().stringWidth(value);
			g.drawString(value, x + (width - w) / 2, y - height / 2);
			g.setFont(f.deriveFont(Font.PLAIN));

			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(1.0);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.0);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.5);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - sw1,
							y + height / 2 + height / 4 + sh1);
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width,
							y + height / 2 + height / 4 + sh0);
				}
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.0);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setW(1.0);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.5);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - sw0,
							y + height / 2 + height / 4 + sh0);
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width,
							y + height / 2 + height / 4 + sh1);
				}
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(1.0);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.0);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.5);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x - sw1,
							y + height / 2 + height / 4 + sh1);
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width,
							y + height / 2 + height / 4 + sh0);
				}
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.0);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setW(1.0);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.5);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x - sw0,
							y + height / 2 + height / 4 + sh0);
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width,
							y + height / 2 + height / 4 + sh1);
				}
			}
		} else if (position == 3) {
			if (first == false) {
				first = true;
				old = width;
				width = height;
				height = old;
				resizeWithFather();
			}

			rotateRightLeft(g);

			int attributeFontSize = this.currentFontSize * 5 / 6;
			int sh0 = g.getFontMetrics().getAscent();
			int sh1 = g.getFontMetrics().getAscent();
			g.setFont(f.deriveFont((float) attributeFontSize));
			g.setFont(f);
			g.setFont(f.deriveFont(Font.BOLD));
			int h = g.getFontMetrics().getAscent();
			g.drawString(value, x + width + width / 2, y + (height + h) / 2);
			g.setFont(f.deriveFont(Font.PLAIN));

			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[0]).setH(1.0);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.0);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width / 2 + width / 4, y);
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width / 2 + width / 4,
							y + height + sh0);
				}
			}
			if ((fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.0);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setH(1.0);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width / 2 + width / 4, y);
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width / 2 + width / 4,
							y + height + sh1);
				}
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == true && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == true && fh_1_3 == false)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[0]).setH(1.0);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setH(0.0);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width / 2 + width / 4, y);
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width / 2 + width / 4,
							y + height + sh0);
				}
			}
			if ((fv_0_2 == true && fv_1_3 == false && fh_0_2 == true && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == true && fh_0_2 == false && fh_1_3 == true)
					|| (fv_0_2 == true && fv_1_3 == true && fh_0_2 == false && fh_1_3 == false)
					|| (fv_0_2 == false && fv_1_3 == false && fh_0_2 == true && fh_1_3 == true)) {
				((ELNConnectingPoint) connectingPoint[0]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[0]).setH(0.0);
				((ELNConnectingPoint) connectingPoint[1]).setW(0.5);
				((ELNConnectingPoint) connectingPoint[1]).setH(1.0);
				if (mgui.getHidden() == false) {
					g.drawString(((ELNConnectingPoint) connectingPoint[0]).getName(), x + width / 2 + width / 4, y);
					g.drawString(((ELNConnectingPoint) connectingPoint[1]).getName(), x + width / 2 + width / 4,
							y + height + sh1);
				}
			}
		}
		g.setColor(c);
		g.setFont(fold);
	}

	private void rotateTopBottom(Graphics g) {
		Color c = g.getColor();
		int[] ptx0 = { x, x + 2 * width / 5, x + 2 * width / 5, x + 2 * width / 5, x + 2 * width / 5 };
		int[] pty0 = { y + height / 2, y + height / 2, y, y + height, y + height / 2 };
		g.drawPolygon(ptx0, pty0, 5);
		int[] ptx1 = { x + width, x + 3 * width / 5, x + 3 * width / 5, x + 3 * width / 5, x + 3 * width / 5 };
		int[] pty1 = { y + height / 2, y + height / 2, y, y + height, y + height / 2 };
		g.drawPolygon(ptx1, pty1, 5);
		g.drawOval(x, y + height / 2 - height / 8, width / 10, height / 4);
		g.setColor(Color.WHITE);
		g.fillOval(x, y + height / 2 - height / 8, width / 10, height / 4);
		g.setColor(c);
		g.drawOval(x + width - width / 10, y + height / 2 - height / 8, width / 10, height / 4);
		g.setColor(Color.WHITE);
		g.fillOval(x + width - width / 10, y + height / 2 - height / 8, width / 10, height / 4);
		g.setColor(c);
	}

	private void rotateRightLeft(Graphics g) {
		Color c = g.getColor();
		int[] ptx0 = { x + width / 2, x + width / 2, x, x + width, x + width / 2 };
		int[] pty0 = { y, y + 2 * height / 5, y + 2 * height / 5, y + 2 * height / 5, y + 2 * height / 5 };
		g.drawPolygon(ptx0, pty0, 5);
		int[] ptx1 = { x + width / 2, x + width / 2, x, x + width, x + width / 2 };
		int[] pty1 = { y + height, y + 3 * height / 5, y + 3 * height / 5, y + 3 * height / 5, y + 3 * height / 5 };
		g.drawPolygon(ptx1, pty1, 5);
		g.drawOval(x + width / 2 - width / 8, y, width / 4, height / 10);
		g.setColor(Color.WHITE);
		g.fillOval(x + width / 2 - width / 8, y, width / 4, height / 10);
		g.setColor(c);
		g.drawOval(x + width / 2 - width / 8, y + height - height / 10, width / 4, height / 10);
		g.setColor(Color.WHITE);
		g.fillOval(x + width / 2 - width / 8, y + height - height / 10, width / 4, height / 10);
		g.setColor(c);
	}

	public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
			return this;
		}
		return null;
	}

	public int getType() {
		return TGComponentManager.ELN_CAPACITOR;
	}

	public boolean editOnDoubleClick(JFrame frame) {
		JDialogELNComponentCapacitor jde = new JDialogELNComponentCapacitor(this);
		jde.setVisible(true);
		return true;
	}

	public StringBuffer encode(String data) {
		StringBuffer databuf = new StringBuffer(data);
		StringBuffer buffer = new StringBuffer("");
		for (int pos = 0; pos != data.length(); pos++) {
			char c = databuf.charAt(pos);
			switch (c) {
			case '\u03BC':
				buffer.append("&#x3BC;");
				break;
			default:
				buffer.append(databuf.charAt(pos));
				break;
			}
		}
		return buffer;
	}

	protected String translateExtraParam() {
		StringBuffer sb = new StringBuffer("<extraparam>\n");
		sb.append("<attributes value=\"" + val);
		sb.append("\" unit0=\"");
		sb.append(encode(unit0));
		sb.append("\" q0=\"" + q0);
		sb.append("\" unit1=\"");
		sb.append(encode(unit1));
		sb.append("\" position=\"" + position);
		sb.append("\" fv_0_2=\"" + fv_0_2);
		sb.append("\" fv_1_3=\"" + fv_1_3);
		sb.append("\" fh_0_2=\"" + fh_0_2);
		sb.append("\" fh_1_3=\"" + fh_1_3);
		sb.append("\" first=\"" + first + "\"");
		sb.append("/>\n");
		sb.append("</extraparam>\n");
		return new String(sb);
	}

	public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException {
		try {
			NodeList nli;
			Node n1, n2;
			Element elt;

			double value, q0;
			String unit0, unit1;
			int position;
			boolean fv_0_2, fv_1_3, fh_0_2, fh_1_3, first;

			for (int i = 0; i < nl.getLength(); i++) {
				n1 = nl.item(i);
				if (n1.getNodeType() == Node.ELEMENT_NODE) {
					nli = n1.getChildNodes();
					for (int j = 0; j < nli.getLength(); j++) {
						n2 = nli.item(j);
						if (n2.getNodeType() == Node.ELEMENT_NODE) {
							elt = (Element) n2;
							if (elt.getTagName().equals("attributes")) {
								value = Double.parseDouble(elt.getAttribute("value"));
								q0 = Double.parseDouble(elt.getAttribute("q0"));
								unit0 = elt.getAttribute("unit0");
								unit1 = elt.getAttribute("unit1");
								position = Integer.parseInt(elt.getAttribute("position"));
								fv_0_2 = Boolean.parseBoolean(elt.getAttribute("fv_0_2"));
								fv_1_3 = Boolean.parseBoolean(elt.getAttribute("fv_1_3"));
								fh_0_2 = Boolean.parseBoolean(elt.getAttribute("fh_0_2"));
								fh_1_3 = Boolean.parseBoolean(elt.getAttribute("fh_1_3"));
								first = Boolean.parseBoolean(elt.getAttribute("first"));
								setVal(value);
								setQ0(q0);
								setUnit0(unit0);
								setUnit1(unit1);
								setPosition(position);
								setFv_0_2(fv_0_2);
								setFv_1_3(fv_1_3);
								setFh_0_2(fh_0_2);
								setFh_1_3(fh_1_3);
								setFirst(first);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new MalformedModelingException();
		}
	}

	public void addActionToPopupMenu(JPopupMenu componentMenu, ActionListener menuAL, int x, int y) {
		componentMenu.addSeparator();

		JMenuItem rotateright = new JMenuItem("Rotate right 90\u00b0");
		rotateright.addActionListener(this);
		componentMenu.add(rotateright);

		JMenuItem rotateleft = new JMenuItem("Rotate left 90\u00b0");
		rotateleft.addActionListener(this);
		componentMenu.add(rotateleft);

		componentMenu.addSeparator();

		JMenuItem rotatevertically = new JMenuItem("Flip vertically");
		rotatevertically.addActionListener(this);
		componentMenu.add(rotatevertically);

		JMenuItem rotatehorizontally = new JMenuItem("Flip horizontally");
		rotatehorizontally.addActionListener(this);
		componentMenu.add(rotatehorizontally);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Rotate right 90\u00b0")) {
			position++;
			position %= 4;
			first = false;
		}
		if (e.getActionCommand().equals("Rotate left 90\u00b0")) {
			position = position + 3;
			position %= 4;
			first = false;
		}
		if (e.getActionCommand().equals("Flip vertically")) {
			if (position == 0 || position == 2) {
				if (fv_0_2 == false) {
					fv_0_2 = true;
				} else {
					fv_0_2 = false;
				}
			}
			if (position == 1 || position == 3) {
				if (fv_1_3 == false) {
					fv_1_3 = true;
				} else {
					fv_1_3 = false;
				}
			}
		}
		if (e.getActionCommand().equals("Flip horizontally")) {
			if (position == 0 || position == 2) {
				if (fh_0_2 == false) {
					fh_0_2 = true;
				} else {
					fh_0_2 = false;
				}
			}
			if (position == 1 || position == 3) {
				if (fh_1_3 == false) {
					fh_1_3 = true;
				} else {
					fh_1_3 = false;
				}
			}
		}
	}

	public int getDefaultConnector() {
		return TGComponentManager.ELN_CONNECTOR;
	}

	public double getVal() {
		return val;
	}

	public void setVal(double _val) {
		val = _val;
	}

	public double getQ0() {
		return q0;
	}

	public void setQ0(double _q0) {
		q0 = _q0;
	}

	public String getUnit0() {
		return unit0;
	}

	public void setUnit0(String _unit0) {
		unit0 = _unit0;
	}

	public String getUnit1() {
		return unit1;
	}

	public void setUnit1(String _unit1) {
		unit1 = _unit1;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int _position) {
		position = _position;
	}

	public boolean isFv_0_2() {
		return fv_0_2;
	}

	public void setFv_0_2(boolean _fv_0_2) {
		fv_0_2 = _fv_0_2;
	}

	public boolean isFv_1_3() {
		return fv_1_3;
	}

	public void setFv_1_3(boolean _fv_1_3) {
		fv_1_3 = _fv_1_3;
	}

	public boolean isFh_0_2() {
		return fh_0_2;
	}

	public void setFh_0_2(boolean _fh_0_2) {
		fh_0_2 = _fh_0_2;
	}

	public boolean isFh_1_3() {
		return fh_1_3;
	}

	public void setFh_1_3(boolean _fh_1_3) {
		fh_1_3 = _fh_1_3;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean _first) {
		first = _first;
	}

	public void resizeWithFather() {
		if ((father != null) && (father instanceof ELNModule)) {
			resizeToFatherSize();

			setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
			setMoveCd(x, y);
		}
	}

	public void wasSwallowed() {
		myColor = null;
	}

	public void wasUnswallowed() {
		myColor = null;
		setFather(null);
		TDiagramPanel tdp = getTDiagramPanel();
		setCdRectangle(tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY());
	}
}