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
 * Package intboolsolver, a generic parser/evaluator for boolean/integer
 * expressions with open leaves (i.e identifiers).
 * Creation: 27/02/2023.
 *
 * <p>
 *     The parser may be instantiated by usual expressions with variables
 *     and their usual evaluation, but it is more generally dedicated to
 *     two-level structured systems, i.e global specifications build over
 *     a set of components. For example we can consider identifiers
 *     associated to local states that define together a global state.
 *     Default parameters are provided to instantiate solvers that do not
 *     use all the features, such as for example a solver for closed
 *     expressions (which is provided).
 *
 * </p>
 * <ul>
 *     <li> {@link myutil.intboolsolver.IBSolverAPI
 *     IBSolverAPI} provides documentation about the functions
 *     exported by the solver after it has been instantiated.
 *     </li>
 *     <li> <p>{@link myutil.intboolsolver.IBSolver IBSolver} is the solver
 *     implementation. It relies on the common structure of all expressions,
 *     which only differ on the way the open leaves are handled.</p>
 *
 *     <p>The way leaves are handled depends on the notions of specification,
 *     component, specification state, component state and (state machine)
 *     state of the instance. Thus they are parameters of the generic solver
 *     and <b> in order to instantiate the solver, we will have to
 *     provide:</b></p>
 *     <ul>
 *         <li> <p>{@code Spec} class, which must implement {@link
 *         myutil.intboolsolver.IBSParamSpec IBSParamSpec}: The class of
 *         global system specifications, which intuitively associates
 *         leave structures to (spec-dedicated) leave identifiers).</p>
 *         </li>
 *         <li> <p>{@code Comp} class, which <b>must implement</b> {@link
 *         myutil.intboolsolver.IBSParamComp IBSParamComp}: the class
 *         of system components, which intuitively associates leave
 *         structures to (comp-dedicated) leave identifiers).</p>
 *         </li>
 *         <li> <p>{@code State} class, which <b>must implement</b>
 *         {@link myutil.intboolsolver.IBSParamState IBSParamState}:
 *         a class of states. It is intended to receive state machine
 *         states in provided extensions but can be used another way
 *         (in the provided extensions, there are state leaves that
 *         may be true or false in different contexts...).</p>
 *         </li>
 *         <li> <p>{@code SpecState} class, which <b>must implement</b>
 *         {@link myutil.intboolsolver.IBSParamSpecState
 *         IBSParamSpecState}: the class of specification state, which
 *         intuitively associates leaves to int/bool values.</p>
 *         </li>
 *         <li> <p>{@code CompState} class, which <b>must implement</b>
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
 *     <p>The structure of leaf expressions is  instantiation dependent.
 *     Thus it is parametrized by "attribute" parameters (themselves
 *     parametrized by  the five classes above) for which an
 *     implementation must also be provided in order to instantiate
 *     {@link myutil.intboolsolver.IBSolver IBSolver}. Attribute
 *     parameters are decomposed into two classes:</p>
 *     <ul>
 *         <li> <p>an "Attribute" class which allow to create
 *         attribute instances with instance dependent methods.</p>
 *         </li>
 *         <li> <p>an "Attribute Class" class which contains all the
 *         methods that do not depend on attribute instances</p>
 *         </li>
 *     </ul>
 *     </li>
 *     <li><p>{@link myutil.intboolsolver.IBSAttribute
 *     IBSAttribute} is the interface describing the features
 *     to provide in order to instantiate the "Attribute" class
 *     of {@link myutil.intboolsolver.IBSolver
 *     IBSolver} (features that depend on attribute instances).
 *     <b> implementation must be provides </b>. It can be
 *     implemented directly or using the provided extensions.</p>
 *     </li>
 *     <li><p>{@link myutil.intboolsolver.IBSAttributeClass
 *     IBSAttributeClass} is the interface describing the features
 *     to provide in order to instantiate the "attribute class"
 *     class of {@link myutil.intboolsolver.IBSolver IBSolver}
 *     (features that do not depend on attribute instances).
 *     <b> implementation must be provided </b>. It requires to
 *     instanciate an "Attribute" parameter by a class that extends
 *     {@link myutil.intboolsolver.IBSAttribute IBSAttribute},
 *     (with the same {@code Spec}, {@code Comp}, {@code SpecState},
 *     {@code CompState} and {@code State} parameters).
 *     Implementation can also be implemented using provided
 *     extensions, which refine the couple
 *     ({@link myutil.intboolsolver.IBSAttribute IBSAttribute},
 *     {@link myutil.intboolsolver.IBSAttributeClass
 *     IBSAttributeClass}).</p>
 *     </li>
 * </ul>
 * <p><a id="instanciation_sumary"> <b>Instantiation
 * summary:</b></a></p>
 * <ul>
 *     <li> provide {@code Spec}, {@code Comp}, {@code SpecState},
 *     {@code CompState} and {@code State} as they are, but making
 *     them extend their associated parameter IBS<i>xxx</i>Param
 *     and not making them extend any IBS<i>yyy</i>Param with
 *     <i>xxx</i> &#8800; <i>yyy</i>
 *     </li>
 *     <li> provide an implementation {@code Attrib} of
 *     {@link myutil.intboolsolver.IBSAttribute IBSAttribute}
 *     for {@code Spec}, {@code Comp}, {@code SpecState},
 *     {@code CompState} and {@code State}.
       <PRE>
     known from context (package, imports): intboolsolver,
         Spec, Comp, SpecState, CompState, State

     public class Attrib
         implements IBSAttribute[Spec,Comp,SpecState,CompState,State] {
              !!! put implementation of IBSAttribute here
     }
 *     </PRE>
 *     </li>
 *     <li> provide an implementation {@code AttribClass} of
 *     {@link myutil.intboolsolver.IBSAttributeClass
 *     IBSAttributeClass} for {@code Spec}, {@code Comp},
 *     {@code SpecState}, {@code CompState}, {@code State} and
 *     {@code Attrib}.
       <PRE>
     known from context (package, imports): intboolsolver,
         Spec, Comp, SpecState, CompState, State, Attrib

     public class AttribClass
         extends IBSAttribute[Spec, Comp, SpecState, CompState,
                                  State,Attrib] {
              !!! put implementation of IBSAttributeClass here
     }
 *     </PRE>
 *     <li> build solver instance following this model:
       <PRE>
     known from context (package, imports): intboolsolver,
         Spec, Comp, SpecState, CompState, State, Attrib, AttribClass
     public class Solver
         extends IBSolver [ Spec,Comp, SpecState, CompState, State,
                           Attrib, AttribClass ] {
             Solver() {
                 super(new AttribClass());
             }
        !!! or (sometimes required, I don't know why...)
             Solver() {
                 super();
                 setAttributeClass(new AttribClass());
             }
        !!! put your solver's specific features here (or extends...)
     }
 *     </PRE>
 *     </li>
 * </ul>
 *
 * <hr>
 * <span style="font-size: 120%"> Extension Standard Attributes </span>
 *  <p> This extension provides a skeleton for a typical
 *  implementation with (boolean) state leaves. It provides a guided
 *  way to implement some methods of
 *  {@link myutil.intboolsolver.IBSAttribute IBSAttribute} and
 *  {@link myutil.intboolsolver.IBSAttributeClass
 *  IBSAttributeClass}</p>
 *  <p> Following the proposed approach, some partial implementation
 *  is provided. It is sometime provided as code in comments that must
 *  be copied in final instantiation (when genericity disappear).</p>
 *  <p> This extension refines the two abstract class for
 *  attributes</p>
 *  <p> Note: the two following classes should be abstract
 *  (future work) making the remaining work to do more clear</p>
 * <ul>
 *     <li><p>{@link myutil.intboolsolver.IBSStdAttribute
 *     IBSStdAttribute} implements
 *     {@link myutil.intboolsolver.IBSAttribute IBSAttribute}.
 *     To instantiate the solver using this approach, <b>a fully
 *     implemented extension of this class must be provided</b>.
 *     Comments in the file say what remains to implement
 *     (in the futur, abstraction, for automatic checking...).
 *     </li>
 *     <li><p>{@link myutil.intboolsolver.IBSStdAttributeClass
 *     IBSStdAttributeClass} implements
 *     {@link myutil.intboolsolver.IBSAttributeClass
 *     IBSAttributeClass}.
 *     To instantiate the solver using this approach, <b>a fully
 *     implemented extension of this class must be provided</b>.
 *     Comments in the file say what remains to implement
 *     (in the futur, abstraction, for automatic checking...).
 *     </li>
 * </ul>
 *
 * <p><b>Instantiation summary:</b></p>
 * <p> similar to <a href="#instanciation_sumary"> the previously
 * described process</a>, replacing IBSAttribute by IBSStdAttribute
 * and IBSAttributeClass by  IBSStdAttributeClass.</p>
 *
 * @version 0.1 07/03/2023
 *
 * @author Sophie Coudert  (rewrite from Alessandro TEMPIA CALVINO)
 */
 package myutil.intboolsolver2;