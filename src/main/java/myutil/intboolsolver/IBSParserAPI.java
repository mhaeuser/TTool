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

import java.util.HashSet;

/**
 * Class IBSParser presents the functions exported by the parser.
 *
 * Creation: 23/03/2023
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 23/03/2023
 */
public interface IBSParserAPI<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
    //public IBSParserAPI(IBSAttributeClass<Spec,Comp,State,SpecState,CompState> _c,
                        //IBSExpressionClass<Spec,Comp,State,SpecState,CompState> _e){}
    //public IBSParserAPI(){}
    public void setAttributeClass(IBSAttributeClass<Spec,Comp,State,SpecState,CompState> _c);
    public IBSAttributeClass<Spec,Comp,State,SpecState,CompState> getAttributeClass();
    public void setExpressionClass(IBSExpressionClass<Spec,Comp,State,SpecState,CompState> _c);
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState> getExpressionClass();
    public HashSet<String> getBadIdents();
    public void clearBadIdents();
    public boolean syntaxError();

    /**
     * return -2: type error...
     * @param _comp
     * @param _s
     * @return
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Spec _comp, String _s);
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Spec _comp, String _s);
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Comp _comp, String _s);
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Comp _comp, String _s);
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(String _s);
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(String _s);
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr
    makeInt(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _attr);
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr
    makeBool(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _attr);
}
