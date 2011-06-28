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
* Class DSEScriptReader
* Reader of script for Design Space Exploration
* Creation: 24/06/2011
* @version 1.0 24/06/2011
* @author Ludovic APVRILLE
* @see
*/

package dseengine;

import java.io.*;
import java.util.*;

//import tmltranslator.*;
//import tmltranslator.touppaal.*;
//import tmltranslator.tomappingsystemc.*;
import tmltranslator.tomappingsystemc2.*;
//import tmltranslator.toturtle.*;

import translator.*;

import dseengine.*;

import myutil.*;


//import uppaaldesc.*;

public class DSEScriptReader  {
	public static final int SYNTAX_ERROR_IN_LINE = 3;
	public static final int OK = 1;
	public static final int FILE_ERROR = 2;
	public static final int KO = 4;
	
	private String fileName;
	private int lineOfError;

	public  DSEScriptReader(String _fileName) {
		fileName = _fileName;
	}
	
	
	
	// Return an eventual error
	// OK: all ok
	// FILE_ERROR: could not read file
	// KO:
	
	public int execute() {
		String scriptToExecute = "";
		try {
			scriptToExecute = FileUtils.loadFile(fileName); 
		} catch (FileException e) {
			return FILE_ERROR;
		}
		
		
		// Read the script line by line, and execute corresponding actions
		StringReader sr = new StringReader(scriptToExecute);
        BufferedReader br = new BufferedReader(sr);
        String s;
		DSEConfiguration config = new DSEConfiguration();
		int line = 0;
		int ret;
		
		try {
            while((s = br.readLine()) != null) {
				line ++;
                s = s.trim();
				if (s.startsWith("#")) {
					// Comment 
				} else {
					if (s.length() > 0) {
						ret = executeLineOfScript(s, config);
						if (ret == SYNTAX_ERROR_IN_LINE) {
							lineOfError = line;
							return SYNTAX_ERROR_IN_LINE;
						}
					}
				}
            }
		} catch (Exception e) {
			return KO;
		}
			return OK;
	}
	
	private int executeLineOfScript(String _line, DSEConfiguration _config) {
		return OK;
	}
	
	
	public int getLineOfError() {
		return lineOfError;
	}
	
} // Class DSEScriptReader

