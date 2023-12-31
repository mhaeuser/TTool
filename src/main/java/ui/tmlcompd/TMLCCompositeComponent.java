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

package ui.tmlcompd;

import myutil.GraphicLib;
import myutil.NameChecker;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ui.*;
import ui.util.IconManager;

import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Class TMLCCompositeComponent
 * Composite Component. To be used in TML component task diagrams
 * Creation: 11/03/2008
 * @version 1.0 11/03/2008
 * @author Ludovic APVRILLE
 */
public class TMLCCompositeComponent extends TGCScalableWithInternalComponent implements SwallowTGComponent, SwallowedTGComponent,
		HiddenInternalComponents, NameChecker.NameStartWithUpperCase, NameChecker.NameTakenFromValue {
//	private int maxFontSize = 20;
//	private int minFontSize = 4;
	private int currentFontSize = -1;
//	private boolean displayText = true;
//	private int spacePt = 3;
	private Color myColor;
	private int iconSize = 17;

	private int textX = 15; // border for ports
	private double dtextX = 0.0;	
	
	private boolean hiddeni;
	
	private int compositePortNb = 0;
    
    public TMLCCompositeComponent(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
		initScaling(250, 200);
		
		oldScaleFactor = tdp.getZoom();
		dtextX = textX * oldScaleFactor;
		textX = (int)dtextX;
		dtextX = dtextX - textX;
		
        minWidth = 1;
        minHeight = 1;
        
        nbConnectingPoint = 0;
        //connectingPoint = new TGConnectingPoint[0];
        //connectingPoint[0] = new TMLArchiConnectingPoint(this, 0, 0, false, true, 0.0, 0.0);
        
        addTGConnectingPointsComment();
        
        nbInternalTGComponent = 0;
		
		maxWidth = 2000;
		maxHeight = 2000;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
		value = "ComponentName";
		name = "Composite component";
		
        myImageIcon = IconManager.imgic1200;
    }
    
    @Override
    public void internalDrawing(Graphics g)
    {
    	//Color choosing
    	if (myColor == null) {
			if (ColorManager.TML_COMPOSITE_COMPONENT == Color.white) {
				myColor = Color.white;
			} else {
				myColor = new Color(251, 252, 200- (getMyDepth() * 10), 200);
			}
		}
    	
    	//Rectangle & filling
    	Color col = g.getColor();
		g.drawRect(x, y, width, height);
		if ((width > 2) && (height > 2)) {
			g.setColor(myColor);
			g.fillRect(x+1, y+1, width-1, height-1);
			g.setColor(col);
		}
    	
    	//Strings
    	Font f = g.getFont();
    	currentFontSize = f.getSize();
    	if (canTextGoInTheBox(g, currentFontSize, value, iconSize + textX))
    		drawSingleString(g, value, getCenter(g, value), y + currentFontSize + textX);
    	
    	//Icon
    	drawImageWithCheck(g, IconManager.imgic1200.getImage(), x + width - scale(iconSize) - textX, y + textX);
    }
    
   // @Override
//    public void internalDrawin(Graphics g) {
//		int w;
//		int c;
//		Font f = g.getFont();
//		Font fold = f;
//		//FontMetrics fm = g.getFontMetrics();
//		
//		if (myColor == null) {
//			if (ColorManager.TML_COMPOSITE_COMPONENT == Color.white) {
//				myColor = Color.white;
//			} else {
//				myColor = new Color(251, 252, 200- (getMyDepth() * 10), 200);
//			}
//		}
//		
//		if ((rescaled) && (!tdp.isScaled())) {
//			
//			if (currentFontSize == -1) {
//				currentFontSize = f.getSize();
//			}
//			rescaled = false;
//			// Must set the font size ..
//			// Find the biggest font not greater than max_font size
//			// By Increment of 1
//			// Or decrement of 1
//			// If font is less than 4, no text is displayed
//			
//			int maxCurrentFontSize = Math.max(0, Math.min(height-(2*textX), maxFontSize));
//			
//			//f = f.deriveFont((float)maxCurrentFontSize);
//			//g.setFont(f);
//			while(maxCurrentFontSize > (minFontSize-1)) {
//				f = f.deriveFont((float)maxCurrentFontSize);
//				g.setFont(f);
//				w = g.getFontMetrics().stringWidth(value);
//				//w = fm.stringWidth(value);
//				c = width - iconSize - (2 * textX);
//				//
//				if (w < c) {
//					break;
//				}
//				maxCurrentFontSize --;
//				
//			}
//			currentFontSize = maxCurrentFontSize;
//
//            displayText = currentFontSize >= minFontSize;
//		}
//		
//		// Zoom is assumed to be computed
//		Color col = g.getColor();
//		g.drawRect(x, y, width, height);
//		if ((width > 2) && (height > 2)) {
//			g.setColor(myColor);
//			g.fillRect(x+1, y+1, width-1, height-1);
//			g.setColor(col);
//		}
//		
//        // Font size 
//		if (displayText) {
//			f = f.deriveFont((float)currentFontSize);
//			g.setFont(f);
//			w = g.getFontMetrics().stringWidth(value);
//			
//			if (!(w < (width - 2 * (iconSize + textX)))) {
//				drawSingleString(g, value, x + textX + 1, y + currentFontSize + textX);
//			} else {
//				drawSingleString(g, value, x + (width - w)/2, y + currentFontSize + textX);
//			}
//		}
//		
//		g.setFont(fold);
//		
//		// Icon
//		if ((width>30) && (height > (iconSize + 2*textX))) {
//			g.drawImage(IconManager.imgic1200.getImage(), x + width - iconSize - textX, y + textX, null);
//		}
//    }
	
//    @Override
//	public void rescale(double scaleFactor){
//		dtextX = (textX + dtextX) / oldScaleFactor * scaleFactor;
//		textX = (int)(dtextX);
//		dtextX = dtextX - textX; 
//		
//		super.rescale(scaleFactor);
//	}
    
    @Override
    public TGComponent isOnOnlyMe(int _x, int _y) {
		if (GraphicLib.isInRectangle(_x, _y, x, y, width, height)) {
            return this;
        }
        return null;
    }

    @Override
    public boolean editOnDoubleClick(JFrame frame) {
        String s = (String)JOptionPane.showInputDialog(frame, "Name:", "Setting component name",
		JOptionPane.PLAIN_MESSAGE, IconManager.imgic100,
        null,
        getValue());
        if ((s != null) && (s.length() > 0)) {
			if (!TAttribute.isAValidId(s, false, false, false)) {
				JOptionPane.showMessageDialog(frame,
					"Could not change the name of the component: the new name is not a valid name",
					"Error",
					JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			if (((TMLComponentTaskDiagramPanel)(tdp)).isCompositeNameUsed(s)) {
                JOptionPane.showMessageDialog(frame,
                                              "Error: the name is already in use",
                                              "Name modification",
                                              JOptionPane.ERROR_MESSAGE);
                return false;
            }
            setValueWithChange(s);
            return true;
        }
        return false;
    }
	
    @Override
    public int getType() {
        return TGComponentManager.TMLCTD_CCOMPONENT;
    }
	
    @Override
	public void wasSwallowed() {
		myColor = null;
	}
	
    @Override
	public void wasUnswallowed() {
		myColor = null;
		setFather(null);
		TDiagramPanel tdp = getTDiagramPanel();
		setCdRectangle(tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY());
	}
	
    @Override
	public boolean acceptSwallowedTGComponent(TGComponent tgc) {
		if (tgc instanceof TMLCCompositeComponent) {
            return true;
        }
		
		if (tgc instanceof TMLCRecordComponent) {
            return true;
        }
		
		if (tgc instanceof TMLCPrimitiveComponent) {
            return true;
        }
		
		if (tgc instanceof TMLCRemoteCompositeComponent) {
            return true;
        }

        return tgc instanceof TMLCCompositePort;
    }
    
    @Override
    public boolean addSwallowedTGComponent(TGComponent tgc, int x, int y) {
		boolean swallowed = false;
		
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof SwallowTGComponent) {
				if (((SwallowTGComponent)tgcomponent[i]).acceptSwallowedTGComponent(tgc)) {
					if (tgcomponent[i].isOnMe(x, y) != null) {
						swallowed = true;
						((SwallowTGComponent)tgcomponent[i]).addSwallowedTGComponent(tgc, x, y);
						break;
					}
				}
			}
        }

		//issue 188 : attach primitive port into primitive component starting at the third level
		if ((!swallowed) && (tgc instanceof TMLCPrimitivePort)) {
			List<TMLCPrimitiveComponent> pcList = getAllPrimitiveComponents();
			for(TMLCPrimitiveComponent tmlpc : pcList) {
				if(((SwallowTGComponent)tmlpc).acceptSwallowedTGComponent(tgc)) {
					if(tmlpc.isOnMe(x,y) != null) {
						swallowed = true;
						((SwallowTGComponent)tmlpc).addSwallowedTGComponent(tgc, x, y);
						break;
					}
				}
			}
			if (swallowed) {
				return true;
			}
		}
		//------------
		
		if (swallowed) {
			return true;
		}
		
		if (!acceptSwallowedTGComponent(tgc)) {
			return false;
		}
		
        //
        // Choose its position
        
        // Make it an internal component
        // It's one of my son
        tgc.setFather(this);
        tgc.setDrawingZone(true);
        
        //Set its coordinates
        if (tgc instanceof TMLCCompositeComponent) {
            tgc.resizeWithFather();
        }
		
		if (tgc instanceof TMLCRecordComponent) {
            tgc.resizeWithFather();
        }
		
		if (tgc instanceof TMLCPrimitiveComponent) {
            tgc.resizeWithFather();
        }
		
		if (tgc instanceof TMLCRemoteCompositeComponent) {
            tgc.resizeWithFather();
        }
		
		 if (tgc instanceof TMLCCompositePort) {
			 tgc.resizeWithFather();
			 compositePortNb ++;
		 }
        
        // else unknown*/
        
        //add it
        addInternalComponent(tgc, 0);
		
		return true;
    }
    
    @Override
    public void removeSwallowedTGComponent(TGComponent tgc) {
		//
		if (tgc instanceof TMLCCompositePort) {
			portRemoved();
		}
        //removeInternalComponent(tgc);
		 for(int i=0; i<nbInternalTGComponent; i++) {
            if (tgcomponent[i] == tgc) {
				nbInternalTGComponent = nbInternalTGComponent - 1;
                if (nbInternalTGComponent == 0) {
                    tgcomponent = null;
                } else {
                    TGComponent [] tgcomponentbis = new TGComponent[nbInternalTGComponent];
                    for(int j=0; j<nbInternalTGComponent; j++) {
                        if (j<i) {
                            tgcomponentbis[j] = tgcomponent[j];
                        }
                        if (j>=i) {
                            tgcomponentbis[j] = tgcomponent[j+1];
                        }
                    }
                    tgcomponent = tgcomponentbis;
                }
				break;
			}
		 }
    }
    
    @Override
    public void hasBeenResized() {
		rescaled = true;
        for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				tgcomponent[i].resizeWithFather();
			}
			if (tgcomponent[i] instanceof TMLCPrimitiveComponent) {
				tgcomponent[i].resizeWithFather();
			}
			if (tgcomponent[i] instanceof TMLCRecordComponent) {
				tgcomponent[i].resizeWithFather();
			}
			if (tgcomponent[i] instanceof TMLCRemoteCompositeComponent) {
				tgcomponent[i].resizeWithFather();
			}
			if (tgcomponent[i] instanceof TMLCCompositePort) {
				tgcomponent[i].resizeWithFather();
			}
        }
		
		if (getFather() != null) {
			resizeWithFather();
		}
    }
	
    @Override
	public void resizeWithFather() {
        if ((father != null) && ((father instanceof TMLCCompositeComponent) ||(father instanceof TMLCPrimitiveComponent))) {
			// Too large to fit in the father? -> resize it!
			resizeToFatherSize();
			
            setCdRectangle(0, father.getWidth() - getWidth(), 0, father.getHeight() - getHeight());
            setMoveCd(x, y);
        }
    }
	
	public List<TMLCPrimitiveComponent> getAllPrimitiveComponents() {
		List<TMLCPrimitiveComponent> ll = new ArrayList<TMLCPrimitiveComponent>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				ll.addAll(((TMLCCompositeComponent)tgcomponent[i]).getAllPrimitiveComponents());
			}
			if (tgcomponent[i] instanceof TMLCRemoteCompositeComponent) {
				ll.addAll(((TMLCRemoteCompositeComponent)tgcomponent[i]).getAllPrimitiveComponents());
			}
			
			if (tgcomponent[i] instanceof TMLCPrimitiveComponent) {
				ll.add(((TMLCPrimitiveComponent)(tgcomponent[i])));
			}
		}
		
		return ll;
	}
	
	public List<TMLCRecordComponent> getAllRecordComponents() {
		List<TMLCRecordComponent> ll = new ArrayList<TMLCRecordComponent>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				ll.addAll(((TMLCCompositeComponent)tgcomponent[i]).getAllRecordComponents());
			}
			if (tgcomponent[i] instanceof TMLCRemoteCompositeComponent) {
				ll.addAll(((TMLCRemoteCompositeComponent)tgcomponent[i]).getAllRecordComponents());
			}
			
			if (tgcomponent[i] instanceof TMLCRecordComponent) {
				ll.add(((TMLCRecordComponent)(tgcomponent[i])));
			}
		}
		
		return ll;
	}
	
	public void getAllCompositeComponents( List<String> list, String _name) {
		String s;
		TMLCCompositeComponent tmlcc;
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				tmlcc = (TMLCCompositeComponent)tgcomponent[i];
				s = _name + "::" + tmlcc.getValue();
				list.add(s);
				tmlcc.getAllCompositeComponents(list, _name);
			}
		}
	}
	
	public List<TMLCCompositePort> getAllInternalCompositePorts() {
		List<TMLCCompositePort> list = new ArrayList<TMLCCompositePort>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				list.addAll(((TMLCCompositeComponent)tgcomponent[i]).getAllInternalCompositePorts());
			}
			if (tgcomponent[i] instanceof TMLCRemoteCompositeComponent) {
				list.addAll(((TMLCRemoteCompositeComponent)tgcomponent[i]).getAllInternalCompositePorts());
			}
			if (tgcomponent[i] instanceof TMLCCompositePort) {
				list.add((TMLCCompositePort)(tgcomponent[i]));
			}
		}
		
		return list;
	}
	
	public List<TMLCCompositePort> getAllReferencedCompositePorts() {
		List<TMLCCompositePort> list = new ArrayList<TMLCCompositePort>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				list.addAll(((TMLCCompositeComponent)tgcomponent[i]).getAllReferencedCompositePorts());
			}
			if (tgcomponent[i] instanceof TMLCRemoteCompositeComponent) {
				list.addAll(((TMLCRemoteCompositeComponent)tgcomponent[i]).getAllInternalCompositePorts());
			}
		}
		
		return list;
	}
	
	public List<TMLCCompositePort> getFirstLevelCompositePorts() {
		List<TMLCCompositePort> list = new ArrayList<TMLCCompositePort>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCCompositePort) {
				list.add((TMLCCompositePort)(tgcomponent[i]));
			}
		}
		
		return list;
	}
	
	public List<TMLCPrimitivePort> getAllInternalPrimitivePorts() {
		List<TMLCPrimitivePort> list = new ArrayList<TMLCPrimitivePort>();
		for(int i=0; i<nbInternalTGComponent; i++) {
			
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				list.addAll(((TMLCCompositeComponent)tgcomponent[i]).getAllInternalPrimitivePorts());
			}
			
			if (tgcomponent[i] instanceof TMLCPrimitiveComponent) {
				list.addAll(((TMLCPrimitiveComponent)tgcomponent[i]).getAllInternalPrimitivePorts());
			}
		}
		
		return list;
	}
	
	public TMLCPrimitiveComponent getPrimitiveComponentByName(String _name) {
		TMLCPrimitiveComponent tgc;
		ListIterator<TMLCPrimitiveComponent> li = getAllPrimitiveComponents().listIterator();
		
		while(li.hasNext()) {
			tgc = li.next();
			
			if (tgc.getValue().equals(_name)) {
				return tgc;
			}
		}
		
		return null;
	}
	
	public TMLCCompositeComponent getCompositeComponentByName(String _name) {
		TGComponent tgc;
		TMLCCompositeComponent tmp;
        
       for(int i=0; i<nbInternalTGComponent; i++) {
            tgc = tgcomponent[i];
            if (tgc instanceof TMLCCompositeComponent) {
				tmp = (TMLCCompositeComponent)tgc;
				if (tmp.getValue().equals(_name)) {
					return tmp;
				}
				
				if ((tmp = tmp.getCompositeComponentByName(name)) != null) {
					return tmp;
				}
            }
        }
		
		return null;
	}
	
    @Override
	public void setInternalsHidden(boolean hide) {
		hiddeni = hide;
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (!(tgcomponent[i] instanceof TMLCCompositePort)) {
				tgcomponent[i].setHidden(hide);
			}
		}
		
		if (tdp instanceof TMLComponentTaskDiagramPanel) {
			((TMLComponentTaskDiagramPanel)tdp).hideConnectors();
		}
	}
	
    @Override
	public boolean areInternalsHidden() {
		return hiddeni;
	}
