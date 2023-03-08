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

package myutil.intboolsolver;

/**
 * class IBSAttribute (interface), describing the "static" generic part
 * shared by all open leaves of {@link myutil.intboolsolver.IBSolver
 * IBSolver}.
 * Creation: 07/03/2023
 *
 *  <p> This interface describes the features required from the
 *  class of {@link myutil.intboolsolver.IBSolver IBSolver}
 *  attributes (methods that do not depend on attribute instances).
 *  Its implementations are intended to instantiate
 *  the {@code AtC} parameter of the generic
 *  {@link myutil.intboolsolver.IBSolver IBSolver} </p>
 *
 * @version 0.1 07/03/2023
 * @author Sophie Coudert  (rewrite from Alessandro TEMPIA CALVINO)
 */

public class IBSAttributeClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState,
        Att extends IBSAttribute<Spec,Comp,State,SpecState,CompState>
        > {
     public IBSTypedAttribute getTypedAttribute(Spec _spec, String _s) {
         return IBSTypedAttribute.NullAttribute;
     }
     public IBSTypedAttribute getTypedAttribute(Comp _comp, String _s){
         return IBSTypedAttribute.NullAttribute;
     }
     public IBSTypedAttribute getTypedAttribute(Comp _comp, State _st){
         return IBSTypedAttribute.NullAttribute;
     }
     public void initBuild(Spec _spec){}
     public void initBuild(Comp _comp){}
     public void initBuild(){}
}
