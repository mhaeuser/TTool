/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class AvatarBDPanel
 * Panel for drawing AVATAR blocks
 * Creation: 06/04/2010
 * @version 1.0 06/04/2010
 * @author Ludovic APVRILLE
 * @see
 */

package ui.avatarbd;


import org.w3c.dom.*;

import ui.*;
import ui.tmldd.*;
import java.util.*;

import myutil.*;

public class AvatarBDPanel extends TDiagramPanel {
	private Vector validated, ignored;
	private String val = null, ign = null;
	private boolean optimized = true;
	
	private static final String DEFAULT_MAIN = "void __user_init() {\n}\n\n";
	
	private String mainCode;
	
    
    public  AvatarBDPanel(MainGUI mgui, TToolBar _ttb) {
        super(mgui, _ttb);
        mainCode = DEFAULT_MAIN;
        /*TDiagramMouseManager tdmm = new TDiagramMouseManager(this);
        addMouseListener(tdmm);
        addMouseMotionListener(tdmm);*/
    }
    

    public boolean actionOnDoubleClick(TGComponent tgc) {
        //System.out.println("Action");
        if (tgc instanceof AvatarBDBlock) {
            AvatarBDBlock b = (AvatarBDBlock)tgc;
			//System.out.println("oldValue:" + b.oldValue);
            return mgui.newAvatarBDBlockName(tp, b.oldValue, b.getValue());
        } else if (tgc instanceof AvatarBDDataType) {
            return true;
		}
            //return false; // because no change made on any diagram
        //}
        return false;
    }
    
    public boolean actionOnAdd(TGComponent tgc) {
        //System.out.println("Action on add!");
        if (tgc instanceof AvatarBDBlock) {
            AvatarBDBlock abdb = (AvatarBDBlock)(tgc);
            //TraceManager.addDev(" *** add Avatar block *** name=" + abdb.getBlockName());
            mgui.addAvatarBlock(tp, abdb.getBlockName());
            return true;
        } 
        return false;
    }
    
    public boolean actionOnRemove(TGComponent tgc) {
		//System.out.println("Action on remove!");
        if (tgc instanceof AvatarBDBlock) {
            AvatarBDBlock abdb = (AvatarBDBlock)(tgc);
            //System.out.println(" *** add tclass *** name=" + tmlt.getTaskName());
            mgui.removeAvatarBlock(tp, abdb.getBlockName());
			LinkedList<AvatarBDBlock> list  = abdb.getFullBlockList();
			for(AvatarBDBlock b: list) {
				mgui.removeAvatarBlock(tp, b.getBlockName());
			}
            return true;
        }
        return false;
    }
    
    public boolean actionOnValueChanged(TGComponent tgc) {
        if (tgc instanceof AvatarBDBlock) {
			//updateAllSignalsOnConnectors();
            return actionOnDoubleClick(tgc);
        }
        return false;
    }
	
	 public boolean areAttributesVisible() {
        return attributesVisible;
    }
    
    public String getXMLHead() {
        return "<AVATARBlockDiagramPanel name=\"" + name + "\"" + sizeParam()  +" >\n" + displayParam();
    }
    
    public String getXMLTail() {
        return "</AVATARBlockDiagramPanel>";
    }
    
    public String getXMLSelectedHead() {
        return "<AVATARBlockDiagramPanelCopy name=\"" + name + "\" xSel=\"" + xSel + "\" ySel=\"" + ySel + "\" widthSel=\"" + widthSel + "\" heightSel=\"" + heightSel + "\" >";
    }
    
    public String getXMLSelectedTail() {
        return "</AVATARBlockDiagramPanelCopy>";
    }
    
    public String getXMLCloneHead() {
        return "<AVATARBlockDiagramPanelCopy name=\"" + name + "\" xSel=\"" + 0 + "\" ySel=\"" + 0 + "\" widthSel=\"" + 0 + "\" heightSel=\"" + 0 + "\" >";
    }
    