//	
//	private void drawInternalComponentsWhenHidden(Graphics g) {
//		//
//		for(int i=0; i<nbInternalTGComponent; i++) {
//			if (tgcomponent[i] instanceof TMLCCompositePort) {
//				//ColorManager.setColor(g, tgcomponent[i].getState(), 0);
//				tgcomponent[i].draw(g);
//			}
//		}
//	}
    
    @Override
    protected String translateExtraParam() {
        StringBuffer sb = new StringBuffer("<extraparam>\n");
        sb.append("<info hiddeni=\"" + hiddeni + "\" "); 
        sb.append("/>\n");
        sb.append("</extraparam>\n");
        return new String(sb);
    }
    
    @Override
    public void loadExtraParam(NodeList nl, int decX, int decY, int decId) throws MalformedModelingException{
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
      //     int t1id;
            
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                //
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            if (elt.getTagName().equals("info")) {
								if (elt.getAttribute("hiddeni").equals("true")) {
									setInternalsHidden(true);
								}
							}
							
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            throw new MalformedModelingException( e );
        }
    }
	
    @Override
	public void drawTGConnectingPoint(Graphics g, int type) {
        //
        for (int i=0; i<nbConnectingPoint; i++) {
            if (connectingPoint[i].isCompatibleWith(type)) {
                connectingPoint[i].draw(g);
            }
        }
		
        for(int i=0; i<nbInternalTGComponent; i++) {
			if (hiddeni) {
				if (tgcomponent[i] instanceof TMLCCompositePort) {
					tgcomponent[i].drawTGConnectingPoint(g, type);
				}
			} else {
				tgcomponent[i].drawTGConnectingPoint(g, type);
			}
            
        }
    }
	
	public String getExtendedValue() {
		return getValuePanel() + "::" + getValue();
	}
	
	public void myActionWhenRemoved() {
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				//ColorManager.setColor(g, tgcomponent[i].getState(), 0);
				tgcomponent[i].myActionWhenRemoved();
			}
		}
		tdp = null;
	}
	
	public void updateReferenceToTMLCCompositeComponent(TMLCCompositeComponent tmlcc) {
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCRemoteCompositeComponent) {
				//ColorManager.setColor(g, tgcomponent[i].getState(), 0);
				((TMLCRemoteCompositeComponent)tgcomponent[i]).updateReference(tmlcc);
			}
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				((TMLCCompositeComponent)tgcomponent[i]).updateReferenceToTMLCCompositeComponent(tmlcc);
			}
		}
	}
	
	public void delayedLoad() {
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCRemoteCompositeComponent) {
				//ColorManager.setColor(g, tgcomponent[i].getState(), 0);
				try {
					((TMLCRemoteCompositeComponent)tgcomponent[i]).delayedLoad();
				} catch (Exception e) {
				}
			}
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				((TMLCCompositeComponent)tgcomponent[i]).delayedLoad();
			}
		}
	}
	
	public int getCompositePortNb() {
		return compositePortNb;
	}
	
	public void portRemoved() {
		compositePortNb --;
	}
	
	public boolean hasRefencesTo(TMLCCompositeComponent tmlcc) {
		boolean b;
		
		for(int i=0; i<nbInternalTGComponent; i++) {
			if (tgcomponent[i] instanceof TMLCRemoteCompositeComponent) {
				b = ((TMLCRemoteCompositeComponent)tgcomponent[i]).getReference() == tmlcc;
				if (b) {
					return true;
				}
			}
			if (tgcomponent[i] instanceof TMLCCompositeComponent) {
				b = ((TMLCCompositeComponent)tgcomponent[i]).hasRefencesTo(tmlcc);
				if (b) {
					return true;
				}
			}
		}
		
		return false;
	}
}
