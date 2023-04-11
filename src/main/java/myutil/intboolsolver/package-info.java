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

/**
 * Package intboolsolver, a generic modular parser/evaluator for
 * boolean/integer expressions with open leaves (i.e identifiers).
 * Creation: 11/04/2023.
 *
 * <p> This package provide a way to parse and evaluate boolean/integer
 * expressions. The handling of open leaves is parametrized (thus can be
 * instantiated in different contexts). The package is modular in the
 * sense that expression structure or parsing algorithm can be changed
 * without having to modify other components. </p>
 *
 * <p> The solver may be instantiated by usual expressions with variables
 * and their usual evaluation, but it is more generally dedicated to
 * two-level structured systems, i.e global specifications build over
 * a set of components. For example we can consider identifiers
 * associated to (local) components that define together a (global)
 * specification. It also handles a notion of component/specification
 * state.  Default parameters are provided to instantiate solvers that
 * do not use all the features, such as for example a solver for closed
 * expressions (which is provided).</p>
 * <ul>
 *     <li> <p>{@link myutil.intboolsolver.IBSParserAPI
 *     IBSParserAPI} provides documentation about the functions
 *     exported by the provided parser implementations.</p>
 *     <p> The way leaves are handled depends on the notions of specification,
 *     component, specification state, component state and (state machine)
 *     state of the instance. Thus the corresponding classes are parameters
 *     of the generic parsers.</p>
 *     <p> The handling of leaves relies on an "<b>attribute class</b>" that
 *     must be provided for each instance (partial implementation is
 *     provided).</p>
 *     <p> The parser also relies on an "<b>expression class</b>" which
 *     implements the structure of the expressions build by the parser (full
 *     implementations are provided)</p>
 *        <ul><li> {@link myutil.intboolsolver.origin.IBSOriginParser IBSOriginParser}
 *         is an implementation of {@link myutil.intboolsolver.IBSParserAPI
 *         IBSParserAPI} based on the original implementation of the Avatar
 *         solver (here for historical reasons... another implementation is
 *         planned).
 *        </li>
 *        <li> {@link myutil.intboolsolver.IBSStdParser IBSStdParser},
 *        {@link myutil.intboolsolver.IBSStdLexer IBSStdLexer} and
 *        {@link myutil.intboolsolver.IBSStdSymbols IBSStdSymbol} are classes
 *        generated by a <a href="#javacup">java-cup/jflex parser </a> (sources
 *        in the "javacup" subdirectory).
 *        The additional interface {@link myutil.intboolsolver.IBSScanner
 *        IBSScanner} abstracts the lexer, which can then be a parameter of
 *        the parser.
 *        </li></ul><p></p>
 *     </li>
 *     <li> <p>{@link myutil.intboolsolver.IBSExpressionClass IBSExpressionClass}
 *     describes the interface expected for expression handling. It contains two
 *     kind of methods: methods for building expressions (required by parsing)
 *     and methods for evaluating expressions (useful for final user, but also
 *     available for parser implementations).</p>
 *     <ul><li> <p>{@link myutil.intboolsolver.origin.IBSOriginExpressionClass
 *         IBSOriginExpressionClass} is a full implementation of {@link
 *         myutil.intboolsolver.IBSExpressionClass IBSExpressionClass} based
 *         on the original implementation of the Avatar solver (here for
 *         historical reasons)</p>
 *         </li>
 *         <li> <p>{@link myutil.intboolsolver.IBSStdExpressionClass
 *         IBSStdExpressionClass} is a full implementation of {@link
 *         myutil.intboolsolver.IBSExpressionClass IBSExpressionClass}.
 *         Its classical structure is easy to extend.</p>
 *         </li></ul>
 *     <li> <p>{@link myutil.intboolsolver.IBSAttributeClass IBSAttributeClass}
 *     describes the interface required from the attribute class that must be
 *     provided for instantiation. It also provides some data shared by all
 *     instances (some constants and a technical classes). Roughly speaking,
 *     this class provides an interpretation for open leaves.</p>
 *     <ul><li><p>{@link myutil.intboolsolver.IBSStdAttributeClass
 *         IBSStdAttributeClass} extends {@link
 *         myutil.intboolsolver.IBSAttributeClass IBSAttributeClass}. It
 *         provides a partial implementation so that instances just have
 *         to provide low level methods.</p>
 *         </li></ul>
 *     <li><p>{@link myutil.intboolsolver.IBSParamSpec IBSParamSpec},
 *     {@link myutil.intboolsolver.IBSParamComp IBSParamComp},
 *     {@link myutil.intboolsolver.IBSParamState IBSParamState},
 *     {@link myutil.intboolsolver.IBSParamSpecState IBSParamSpecState}
 *     and {@link myutil.intboolsolver.IBSParamCompState IBSParamCompState} are
 *     trivial classes used for technical reasons. The corresponding concrete
 *     classes provided for instantiation must extend them. They are the type
 *     of the parameter of the generic parser. Thus to instantiate a parser we
 *     have to provide:</p>
 *     <ul>
 *         <li> <p>a {@code Spec} class, which <b>must implement</b> {@link
 *         myutil.intboolsolver.IBSParamSpec IBSParamSpec}: The class of
 *         global system specifications, which intuitively associates
 *         leave structures to (spec-dedicated) leave identifiers).</p>
 *         </li>
 *         <li> <p>a {@code Comp} class, which <b>must implement</b> {@link
 *         myutil.intboolsolver.IBSParamComp IBSParamComp}: the class
 *         of system components, which intuitively associates leave
 *         structures to (comp-dedicated) leave identifiers).</p>
 *         </li>
 *         <li> <p>a {@code State} class, which <b>must implement</b>
 *         {@link myutil.intboolsolver.IBSParamState IBSParamState}:
 *         a class of states. It is intended to receive state machine
 *         states in provided extensions but can be used another way
 *         (in the provided extensions, there are state leaves that
 *         may be true or false in different contexts...).</p>
 *         </li>
 *         <li> <p>a {@code SpecState} class, which <b>must implement</b>
 *         {@link myutil.intboolsolver.IBSParamSpecState
 *         IBSParamSpecState}: the class of specification state, which
 *         intuitively associates leaves to int/bool values.</p>
 *         </li>
 *         <li> <p>a {@code CompState} class, which <b>must implement</b>
 *         {@link myutil.intboolsolver.IBSParamCompState
 *         IBSParamCompState}: the class of component states, which
 *         intuitively associates leaves to int/bool values.</p>
 *         </li>
 *     </ul>
 *     <p><b>WARNING!!</b> A successful instantiation requires that
 *     class <i>xxx</i> (with <i>xxx</i> among {@code Spec}, {@code Comp},
 *     {@code SpecState}, {@code CompState} and {@code State}) does
 *     not implement any IBS<i>yyy</i>Param with <i>yyy</i>
 *     &#8800;  <i>xxx</i>.</p>
 *     <p>{@link myutil.intboolsolver.IBSParamSpec IBSParamSpec},
 *     {@link myutil.intboolsolver.IBSParamComp IBSParamComp},
 *     {@link myutil.intboolsolver.IBSParamState IBSParamState},
 *     {@link myutil.intboolsolver.IBSParamSpecState IBSParamSpecState}
 *     and
 *     {@link myutil.intboolsolver.IBSParamCompState IBSParamCompState}
 *     can be used as default parameters to instanciate solvers that do
 *     not implement the corresponding concepts.</p>
 * </ul>
 * <p><a id="instanciation_sumary"> <b>Instantiation summary:</b></a></p>
 * <ol>
 *     <li><p> provide {@code Spec}, {@code Comp}, {@code SpecState},
 *     {@code CompState} and {@code State} from application. These class
 *     just have to extend their associated parameter IBS<i>xxx</i>Param
 *     and not making them extend any IBS<i>yyy</i>Param with
 *     <i>xxx</i> &#8800; <i>yyy</i></p>
 *     </li>
 *     <li> <p> provide an implementation {@code AttribClass} of
 *     {@link myutil.intboolsolver.IBSAttributeClass IBSAttributeClass} or
 *     {@link myutil.intboolsolver.IBSStdAttributeClass
 *     IBSStdAttributeClass} the instances of
 *     for {@code Spec}, {@code Comp}, {@code SpecState},
 *     {@code CompState} and {@code State}.</p>
       <PRE>
     known from context (package, imports): intboolsolver,
         Spec, Comp, SpecState, CompState, State

     public class AttribClass
         implements IBS[Std]AttributeClass &lt;Spec,Comp,SpecState,CompState,State&gt; {

              !!! put implementation of IBS[Std]AttributeClass here
     }
 *     </PRE>
 *     </li>
 *     <li> <p>build expression class <code>ExprClass</code> for your instance, ie.
 *     for example instantiate {@link myutil.intboolsolver.IBSStdExpressionClass
 *     IBSStdExpressionClass} or {@link myutil.intboolsolver.origin.IBSOriginExpressionClass
 *     IBSOriginExpressionClass}:</p>
       <PRE>
     known from context (package, imports): intboolsolver,
         Spec, Comp, SpecState, CompState, State

     public class ExprClass
         extends IBSStdExpressionClass &lt;Spec,Comp, SpecState, CompState, State&gt; {

           public ExprClass(){}

           !!! optionnal additionnal features here
     }
 *     </PRE>
 *     <p>Note that if you want to add features to the inner class <code>Expr</code>,
 *     you have to directly modify {@link myutil.intboolsolver.IBSStdExpressionClass
 *     IBSStdExpressionClass}.</p>
 *     </li>
 *     <li> <p>build the parser <code>Parser</code> for your instance, ie.
 *     instantiate an implementation of the generic parser (for example
 *     {@link myutil.intboolsolver.origin.IBSOriginParser
 *     IBSSOriginParser}) :</p>
 *        <PRE>
 *      known from context (package, imports): intboolsolver,
 *          Spec, Comp, SpecState, CompState, State
 *
 *      public class Parser
 *          extends IBSOriginParser &lt;Spec,Comp, SpecState, CompState, State&gt; {
 *
 *                 public Parser() { super(new AttrClass(),new ExprClass()); }
 *                 public Parser(AttrClass _a, ExprClass _e) { super(_c,_e); }
 *
 *            !!! optional additional features here
 *      }
 *        </PRE>
 *      Note: for the JavaCUP/Jflex parser, the scanner must also be provided
 *      (see <a href="#javacup">here </a>).
 * </ol>
 * <p><a id="instanciation_use"> <b>Typical use of an instantiated parser:</b></a></p>
 *     <PRE>
 *          private Parser parser = new Parser(new AttribClass(),new ExprClass());
 *          ExprClass.BExpr e = (ExprClass.BExpr) parser.parseBool("10 + 15 &gt;= 20");
 *          boolean b = e.eval();
 *          ...
 *    </PRE>
 * <p></p>
 * <hr>
 * <p><a id="javacup"> <b>The JavaCUP/JFlex provided parser</b></a></p>
 * <p>A classical JavaCUP/JFlex parser is provided. Sources are in the subdirectory
 * "javacup" of the intboolsolver package. A script <code>compile</code> is provided to
 * generate the java code from the source. It relies on <code>sed</code> to make the
 * small modifyings that cannot be made directly by the generating tools (due to
 * genericity). Moreover by setting the variables at the beginning of the script,
 * several different parsers and lexers can be produced (obviously, a lexer can only
 * be coupled with a parser that defines all the symbols it uses)</p>
 *
 * <i>Script variables : </i>
 * <ul><li><code>JFLEX_FILE</code>: the lexer jflex source file
 * </li><li><code>JAVACUP_FILE</code>: the parser java-cup source file
 * </li><li><code>LEXER_CLASS</code>: the class-and-file name of the generated lexer
 * </li><li><code>PARSER_CLASS</code>: the class-and-file name of the generated parser
 * </li><li><code>SYMBOL_CLASS</code>: the class-and-file name of the set of symbols
 * generated by the parser and used by the lexer.
 * </li></ul>
 * <p> The script replaces tags in sources by the parameters provided in variables.
 * Moreover it restore in the resulting java files the generic parameters that are not
 * correctly handled by java-cup and jflex.</p>
 * <ul><li>
 *     in the lexer source file, <code>IBSLexerClassName</code> is replaced by
 *     <code>LEXER_CLASS</code> and <code>IBSFlex#Symb</code> is replaced by
 *     <code>SYMBOL_CLASS</code>. After generation, the generic parameters
 *     of the lexer class are added in the lexer java file (<code>&lt; Spec extends
 *     IBSParamSpec, Comp extends IBSParamComp, State extends  IBSParamState,
 *     SpecState extends IBSParamSpecState, CompState extends IBSParamCompState
 *     &gt;</code>)
 * </li><li>
 *     the generic parameters
 *     of the parser class are added in the parser generated java file (<code>&lt;
 *     Spec extends IBSParamSpec, Comp extends IBSParamComp, State extends
 *     IBSParamState, SpecState extends IBSParamSpecState, CompState extends
 *     IBSParamCompState &gt;</code>)
 * </li></ul>
 * <p>As an example, the instantiation of the standard JFlex/javaCUP parser
 * for Avatar : </p>
 * <PRE>
public class AvatarIBSStdParser extends IBSStdParser&lt;
        AvatarSpecification,
        AvatarBlock,
        AvatarStateMachineElement,
        SpecificationState,
        SpecificationBlock&gt; {
        public AvatarIBSStdParser() {
                super();
                setLexer(new IBSStdLexer&lt;
                        AvatarSpecification,
                        AvatarBlock,
                        AvatarStateMachineElement,
                        SpecificationState,
                        SpecificationBlock&gt;());
                setAttributeClass(new AvatarIBSAttributeClass());
                setExpressionClass(new AvatarIBSExpressionClass());
        }
}
 </PRE>
 * Note: the (deprecated) constructor of javaCUP parser "super()" is used
 * as setting the lexer is a bit more complex than the default behaviour
 * of "super(lexer)" provided by javaCUP (do not use it).</p>
 * <p>WARNING : The lexer must be set first as setAttributeClass and
 * setExpressionClass modify it. (these classes are saved as member of
 * the lexer and not as member of the parser)</p>
 *
 * @version 1.0 11/04/2023
 * @author Sophie Coudert
 */
 package myutil.intboolsolver;