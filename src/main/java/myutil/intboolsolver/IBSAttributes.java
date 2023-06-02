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
 * <p> This class defines the features attributes must provide in order
 * to be usable in the the generic IntBoolSolver System.</p>
 * Creation: 11/04/2023
 *
 * <p>  The handling of variable expressions is instantiation dependent.
 * Thus they have associated "attribute" whose class is provided by
 * instantiation. This class provides two main kinds of methods:
 * <ul>
 *     <li> methods for initialising attribute with information useful to
 *     evaluate them later.</li>
 *     <li> attribute evaluation methods</li>
 * </ul>
 * <p> In this interface, provided features are parametrized by the same
 * classes as {@link myutil.intboolsolver.IBSParserAPI IBSParserAPI}
 * i.e  {@code Spec}, {@code Comp}, {@code SpecState},
 * {@code CompState} and {@code State} parameters.</p>
 *
 * <p>Note: in this class, both boolean and integer values are represented by integers.
 * How integers are interpreted by boolean is application/instanciation dependent.</p>
 *
 * @version 1.0 11/04/2023
 * @author Sophie Coudert
*/
public class IBSAttributes<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
     /**
      * The different types of attributes:
      * The type obtained when building
      * attribute fails (for example due to an unknown identifier).
      */
     /* DO NOT MODIFY */
     public static final int NullAttr =0;  // val is null
     /**
      * The different types of attributes:
      * The type obtained when building
      * attribute identifies a boolean constant.
      */
     /* DO NOT MODIFY */
     public static final int BoolConst =1;  // val is an Int integer
     /**
      * The different types of attributes:
      * The type obtained when building
      * attribute identifies an integer constant.
     */
     /* DO NOT MODIFY */
     public static final int IntConst  =2;  // val is an Int boolean (0 or 1)
     /**
      * The different types of attributes:
      * The type obtained when building
      * attribute identifies a boolean variable.
     */
     /* DO NOT MODIFY */
     public static final int BoolAttr  =3;  // val is a bool IBSAttribute.
     /**
      * The different types of attributes:
      * The type obtained when building
      * attribute identifies an integer variable.
     */
     /* DO NOT MODIFY */
     public static final int IntAttr   =4;  // val is an int IBSAttribute.
     /** Typed attribute returned when building an attribute fails
      */
     /* DO NOT MODIFY. */
     public final TypedAttribute NullTypedAttribute = new TypedAttribute();

     /**
      * Technical: classified attributes, i.e attributes provided with information
      * obtained while building them.
      * <p> This class is not robust against erroneous use. </p>
      */
     /* DO NOT MODIFY */
     public class TypedAttribute {
          /** The type of the typed attribute */
          private final int type;
          /** The value of the attribute. Only relevant  if the field {@code type} is
           * IntAttr or BoolAttr
           */
          protected final Attribute attrVal;
          /** The value of the attribute. Only relevant if the field {@code type} is
           * IntConst or BoolConst
           */
          protected final int constVal;
          /** Build a null typed attribute */
          private TypedAttribute(){
               type = NullAttr;
               attrVal = null;
               constVal = 0;
          } //intVal=0; attVal=null; type=NullAttr;
          /**
           * Build a constant typed attribute.
           * <p> The interpretation of integers by boolean is application
           * dependent: provided integer values are preserved as they are.</p>
           * @param _i the constant value
           * @param _isbool is true for boolean constants and
           *               false for integer constants.
           */
          public TypedAttribute(int _i, boolean _isbool) {
               constVal = _i;
               attrVal = null;
               if (_isbool) type = BoolConst; else type = IntConst;
          }
          /**
           * Build a variable typed attribute
           * @param _a the attribute (structure associated to a variable identifier)
           * @param _isbool is true for boolean variables and
           *               false for integer variables.
           */
          public TypedAttribute(Attribute _a, boolean _isbool) {
               constVal = 0;
               attrVal = _a;
               if (_isbool) type = BoolAttr; else type = IntAttr;
          }
          /** As its name suggests...
           * @return the attribute type, among NullAttr IntConst BoolConst IntAttr BoolAttr
           */
          public int getType() { return type; }
          /** As its name suggests... Only relevant  for constant types (unchecked)
           * @return the constant value of the attribute
           */
          public int getConstant() { return constVal; }
          /** As its name suggests... Only relevant for variable types (unchecked)
           * @return the attribute that characterizes the variable
           */
          public Attribute getAttribute() { return attrVal; }
          public boolean isAttribute() { return (type >= BoolAttr); }
     }

     /** Build an attribute from a specification and an identifier.
      * @param _spec the specification
      * @param _s the identifier string
      * @return the build typed attribute or <code>NullTypedAttribute</code> if building fails.
      */
     public TypedAttribute getTypedAttribute(Spec _spec, String _s) {
          return NullTypedAttribute;
     }
     /** Build an attribute from a component and an identifier.
      * @param _comp the component
      * @param _s the identifier string
      * @return the build typed attribute or <code>NullTypedAttribute</code> if building fails.
      */
     public TypedAttribute getTypedAttribute(Comp _comp, String _s) {
          return NullTypedAttribute;
     }
     /** Build a boolean attribute from a component and a (state machine) state.
      * @param _comp the component
      * @param _st the state
      * @return the build typed attribute (with type BoolAttr) or <code>NullTypedAttribute</code> if building fails.
      */
     public TypedAttribute getTypedAttribute(Comp _comp, State _st) {
          return NullTypedAttribute;
     }

     /** Class of attributes that are associated to variables.
      *
      */
     public class Attribute {
          public int getType() { return NullAttr; }
          public int getValue(SpecState _ss) { return 0; }
          public int getValue(SpecState _ss, State _st) { return 0; }
          public int getValue(CompState _cs) { return 0; }
          // for efficiency, to allow low level objects as state
          public int getValue(Object _quickstate) { return 0; }
          public void setValue(SpecState _ss, int _val) {}
          public void setValue(CompState _cs, int _val) {}
          public boolean isState() { return false; }
          /**
           * links state attributes to their environment (spec, comp).
           * If possible... (attributes must have internal information about
           * this environment). Too specific, to enhance in the future...
           */
          public void linkState() {}
          /**
           * links components of attributes to their environment (spec).
           * If possible... (attributes must have internal information about
           * their components). Too specific, to enhance in the future...
           * @param _spec the specification in which the components are
           *              comprised
           */
          public void linkComp(Spec _spec) {}
          public String toString() { return ""; }
     }


}
