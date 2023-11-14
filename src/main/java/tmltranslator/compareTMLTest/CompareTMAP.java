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


package tmltranslator.compareTMLTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class CompareTMAP
 * 
 * Creation: 13/11/2023
 *
 * @author Jawher JERRAY
 * @version 1.0 13/11/2023
 */

public class CompareTMAP {

    public CompareTMAP () {

    }

    public boolean CompareTMAPFiles(File expected, File clone) throws Exception {

        BufferedReader expectedReader = new BufferedReader(new FileReader(expected));
        BufferedReader cloneReader = new BufferedReader(new FileReader(clone));

        String s1;
        String s2;
        List<String> expectedStringArray = new ArrayList<String>();
        List<String> cloneStringArray = new ArrayList<String>();
        
        while ((s1 = expectedReader.readLine()) != null) {
            if (s1.indexOf("//") >= 0) {
                s1 = s1.substring(0, s1.indexOf("//"));
            }
            if (!s1.contains("#include") && s1.length() > 0) {
                s1 = s1.trim();
                expectedStringArray.add(s1);
            }
        }

        while ((s2 = cloneReader.readLine()) != null){
            if (s2.indexOf("//") >= 0) {
                s2 = s2.substring(0, s2.indexOf("//"));
            }
            if (!s2.contains("#include") && s2.length() > 0) {
                s2 = s2.trim();
                cloneStringArray.add(s2);
            }
        }
        expectedReader.close();
        cloneReader.close();
        
        return checkEquality(expectedStringArray, cloneStringArray);
    }

    public boolean checkEquality(List<String> s1, List<String> s2) {
        for (String s : s1) {
            if (s2.contains(s)) {
                s2.remove(s);
            }
        }

        if (s2.size() == 0) {
            return true;
        }

        return false;
    }
}
