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
 * <p>Class IBSParser presents the functions exported by the parser.
 * Creation: 11/04/2023.</p>
 *
 * <p> As explained in {@link myutil.intboolsolver package documentation},
 * the parser is parametrized by application classes <code>Spec</code>,
 * <code>Comp</code>, <code>State</code>, <code>SpecState</code> and
 * <code>CompState</code>. It also depends on an attribute class and
 * an expression class. Thus, it provides two constructors:
 * <PRE>
 *     IBSParserAPI();
 *     public IBSParserAPI(
 *          IBSAttributeClass&lt;Spec,Comp,State,SpecState,CompState&gt; _c,
 *          IBSExpressionClass&lt;Spec,Comp,State,SpecState,CompState&gt; _e);
 * </PRE>
 * <p> When using the default constructor, attribute and expression classes
 * can be set <i>a posteriori</i>.</p>
 *
 * <p>The parser offers several parsing methods depending on the structure
 * in which open leaves are interpreted.</p>
 *
 * <p> While parsing, bad identifiers are collected, i.e. identifiers
 * for which no valid interpretation has been found. Finding such
 * identifiers in an expression is an error case. Other error cases are
 * usual syntax errors and type errors. As several different errors can
 * occur in a single expression, returned error information is not
 * exhaustive.
 * </p>
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 1.0 11/04/2023
 */
public interface IBSParserAPI<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
    /**
     * Set the parser's attribute class.
     * @param _c the attribute class to set.
     */
    public void setAttributeClass(IBSAttributeClass<Spec,Comp,State,SpecState,CompState> _c);
    /**
     * Get the parser's attribute class.
     * @return the parser's attribute class.
     */
    public IBSAttributeClass<Spec,Comp,State,SpecState,CompState> getAttributeClass();
    /**
     * Set the parser's expression class.
     * @param _c the expression class to set.
     */
    public void setExpressionClass(IBSExpressionClass<Spec,Comp,State,SpecState,CompState> _c);
    /**
     * Get the parser's expression class.
     * @return the parser's expression class.
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState> getExpressionClass();

    /**
     * Get the bad identifiers found while parsing
     * @return the set of found bad identifiers;
     */
    public HashSet<String> getBadIdents();

    /**
     * empty the parser's set of found bad identifiers.
     * (this is not done automatically to allow some flexibility)
     */
    public void clearBadIdents();

    /** parse an integer expression
     * @param _spec the specification in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Spec _spec, String _s);
    /** parse a boolean expression
     * @param _spec the specification in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Spec _spec, String _s);
    /** parse an integer expression
     * @param _comp the component in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Comp _comp, String _s);
    /** parse a boolean expression
     * @param _comp the component in which open leaves are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Comp _comp, String _s);
    /** parse a closed integer expression
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(String _s);
    /** parse a closed boolean expression
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(String _s);
    /** builds an integer expression from an attribute
     * @param _attr the source attribute
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.IExpr
    makeInt(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _attr);
    /** builds a boolean expression from an attribute
     * @param _attr the source attribute
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressionClass<Spec,Comp,State,SpecState,CompState>.BExpr
    makeBool(IBSAttributeClass<Spec,Comp,State,SpecState,CompState>.Attribute _attr);
}
