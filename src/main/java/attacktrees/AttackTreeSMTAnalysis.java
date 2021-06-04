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

import java.util.*;

import com.microsoft.z3.*;
import myutil.TraceManager;

/**
 * Class AttackTreeSMTAnalysis
 * Creation: 04/06/2021
 *
 * @author Ludovic APVRILLE
 * @version 1.0 04/06/2021
 */
public  class AttackTreeSMTAnalysis  {

    private AttackTree at;

    public AttackTreeSMTAnalysis(AttackTree _at) {
       at = _at;
    }


    public AttackTreeSMTSolution computeSolution(Attacker attacker) {
        AttackTreeSMTSolution atsmts = new AttackTreeSMTSolution();
        atsmts.at = at;
        atsmts.attacker = attacker;
        atsmts.status = AttackTreeSMTSolution.NO_SOLUTION;

        if (attacker == null) {
            return atsmts;
        }

        int nbOfAttacks = at.getAttacks().size();
        Attack[] attacks = at.getAttacks().toArray(new Attack[nbOfAttacks]);

        if (nbOfAttacks == 0) {
            return atsmts;
        }

        // Index of root attack
        int indexRoot = at.getIndexRootAttack();
        if (indexRoot == -1) {
            return atsmts;
        }
        atsmts.indexRoot = indexRoot;


        // Z3 init
        Context ctx = initZ3();

        // Main constraints
        BoolExpr tree_constraints = ctx.mkTrue();

        // Resources
        IntExpr[] res = new IntExpr[nbOfAttacks];
        for (int i = 0; i < res.length; i++) {
            res[i] = ctx.mkInt(attacks[i].getAttackCost());
        }

        // Expertises
        IntExpr[] exp = new IntExpr[nbOfAttacks];
        for (int i = 0; i < exp.length; i++) {
            exp[i] = ctx.mkInt(attacks[i].getAttackExperience());
        }

        // Start and end of attacks
        IntExpr[] startA = new IntExpr[nbOfAttacks];
        BoolExpr[] c_bound_start = new BoolExpr[nbOfAttacks];
        BoolExpr[] c_exp_start = new BoolExpr[nbOfAttacks];
        IntExpr[] endA = new IntExpr[nbOfAttacks];
        BoolExpr[] c_bound_end = new BoolExpr[nbOfAttacks];


        // Start >= 0
        for (int i = 0; i < startA.length; i++) {
            startA[i] = ctx.mkIntConst("start_" + i);
            c_bound_start[i] = ctx.mkGe(startA[i], ctx.mkInt(0));
            tree_constraints = ctx.mkAnd(tree_constraints, c_bound_start[i]);
        }


        // end = start + R
        for (int i = 0; i < startA.length; i++) {
            endA[i] = ctx.mkIntConst("end_" + i);
            c_bound_end[i] = ctx.mkEq(endA[i], ctx.mkAdd(startA[i], res[i]));
            tree_constraints = ctx.mkAnd(tree_constraints, c_bound_end[i]);
        }

        // All attacks with expertise too high have their start after
        // the root attack
        for (int i = 0; i < startA.length; i++) {
            if (attacks[i].getAttackExperience() > attacker.expertise) {
                c_exp_start[i] = ctx.mkGt(startA[i], endA[indexRoot]);
                tree_constraints = ctx.mkAnd(tree_constraints,  c_exp_start[i]);
            }
        }

        // At most one attack at a time
        BoolExpr[][] c_const_oneatatime = new BoolExpr[nbOfAttacks][nbOfAttacks];
        for (int i = 0; i < startA.length; i++) {
            for (int j = 0; j < startA.length; j++) {
                if (i != j) {
                    c_const_oneatatime[i][j] = ctx.mkOr(
                            ctx.mkGe(startA[i], endA[j]),
                            ctx.mkGe(startA[j], endA[i])
                    );
                    tree_constraints = ctx.mkAnd(tree_constraints, c_const_oneatatime[i][j]);
                }
            }
        }

        // Structure of the tree
        ArrayList<AttackNode> nodes = at.getAttackNodes();
        BoolExpr[] c_const = new BoolExpr[nodes.size()];

        int index_c_const = 0;
        for(AttackNode node: nodes) {
            // Sanitizing
            Attack resultingAttack = node.getResultingAttack();
            ArrayList<Attack> inputAttacks = node.getInputAttacks();
            if ((resultingAttack != null) && (inputAttacks != null) && (inputAttacks.size() > 0)) {

                int indexResulting = at.getAttacks().indexOf(resultingAttack);
                if (indexResulting > -1) {
                    TraceManager.addDev("Found resulting attack: " + resultingAttack.getName());

                    // Getting indexes of input attacks
                    ArrayList<Integer> indexes = new ArrayList<>();
                    for (Attack attack: inputAttacks) {
                        int index = at.getAttacks().indexOf(attack);
                        if (index > -1) {
                            indexes.add(index);
                        }
                    }

                    TraceManager.addDev("Nb Of indexes:" + indexes.size());

                    // If at least one input attack, we can start working on the node semantics
                    if (indexes.size() > 0) {
                        //Make the list
                        BoolExpr [] e = new BoolExpr[indexes.size()];
                        for(int i=0; i<indexes.size(); i++) {
                            e[i] = ctx.mkGe( startA[indexResulting], endA[indexes.get(i)] );
                        }
                        if (node instanceof ORNode) {
                            TraceManager.addDev("OR");
                            c_const[index_c_const] = ctx.mkOr( e );
                        } else {
                            TraceManager.addDev("AND");
                            c_const[index_c_const] = ctx.mkAnd( e );
                        }
                        // Finally adding the constraint on the node
                        tree_constraints = ctx.mkAnd(tree_constraints, c_const[index_c_const]);
                        index_c_const ++;
                    }
                }
            }
        }


        // We must ensure that the end of a0 is before or equal to the resources of an atacker
        BoolExpr maxResources = ctx.mkLe(endA[indexRoot], ctx.mkInt(attacker.money));
        tree_constraints = ctx.mkAnd(tree_constraints, maxResources);

        // Solve
        Solver s = ctx.mkSolver();
        s.add(tree_constraints);

        if (s.check() == Status.SATISFIABLE) {
            atsmts.status = AttackTreeSMTSolution.SATISFY_SOLUTION;
            Model model = s.getModel();
            atsmts.endTime = Integer.decode( model.evaluate(endA[indexRoot], false).toString()   ) ;

            // start Times
            atsmts.startA = new int[nbOfAttacks];
            atsmts.endA = new int[nbOfAttacks];

            for(int i=0; i<nbOfAttacks; i++) {
                atsmts.startA[i] = Integer.decode( model.evaluate(startA[i], false).toString()   ) ;
                atsmts.endA[i] = Integer.decode( model.evaluate(endA[i], false).toString()   ) ;
            }

            // end Times


            // Now, find an optimum for the end of the root attack
            Optimize opt = ctx.mkOptimize();
            opt.Add(tree_constraints);

            ArithExpr end0 = ctx.mkSub(endA[indexRoot], ctx.mkInt(0));
            Optimize.Handle objective_R = opt.MkMinimize(end0);

            if (opt.Check() == Status.SATISFIABLE) {
                atsmts.status = AttackTreeSMTSolution.OPTIMAL_SOLUTION;
                model = opt.getModel();
                atsmts.endTime = Integer.decode( model.evaluate(endA[indexRoot], false).toString()   ) ;
            }
        }

        // Z3 has finished!
        closeZ3(ctx);

        return atsmts;
    }

    private Context initZ3() {
        com.microsoft.z3.Global.ToggleWarningMessages(true);
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        return new Context(cfg);
    }

    private void closeZ3(Context ctx) {
        ctx.close();
    }


}
