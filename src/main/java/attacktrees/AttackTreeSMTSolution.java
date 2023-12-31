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


package attacktrees;


import java.util.ArrayList;

/**
 * Class AttackTreeSMTSolution
 * Creation: 04/06/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.0 04/06/2021
 */
public class AttackTreeSMTSolution {

    public final static int NO_SOLUTION = 0;
    public final static int SATISFY_SOLUTION = 1;
    public final static int OPTIMAL_SOLUTION = 2;

    public final static String[] STATUS = {"No solution", "Satisfiable solution found", "Optimal solution found"};

    public int status;
    public int endTime;

    public AttackTree at;
    public Attacker attacker;

    public int indexRoot;

    public int[] startA;
    public int[] endA;


    public AttackTreeSMTSolution() {

    }

    public String getAttackStartEndTime() {
        if ((startA == null) || (at == null)) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        ArrayList<Attack> attacks = at.getAttacks();

        int cpt = 0;
        for (Attack att : attacks) {
            if (endA[cpt] <= endA[indexRoot]) {
                sb.append(cpt + ".");
                if (att.isRoot()) {
                    sb.append(" * ");
                } else {
                    sb.append("   ");
                }
                sb.append(att.getName() + ", ");
                sb.append(startA[cpt] + ", ");
                sb.append(endA[cpt]);
                sb.append("\n");
            }
            cpt++;
        }

        return sb.toString();
    }


    public String getAttackScenario() {


        return "";
    }


}
