/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * Daniela Genius, Lip6, UMR 7606 
 * 
 * ludovic.apvrille AT enst.fr
 * daniela.genius@lip6.fr
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

package syscamstranslator.toSysCAMSCluster;

import java.util.LinkedList;

import syscamstranslator.*;

/**
 * Class HeaderCluster
 * HeaderCluster of files .h and .cpp
 * Creation: 14/05/2018
 * @version 1.0 14/05/2018
 * @author Irina Kit Yan LEE
 * @version 1.1 30/07/2018
 * @author Rodrigo CORTES PORTO
*/

public class HeaderCluster {
	static private String headerPrimitiveTDF;
	static private String headerPrimitiveDE;
	static private String headerCluster;
	
	private final static String CR = "\n";
	private final static String CR2 = "\n\n";

	HeaderCluster() {}

	public static String getPrimitiveHeaderTDF(SysCAMSTBlockTDF tdf) {
		if (tdf != null) {
			headerPrimitiveTDF = "#ifndef " + tdf.getName().toUpperCase() + "_TDF_H"+ CR 
					+ "#define " + tdf.getName().toUpperCase() + "_TDF_H" + CR2
					+ "#include <cmath>" + CR + "#include <iostream>" + CR + "#include <systemc-ams>" + CR2;
		} else {
			headerPrimitiveTDF = "";
		}
		return headerPrimitiveTDF;
	}
	
	public static String getPrimitiveHeaderDE(SysCAMSTBlockDE de) {
		if (de != null) {
			headerPrimitiveDE = "#ifndef " + de.getName().toUpperCase() + "_TDF_H"+ CR 
					+ "#define " + de.getName().toUpperCase() + "_TDF_H" + CR2
					+ "#include <cmath>" + CR + "#include <iostream>" + CR + "#include <systemc>" + CR2;
		} else {
			headerPrimitiveDE = "";
		}
		return headerPrimitiveDE;
	}
	
	public static String getClusterHeader(SysCAMSTCluster cluster) {
		 if (cluster != null) {
			 LinkedList<SysCAMSTBlockTDF> tdf = cluster.getBlockTDF();
             LinkedList<SysCAMSTBlockDE> de = cluster.getBlockDE();

             headerCluster = "#ifndef " + cluster.getClusterName().toUpperCase() + "_TDF_H"+ CR 
                    + "#define " + cluster.getClusterName().toUpperCase() + "_TDF_H" + CR2;
             headerCluster += "#include <systemc-ams>" + CR;
             
             for (SysCAMSTBlockTDF b : tdf) {
                 headerCluster = headerCluster + "#include \"" + b.getName() + "_tdf.h\"" + CR;
             }
             for (SysCAMSTBlockDE b : de) {
                 headerCluster = headerCluster + "#include \"" + b.getName() + "_tdf.h\"" + CR;
             }
             headerCluster = headerCluster + CR;
         } else {
             headerCluster = "";
         }
         return headerCluster;
	} 
}
