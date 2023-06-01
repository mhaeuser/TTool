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

/** Interface for the lexers that are intended to be used in the IBS
 * JFlex/JavaCUP parsing system.
 *
 * <p> This is used by the provided JavaCUP  parser and may be useless
 * for the final user. The <code> init functions </code> decide the
 * way the variables are handled by the lexer. Thus they should be called
 * just before parsing an expression.
 *
 * @param <Spec> The specifications that provide a meaning to the leaves
 * @param <Comp> The components that provide a meaning to the leaves
 * @param <State> The state machine states of the system
 * @param <SpecState> The specification states, used in evaluation
 * @param <CompState> The component states, used in evaluation
 *
 * @version 1.0 11/04/2023
 * @author Sophie Coudert
 */
public interface IBSScanner< Spec extends IBSParamSpec, Comp extends IBSParamComp, State extends IBSParamState, SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState > extends java_cup.runtime.Scanner {
    /** Set the scanner's attributes class
     * @param _attrC the attribute class
     */
    public void setAttributes(IBSAttributes<Spec,Comp,State,SpecState,CompState> _attrC);
    /** Get the scanner's attribute class
     * @return the scanner's attribute class
     */
    public IBSAttributes<Spec,Comp,State,SpecState,CompState> getAttributes();
    /** Set the scanner's expressions class
     * @param _exprC the expressions class
     */
    public void setExpressions(IBSExpressions<Spec,Comp,State,SpecState,CompState> _exprC);
    /** Get the scanner's expressions class
     * @return the scanner's expressions class
     */
    public IBSExpressions<Spec,Comp,State,SpecState,CompState> getExpressions();

    /** Get the set of bad identifiers found while lexing
     * @return the set of bad identifiers
     */
    public HashSet<String> getBadIdents();
    /** Reset the set of bad identifiers */
    public void clearBadIdents();

    /** To parse closed expressions. All variables are considered as bad
     * identifiers. To be called just before parsing an expression.
     * @param _s the string to parse
     * @throws java.io.IOException if the scanner fails to create a reader from _s
     */
    public void init(String _s) throws java.io.IOException;
    /** To interpret variable w.r.t. a specification. Variables with no
     * valid interpretation (unknown or not well typed) are considered as
     * bad  identifiers. To be called just before parsing an expression.
     * @param _spec the specification in which variables are interpreted
     * @param _s the string to parse
     * @throws java.io.IOException if the scanner fails to create a reader from _s
     */
    public void init(Spec _spec, String _s) throws java.io.IOException;
    /** To interpret variable w.r.t. a component. Variables with no
     * valid interpretation (unknown or not well typed) are considered as
     * bad  identifiers. To be called just before parsing an expression.
     * @param _comp the component in which variables are interpreted
     * @param _s the string to parse
     * @throws java.io.IOException if the scanner fails to create a reader from _s
     */
    public void init(Comp _comp, String _s) throws java.io.IOException;
}
