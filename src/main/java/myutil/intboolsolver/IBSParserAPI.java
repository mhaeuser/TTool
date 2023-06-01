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
 * <p>This class presents the features exported by the parsers of the
 * IntBoolSolver System.
 * Creation: 11/04/2023.</p>
 *
 * <p> As explained in {@link myutil.intboolsolver package documentation},
 * the parser is parametrized by application classes <code>Spec</code>,
 * <code>Comp</code>, <code>State</code>, <code>SpecState</code> and
 * <code>CompState</code>. </p>
 * Note that the parsers alsorely on an attribute class and an expression
 * class (based on the same generic parameters). Thus, they should provide
 * at least one constructors like this (where {@code IBSParser} implements
 * {@code IBSParserAPI}):
 * <PRE>
 *     public IBSParser(
 *          IBSAttributes&lt;Spec,Comp,State,SpecState,CompState&gt; _c,
 *          IBSExpressions&lt;Spec,Comp,State,SpecState,CompState&gt; _e);
 * </PRE>
 *
 * <p>The parser offers several parsing methods depending on the structure
 * in which variables are interpreted.</p>
 *
 * <p> While parsing, bad identifiers are collected, i.e. identifiers
 * for which no valid interpretation has been found. Finding such
 * identifiers in an expression is an error case. Other error cases are
 * usual syntax errors and type errors. As of now, returned error
 * information is not precise.
 * </p>
 *
 * <p> Parsing is provided for integer expressions, boolean expressions,
 * and guards. The only difference between guards and boolean expressions
 * is that guards can be empty strings (or string containing only
 * spaces/separators), which are evaluated as {@code true}. </p>
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
    public void setAttributes(IBSAttributes<Spec,Comp,State,SpecState,CompState> _c);
    /**
     * Get the parser's attribute class.
     * @return the parser's attribute class.
     */
    public IBSAttributes<Spec,Comp,State,SpecState,CompState> getAttributes();
    /**
     * Set the parser's expression class.
     * @param _e the expression class to set.
     */
    public void setExpressions(IBSExpressions<Spec,Comp,State,SpecState,CompState> _e);
    /**
     * Get the parser's expression class.
     * @return the parser's expression class.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState> getExpressions();

    /**
     * Get the bad identifiers found while parsing
     * @return the set of found bad identifiers;
     */
    public HashSet<String> getBadIdents();

    /**
     * Empty the parser's set of found bad identifiers.
     * (this is not done automatically to allow some flexibility)
     */
    public void clearBadIdents();

    /** Parse an integer expression
     * @param _spec the specification in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Spec _spec, String _s);
    /** Parse a boolean expression
     * @param _spec the specification in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Spec _spec, String _s);
    /** Parse a guard
     * @param _spec the specification in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.BExpr parseGuard(Spec _spec, String _s);
    /** Parse an integer expression
     * @param _comp the component in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(Comp _comp, String _s);
    /** Parse a boolean expression
     * @param _comp the component in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(Comp _comp, String _s);
    /** Parse a guard
     * @param _comp the component in which variables are interpreted
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.BExpr parseGuard(Comp _comp, String _s);
    /** Parse a closed integer expression
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.IExpr parseInt(String _s);
    /** Parse a closed boolean expression
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.BExpr parseBool(String _s);
    /** Parse a closed guard
     * @param _s the string to parse
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.BExpr parseGuard(String _s);
    /** Builds an integer expression from an attribute
     * @param _attr the source attribute
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.IExpr
    makeInt(IBSAttributes<Spec,Comp,State,SpecState,CompState>.Attribute _attr);
    /** Builds a boolean expression from an attribute
     * @param _attr the source attribute
     * @return an expression if no error has been detected. Otherwise, null.
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState>.BExpr
    makeBool(IBSAttributes<Spec,Comp,State,SpecState,CompState>.Attribute _attr);
}
