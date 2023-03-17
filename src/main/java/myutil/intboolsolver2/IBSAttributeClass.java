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

package myutil.intboolsolver2;

/**
 * class IBSAttribute (interface), describing the "static" generic part
 * shared by all open leaves of {@link IBSolver
 * IBSolver}.
 * Creation: 07/03/2023
 *
 *  <p> This interface describes the features required from the
 *  class of {@link IBSolver IBSolver}
 *  attributes (methods that do not depend on attribute instances).
 *  Its implementations are intended to instantiate
 *  the {@code AtC} parameter of the generic
 *  {@link IBSolver IBSolver} </p>
 *
 * @version 0.1 07/03/2023
 * @author Sophie Coudert  (rewrite from Alessandro TEMPIA CALVINO)
 */

public class IBSAttributeClass<
        Spec extends IBSParamSpec,
        Comp extends IBSParamComp,
        State extends IBSParamState,
        SpecState extends IBSParamSpecState,
        CompState extends IBSParamCompState
        > {
     public static final int NullAttr =0;  // val is null
     public static final int BoolConst =1;  // val is an Int integer
     public static final int IntConst  =2;  // val is an Int boolean (0 or 1)
     public static final int BoolAttr  =3;  // val is a bool IBSAttribute.
     public static final int IntAttr   =4;  // val is an int IBSAttribute.
     public final TypedAttribute NullTypedAttribute = new TypedAttribute();
     public class TypedAttribute {
          protected Attribute attrVal = null;
          protected int constVal =0;
          private int type = NullAttr;
          private TypedAttribute(){} //intVal=0; attVal=null; type=NullAttr;
          public TypedAttribute(int i,boolean isbool) {
               constVal = i;
               if (isbool) type=BoolConst; else type=IntConst;
          }
          public TypedAttribute(Attribute a,boolean isbool) {
               attrVal = a;
               if (isbool) type=BoolAttr; else type=IntAttr;
          }
          public int getType() { return type; }
          public int getConstant() { return constVal; }
          public Attribute getAttribute() { return attrVal; }
          public boolean isAttribute() { return (type >= BoolAttr); }
     }

     public TypedAttribute getTypedAttribute(Spec _spec, String _s) {
          return NullTypedAttribute;
     }
     public TypedAttribute getTypedAttribute(Comp _comp, String _s) {
          return NullTypedAttribute;
     }
     public TypedAttribute getTypedAttribute(Comp _comp, State _st) {
          return NullTypedAttribute;
     }
    /**
     * Initialisation before parsing an expression.
     *
     * <p> Automatically called by {@code buildExpression(Spec _spec)} </p>
     * @param _spec the specification that associates structures to open
     *             leaves
     */
     public void initBuild(Spec _spec){}
     /**
      * Initialisation before parsing an expression.
      *
      * <p> Automatically called by {@code buildExpression(Comp _comp)} </p>
      * @param _comp the component that associates structures to open
      *             leaves
      */
     public void initBuild(Comp _comp){}
     /**
      * Initialisation before parsing an expression.
      *
      * <p> Automatically called by {@code buildExpression()} </p>
      */
     public void initBuild(){}

     public class Attribute {
          // returns a type from IBSolver (to modify)
          // (i.e. among IMMEDIATE_(BOOL,INT,NO))
          int getType() { return NullAttr; }
          int getValue(SpecState _ss) { return 0; }
          int getValue(SpecState _ss, State _st) { return 0; }
          int getValue(CompState _cs) { return 0; }
          // for efficiency, to allow low level objects as state
          int getValue(Object _quickstate) { return 0; }
          void setValue(SpecState _ss, int _val) {}
          void setValue(CompState _cs, int _val) {}
          public boolean isState() { return false; }
          /**
           * links state attributes to their environment (spec, comp).
           * If possible... (attributes must have internal information about
           * this environment). Too specific, to enhance in the future...
           */
          void linkState() {}
          /**
           * links components of attributes to their environment (spec).
           * If possible... (attributes must have internal information about
           * their components). Too specific, to enhance in the future...
           */
          public void linkComp(Spec _spec) {}
          public String toString() { return ""; }
     }


}
