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
   * Class IBSAttribute
   * Creation: 27/02/2023
   * @version 0.0 27/02/2023
   *
   * @author Sophie Coudert
 */

package intboolsolver;
// import intboolsolver.IBSolver; // usefull in implementations

public interface IBSAttribute {
    
    public static class Spec{};
    public static class Comp{};
    public static class State{};
    public static class SpecState{};
    public static class CompState{};

    public static final class IBSTypedAttribute {
	public static final int None=0;  // val is null
	public static final int BoolConst =1;  // val is an Int integer
	public static final int IntConst  =2;  // val is an Int boolean (0 or 1)
	public static final int BoolAttr  =3;  // val is a bool IBSAttribute.
	public static final int IntAttr   =4;  // val is an int IBSAttribute.
	public static final IBSTypedAttribute NullAttribute =
	    new IBSTypedAttribute(None,null);
	                                    
	private int type = None;
	private Object val = null;
	private IBSTypedAttribute(){};
	public <AT extends IBSAttribute>IBSTypedAttribute(int _type, Object _val){
	    type = _type; val = _val;
	    if ( (val == null && type !=None) ||
		 (type == IntConst && !(val instanceof Integer)) ||
		 (type == BoolConst && !(val instanceof Integer))||
		 (type == BoolAttr && !(instanceOfMe(BoolAttr,val))) )
		; // error case
	}
	public int getType(){return type;}
	public Object getVal(){return val;}
	public boolean isAttribute(){return (type >= BoolAttr);}
    }
    // method to override by subclasses
    public static boolean instanceOfMe(int type, Object _val) {
	return (_val instanceof IBSAttribute);
    }
    
    public static void initBuild(Spec _spec){};
    public static void initBuild(Comp _comp){};
    public static void initBuild(){};

    public static IBSTypedAttribute getTypedAttribute(Spec _spec, String _s){
	return IBSTypedAttribute.NullAttribute;
    }
    public static IBSTypedAttribute getTypedAttribute(Comp _comp, String _s){
	return IBSTypedAttribute.NullAttribute;
    }
    public static IBSTypedAttribute getTypedAttribute(Comp _comp, State _st){
	return IBSTypedAttribute.NullAttribute;
    }

    // returns a type from IBSolver
    // (i.e. among IMMEDIATE_(BOOL,INT,NO))
    public int getAttributeType();

    public int getValue(SpecState _ss);
    public int getValue(SpecState _ss, State _st);
    public int getValue(CompState _cs);
    // for efficiency, to allow low level objects as state
    public int getValue(Object _quickstate);
    public void setValue(SpecState _ss, int _val);
    public void setValue(CompState _cs, int _val);
    
   public boolean isState();
   public void linkComp(Spec _spec);
   public void linkState();
   public String toString();
}