    public String getXMLCloneTail() {
        return "</AVATARBlockDiagramPanelCopy>";
    }
	
	public void setConnectorsToFront() {
		TGComponent tgc;
		
		//System.out.println("list size=" + componentList.size());
		
        Iterator iterator = componentList.listIterator();
        
		ArrayList<TGComponent> list = new ArrayList<TGComponent>();
		
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (!(tgc instanceof TGConnector)) {
				list.add(tgc);
			}
		}
		
		//System.out.println("Putting to back ...");
		for(TGComponent tgc1: list) {
			//System.out.println("Putting to back: " + tgc1);
			componentList.remove(tgc1);
			componentList.add(tgc1);
		}
	}
    

    
    /*public boolean areAttributesVisible() {
        return attributesVisible;
    }
    
    
    public boolean areChannelVisible() {
        return synchroVisible;
    }
    
    public void setAttributesVisible(boolean b) {
        attributesVisible = b;
    }
    
    
    public void setChannelVisible(boolean b) {
        channelVisible = b;
    }*/
    
    public String displayParam() {
        String s = "";
        String [] tmp =  Conversion.wrapText(mainCode);
        String tmps;
        int i;
        
        for(i=0; i<tmp.length; i++) {
        	s += "<MainCode value=\"" + GTURTLEModeling.transformString(tmp[i]) + "\"/>\n";
        }
        
        if (optimized) {
        	s += "<Optimized value=\"true\" />\n";
        } else {
        	s += "<Optimized value=\"false\" />\n";
        }
        
        if (validated == null) {
        	s += "<Validated value=\"\" />\n";
        } else {
        	s+= "<Validated value=\"";
        	for(i=0; i<validated.size();i++) {
        		s += ((AvatarBDBlock)(validated.elementAt(i))).getBlockName() + ";";
        	}
        	s += "\" />\n";
        }
        
        if (ignored == null) {
        	s += "<Ignored value=\"\" />\n";
        } else {
        	s+= "<Ignored value=\"";
        	for(i=0; i<ignored.size();i++) {
        		s += ((AvatarBDBlock)(ignored.elementAt(i))).getBlockName() + ";";
        	}
        	s += "\" />\n";
        }
        
        
        return s;
    }
    
    
  
    
    public void loadExtraParameters(Element elt) {
        String s;
        
        // Main code
        NodeList nl = elt.getElementsByTagName("MainCode");
        //TraceManager.addDev("Extra parameter of block diagram nbOfElements: " + nl.getLength());
        Node n;
        
        try {
        	if (nl.getLength()>0) {
        		mainCode = "";
        	}
        	for(int i=0; i<nl.getLength(); i++) {
        		n = nl.item(i);
        		if (n.getNodeType() == Node.ELEMENT_NODE) {
        			s = ((Element)n).getAttribute("value");
        			TraceManager.addDev("Found value=" + s);
        			if (s != null) {
        				mainCode += s + "\n";
        			}
        		}
        		
        	}
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            TraceManager.addDev("Exception when loading parameter of block diagram:" + e.getMessage());
            
        }
        
        // Optimized
        nl = elt.getElementsByTagName("Optimized");
        try {
        	if (nl.getLength()>0) {
        		n = nl.item(0);
        		if (n.getNodeType() == Node.ELEMENT_NODE) {
        			s = ((Element)n).getAttribute("value");
        			TraceManager.addDev("Found value=" + s);
        			if (s != null) {
        				if (s.compareTo("true") == 0) {
        					optimized = true;
        				} else {
        					optimized = false;
        				}
        			}
        		}
        	}
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            TraceManager.addDev("Exception when loading parameter of block diagram:" + e.getMessage());
            
        }
        
        // Validated
        nl = elt.getElementsByTagName("Validated");
        try {
        	if (nl.getLength()>0) {
        		n = nl.item(0);
        		if (n.getNodeType() == Node.ELEMENT_NODE) {
        			s = ((Element)n).getAttribute("value");
        			TraceManager.addDev("Found value=" + s);
        			if (s != null) {
        				val = s;
        			}
        		}
        	}
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            TraceManager.addDev("Exception when loading parameter of block diagram:" + e.getMessage());
            
        }
        
        // Ignored
        nl = elt.getElementsByTagName("Ignored");
        try {
        	if (nl.getLength()>0) {
        		n = nl.item(0);
        		if (n.getNodeType() == Node.ELEMENT_NODE) {
        			s = ((Element)n).getAttribute("value");
        			TraceManager.addDev("Found value=" + s);
        			if (s != null) {
        				ign = s;
        			}
        		}
        	}
        } catch (Exception e) {
            // Model was saved in an older version of TTool
            TraceManager.addDev("Exception when loading parameter of block diagram:" + e.getMessage());
            
        }
        
        
    }
	
	
	public void updateAllSignalsOnConnectors() {
		TGComponent tgc;
		AvatarBDPortConnector port;
		Iterator iterator = componentList.listIterator();
		while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof AvatarBDPortConnector) {
				port = (AvatarBDPortConnector)tgc;
				port.updateAllSignals();
			}
		}
	}
	
	public Vector getListOfAvailableSignals(AvatarBDBlock _block) {
		int i;
		TGComponent tgc;
		LinkedList<String> ll;
		AvatarBDPortConnector port;
        Iterator iterator = componentList.listIterator();
		ArrayList<String> list = new ArrayList<String>();
		Vector v = new Vector();
		Vector listOfBlock = _block.getSignalList();
		
		if (listOfBlock.size() == 0) {
			return v;
		}
		
		v.addAll(listOfBlock);
        
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
            if (tgc instanceof AvatarBDPortConnector) {
				port = (AvatarBDPortConnector)tgc;
				if (port.getAvatarBDBlock1() == _block) {
					ll = port.getListOfSignalsOrigin();
					removeSignals(v, ll);
				}
				if (port.getAvatarBDBlock2() == _block) {
					ll = port.getListOfSignalsDestination();
					removeSignals(v, ll);
				}
			}
		}
		
		return v;
	}
	
	// Remove AvatarSignals of v which name is provided in list
	private void removeSignals(Vector v, LinkedList<String> list) {
		int i;
		AvatarSignal as;
		for(String s: list) {
			for(i=0; i<v.size(); i++) {
				as = (AvatarSignal)(v.get(i));
				if (as.toString().compareTo(s) == 0) {
					v.removeElementAt(i);
					break;
				}
			}
		}
	}
	
	public LinkedList<AvatarBDBlock> getFullBlockList() {
		TGComponent tgc;
		AvatarBDBlock block;
		LinkedList<AvatarBDBlock> list = new LinkedList<AvatarBDBlock>();
		Iterator iterator = componentList.listIterator();
		
		 while(iterator.hasNext()) {
           tgc = (TGComponent)(iterator.next());
		   if (tgc instanceof AvatarBDBlock) {
			   block = (AvatarBDBlock)tgc;
			   list.add(block);
			   list.addAll(block.getFullBlockList());
		   }
		 }
		 return list;
	}
	
	public TAttribute getAttributeByBlockName(String _blockName, String attributeName) {
		TAttribute a;
		for(AvatarBDBlock block: getFullBlockList()) {
			if (block.getBlockName().compareTo(_blockName) == 0) {
				return block.getAttributeByName(attributeName);
			}
		}
		return null;
	}
	
	public Vector getAllAttributesOfBlock(String _name) {
		LinkedList<AvatarBDBlock> list = getFullBlockList();
		for(AvatarBDBlock block: list) {
			if (block.getBlockName().compareTo(_name) ==0) {
				return block.getAttributeList();
			}
		}
		return null;
	}
	
	public Vector getAllMethodsOfBlock(String _name) {
		LinkedList<AvatarBDBlock> list = getFullBlockList();
		for(AvatarBDBlock block: list) {
			if (block.getBlockName().compareTo(_name) ==0) {
				return block.getAllMethodList();
			}
		}
		return null;
	}
	
	public Vector getAllSignalsOfBlock(String _name) {
		LinkedList<AvatarBDBlock> list = getFullBlockList();
		for(AvatarBDBlock block: list) {
			if (block.getBlockName().compareTo(_name) ==0) {
				return block.getAllSignalList();
			}
		}
		return null;
	}
	
	public Vector getAllTimersOfBlock(String _name) {
		LinkedList<AvatarBDBlock> list = getFullBlockList();
		for(AvatarBDBlock block: list) {
			if (block.getBlockName().compareTo(_name) ==0) {
				return block.getAllTimerList();
			}
		}
		return null;
	}
	
	public Vector getAttributesOfDataType(String _name) {
		TGComponent tgc;
        Iterator iterator = componentList.listIterator();
		AvatarBDDataType adt;
		
        while(iterator.hasNext()) {
            tgc = (TGComponent)(iterator.next());
			if (tgc instanceof AvatarBDDataType) {
				adt = (AvatarBDDataType)tgc;
				if (adt.getDataTypeName().compareTo(_name) == 0) {
					return adt.getAttributeList();
				}
			}
		}
		
		return null;
	}
	
	public TAttribute getAttribute(String _name, String _nameOfBlock) {
		
		//TraceManager.addDev("Searching for attribute: " + _name  + " of block " + _nameOfBlock);
	
		TAttribute ta;
		Vector v = getAllAttributesOfBlock(_nameOfBlock);
		
		for(int i=0; i<v.size(); i++) {
			ta = (TAttribute)(v.get(i));
			if (ta.getId().compareTo(_name) ==0) {
				return ta;
			}
		}

		return null;
	}
	
	    public void setMainCode(String s) {
    	if (s == null) {
    		s = "";
    	}
    	mainCode = s;
    }
    
    public String getMainCode() {
    	return mainCode;
    }
    
    public Vector getValidated() {
    	if ((val != null) && (validated == null)) {
    		makeValidated();
    	}
    	return validated;
    }
    
    public Vector getIgnored() {
    	if ((ign != null) && (ignored == null)) {
    		makeIgnored();
    	}
    	return ignored;
    }
    
    public boolean getOptimized() {
    	return optimized;
    }
    
    
    public void setValidated(Vector _validated) {
    	validated = _validated;
    }
    
    public void setIgnored(Vector _ignored) {
    	ignored = _ignored;
    }
    
    public void setOptimized(boolean _optimized) {
    	optimized = _optimized;
    }
    
    public void makeValidated() {
    	TraceManager.addDev("Making validated with val=" + val);
    	validated = new Vector();
    	LinkedList<AvatarBDBlock> list = getFullBlockList();
    	String tmp;
    	
    	String split[] = val.split(";");
    	for(int i=0; i<split.length; i++) {
    		tmp = split[i].trim();
    		if (tmp.length() > 0) {
    			for (AvatarBDBlock block: list) {
    				if (block.getBlockName().compareTo(tmp) == 0) {
    					validated.add(block);
    					break;
    				}
    			}
    		}
    	}
    	val = null;
    }
    
    public void makeIgnored() {
    	TraceManager.addDev("Making ignored with ign=" + val);
    	ignored = new Vector();
    	LinkedList<AvatarBDBlock> list = getFullBlockList();
    	String tmp;
    	
    	String split[] = ign.split(";");
    	for(int i=0; i<split.length; i++) {
    		tmp = split[i].trim();
    		if (tmp.length() > 0) {
    			for (AvatarBDBlock block: list) {
    				if (block.getBlockName().compareTo(tmp) == 0) {
    					validated.add(block);
    					break;
    				}
    			}
    		}
    	}
    	ign = null;
    }
    
}
