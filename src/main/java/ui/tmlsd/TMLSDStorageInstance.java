/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

package ui.tmlsd;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;
import ui.window.JDialogTMLCPStorageInstance;

import javax.swing.*;

/**
 * Class TMLSDStorageInstance
 * Instance of storage CP component for a TML Sequence Diagram
 * Creation: 17/02/2004
 * @version 1.1 10/06/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public class TMLSDStorageInstance extends TMLSDInstance implements SwallowTGComponent {
	
    public TMLSDStorageInstance( int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
																	TGComponent _father, TDiagramPanel _tdp )  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
        
        initScaling( 10, 500 );
//        width = 10;
//        height = 500;
        minWidth = scale( 10 );
        maxWidth = scale( 10 );
        minHeight = scale( 250 );
        maxHeight = scale( 1500 );
        
        // Issue #31 Already called in superclass
      //  makeTGConnectingPoints();

        nbInternalTGComponent = 0;
        
        moveable = true;
        editable = true;
        removable = true;
        userResizable = true;
        
        value = "Storage instance name";
        name = "StorageInstance";
		isActor = false;
        
        myImageIcon = IconManager.imgic500;
	}
    
    @Override
	public boolean editOnDoubleClick(JFrame frame) {
		JDialogTMLCPStorageInstance jdab = new JDialogTMLCPStorageInstance( myAttributes, 
																			null,
																			frame,
																			"Setting properties of " + name, 
																			"Attribute", 
																			name );
		setJDialogOptions(jdab);
		GraphicLib.centerOnParent( jdab, 650, 500 );
		jdab.setVisible(true); // blocked until dialog has been closed
		name = jdab.getName();																											

		return true;
	}
	
	protected void setJDialogOptions( JDialogTMLCPStorageInstance jda ) {
		jda.addAccess(TAttribute.getStringAccess(TAttribute.PUBLIC));
		jda.addAccess(TAttribute.getStringAccess(TAttribute.PRIVATE));
		jda.addType(TAttribute.getStringType(TAttribute.NATURAL), true);
		jda.addType(TAttribute.getStringType(TAttribute.BOOLEAN), true);
		jda.addType(TAttribute.getStringType(TAttribute.ADDRESS), true);
		
		jda.enableInitialValue(true);
		jda.enableRTLOTOSKeyword(true);
		jda.enableJavaKeyword(false);
	}

	@Override
	public int getType() {
		return TGComponentManager.TMLSD_STORAGE_INSTANCE;
	}

	public String getInstanceType()	{
		return "STORAGE";
	}
}	//End of class
