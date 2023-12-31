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




package jttool;

import java.util.*;


/**
 * Class LibLogicalOperator
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 10/03/2005
 * @version 1.1 10/03/2005
 * @author Ludovic APVRILLE
 */
public class LibLogicalOperator {
    public static Random random = new Random();

    public static int makeChoice(boolean b[]) {
	int i, cpt = 0, r;

	//System.out.println("length of b[]:" + b.length);

	for(i=0; i<b.length; i++) {
	    //System.out.println("Checking b[" + i + "]");
	    if (b[i]) { 
		//System.out.println("cpt ++");
		cpt++;
	    }
	}

	if (cpt == 0) {
	    return -1;
	}

	//System.out.println("cpt = " + cpt);

	r = random.nextInt(cpt);
	cpt = cpt - r;
	//System.out.println("r=" + r);

	i=0;

	while(i<b.length) {
	    if ((cpt == 1) && (b[i])) {
		return i;
	    }
	    if (b[i]) {
		cpt --;
	    }
	    i++;
	}

	return -1;
    }

    public static int nbOfTrue(boolean b[]) {
	int i, cpt = 0;

	for(i=0; i<b.length; i++) {
	    //System.out.println("Checking b[" + i + "]");
	    if (b[i]) { 
		cpt++;
	    }
	}

	return cpt;
    }

}
