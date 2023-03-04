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
import avatartranslator.*;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
import myutil.intboolsolver.IBSAbstractAttribute;
/**
 * Class AvatarIBSAbstractAttribute
 * Avatar IBS Abstract Attribute implementation
 * Creation: 27/02/2023
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 27/02/2023
 */

// TO BE ABLE TO COMPILE THIS FILE:
// - AvatarSpecification must extend AvatarIBSAbstractAttribute.Spec
// - AvatarBlock must extend AvatarIBSAbstractAttribute.Comp
// - AvatarStateMachineElement must extend AvatarIBSAbstractAttribute.State
// - AvatarSpecificationState must extend AvatarIBSAbstractAttribute.SpecState
// - AvatarSpecificationBlock must extend AvatarIBSAbstractAttribute.CompState

public class AvatarIBSAbstractAttribute extends IBSAbstractAttribute {

    // attribute access information
    private AvatarBlock block;
    private int blockIndex; // Similar to the one of the block "block".
                            // For performance reason
    private int accessIndex;
    private int shift;
    private int mask;

    // handling already covered attributes (memorisation)
    private static Spec findSpec = null;
    private static Comp findComp = null;
    private static String findString = "";
    private static AvatarElement keyElement;
    private static Map<AvatarElement, IBSTypedAttribute> attributesMap;   

    protected int initAttribute(Spec _sp) {
	AvatarSpecification _spec = (AvatarSpecification) _sp;
	
        //Extract Block and Attribute
        String[] splitS;
        String blockString;
        String fieldString;
        
        if (_spec == null) {
            return IBSTypedAttribute.None;
        }
        
        if (s.matches(".+\\..+")) {
            splitS = s.split("\\.");
            blockString = splitS[0];
            fieldString = splitS[1];
        } else {
            return IBSTypedAttribute.None;
        }
        
        block = _spec.getBlockWithName(blockString);

        if (block == null) {
            return IBSTypedAttribute.None;
        }
        
        blockIndex = _spec.getBlockIndex(block);
        
        int attributeIndex = block.getIndexOfAvatarAttributeWithName(fieldString);
        
        shift = 0;
        mask = 0xFFFFFFFF;
        
        if (attributeIndex == -1) {
            attributeIndex = block.getIndexOfConstantWithName(fieldString);
            if (attributeIndex == -1) {
                // state?
                state = block.getStateMachine().getStateWithName(fieldString);
                if (state == null) {
                    return IBSTypedAttribute.None;
                }
                isState = true;
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
		return IBSTypedAttribute.BoolAttr;
            } else { //constant
                accessIndex = attributeIndex;
                AvatarAttribute attr = block.getConstantWithIndex(accessIndex);
		constantInt = attr.getInitialValueInInt();
                return (attr.isBool() ? IBSTypedAttribute.BoolConst : IBSTypedAttribute.IntConst);
	    }
        } else {
            int offset = block.getBooleanOffset();
            int optRatio = block.getAttributeOptRatio();
            if (offset == -1 || attributeIndex < offset) {
                accessIndex = attributeIndex / optRatio + SpecificationBlock.ATTR_INDEX;
                shift = (attributeIndex % optRatio) * (32 / optRatio);
                if (optRatio == 2) {
                    mask = 0xFFFF;
                } else if (optRatio == 4) {
                    mask = 0xFF;
                }
            } else {
                accessIndex = SpecificationBlock.ATTR_INDEX + (offset + optRatio - 1) / optRatio + ((attributeIndex - offset) / 32);
                shift = (attributeIndex - offset) % 32;
                mask = 1;
            }
            return this.attributeType();
        }
    }
    
    
    protected int initAttribute(Comp _blk) {
	AvatarBlock _block = (AvatarBlock)_blk;
	
        //Extract Attribute
        if (_block == null) {
            return IBSTypedAttribute.None;
        }
        
        this.blockIndex = -1; //not initialized
        
        int attributeIndex = block.getIndexOfAvatarAttributeWithName(s);
        
        shift = 0;
        mask = 0xFFFFFFFF;
        
        if (attributeIndex == -1) {
            attributeIndex = block.getIndexOfConstantWithName(s);
            if (attributeIndex == -1) {
                // state?
                state = block.getStateMachine().getStateWithName(s);
                if (state == null) {
                    return IBSTypedAttribute.None;
                }
                isState = true;
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
            } else { //constant
                accessIndex = attributeIndex;
                AvatarAttribute attr = block.getConstantWithIndex(accessIndex);
		constantInt = attr.getInitialValueInInt();
                return (attr.isBool() ? IBSTypedAttribute.BoolConst : IBSTypedAttribute.IntConst);
	    }
        } else {
            int offset = block.getBooleanOffset();
            int optRatio = block.getAttributeOptRatio();
            if (offset == -1 || attributeIndex < offset) {
                accessIndex = attributeIndex / optRatio + SpecificationBlock.ATTR_INDEX;
                shift = (attributeIndex % optRatio) * (32 / optRatio);
                if (optRatio == 2) {
                    mask = 0xFFFF;
                } else if (optRatio == 4) {
                    mask = 0xFF;
                }
            } else {
                accessIndex = SpecificationBlock.ATTR_INDEX + (offset + optRatio - 1) / optRatio + ((attributeIndex - offset) / 32);
                shift = (attributeIndex - offset) % 32;
                mask = 1;
            }
	    return this.attributeType();
        }  
    }


    protected int initStateAttribute(Comp _comp, State) {
	// verify type
	AvatarBloc b = (AvatarBloc)_comp;
	AvatarStateMachineElement e = (AvatarStateMachineElement) _state;
	// update access information
        accessIndex = -1;
        shift = 0;
        mask = 0xFFFFFFFF;
        block = null;
	return IBSTypedAttribute.BoolAttr;
    }
    
    private int attributeType() {
        if (isState) { // should not happen
            return IBSTypedAttribute.BoolAttr;
        }
        int offset = block.getBooleanOffset();
        int ratio = block.getAttributeOptRatio();
        int attributeIndex = (accessIndex - SpecificationBlock.ATTR_INDEX) * ratio + shift * ratio / 32;
        if (offset == -1 || (attributeIndex < offset)) {
            if (block.getAttribute((accessIndex - SpecificationBlock.ATTR_INDEX)).getType()
		== AvatarType.BOOLEAN) {
                return IBSTypedAttribute.BoolAttr;
            } else {
                return IBSTypedAttribute.IntAttr;
            }
        } else {
            return IBSTypedAttribute.BoolAttr;
        }
    }

   protected static IBSTypedAttribute make_getTypedAttribute(Spec _spec, String _s) {
	IBSTypedAttribute a = findAttribute(_spec, _s);
	if (a == null) {
	    AvatarIBSAbstractAttribute x = new AvatarIBSAbstractAttribute(); // replaced...
	    baseInitAttributes(_spec,_s);
	    switch (x.type) {
	    case IBSTypedAttribute.None:{
		return IBSTypedAttribute.NullAttribute;
	    }
	    case IBSTypedAttribute.BoolConst:
	    case IBSTypedAttribute.IntConst:{
	        a = new IBSTypedAttribute(x.type,(Object) new Int(constantInt));
		break;
	    }
	    others: {
		a = new IBSTypedAttribute(x.type,(Object) x);
		    
	    }
	    }
	    addAttribute(_spec, _s, a);
	}
	return a;
   }

    protected static IBSTypedAttribute make_getTypedAttribute(Comp _comp, String _s) {
	IBSTypedAttribute a = findAttribute(_comp, _s);
	if (a == null) {
	    AvatarIBSAbstractAttribute x = new AvatarIBSAbstractAttribute(); // replaced
	    baseInitAttributes(_comp,_s);
	    switch (x.type) {
	    case IBSTypedAttribute.None:{
		return IBSTypedAttribute.NullAttribute;
	    }
	    case IBSTypedAttribute.BoolConst:
	    case IBSTypedAttribute.IntConst:{
	        a = new IBSTypedAttribute(x.type,(Object) new Int(constantInt));
		break;
	    }
	    others: {
		a = new IBSTypedAttribute(x.type,(Object) x);
		    
	    }
	    }
	    addAttribute(_comp, _s, a);
	}
	return a;
    }
    
    protected static IBSTypedAttribute getTypedAttribute(Comp _comp, State _state) {
	IBSTypedAttribute a = findAttribute(_comp, _state);
	if (a == null) {
	    AvatarIBSAbstractAttribute x = new AvatarIBSAbstractAttribute(); // replaced
	    baseInitAttributes(_comp,_state);
	    switch (x.type) {
	    case IBSTypedAttribute.None:{
		return IBSTypedAttribute.NullAttribute;
	    }
	    case IBSTypedAttribute.BoolConst:
	    case IBSTypedAttribute.IntConst:{
	        a = new IBSTypedAttribute(x.type,(Object) new Int(constantInt));
		break;
	    }
	    others: {
		a = new IBSTypedAttribute(x.type,(Object) x);
		    
	    }
	    }
	    addAttribute(_comp, _state, a);
	}
	return a;
    }

    public static boolean instanceOfMe(int type, Object _val) {
	return (_val instanceof AvatarIBSAbstractAttribute &&
		_type == type );
    }

    public static String getStateName(Comp _comp, State _state){
	return ((AvatarStateMachineElement)_state).name;
    }

    private void initBuild_internal(){
        if (attributesMap == null) {
            attributesMap = new HashMap<AvatarElement, IBSTypedAttribute>();
	}
    }

    public static void initBuild(Spec _spec){initBuild_internal();}

    public static void initBuild(Comp _comp){initBuild_internal();}

    protected static IBSTypedAttribute findAttribute(Spec _sp, String _s){
	AvatarSpecification _spec = (AvatarSpecification)_sp;
	AvatarElement ae = getElement(_s, _spec);
        if (ae != null && attributesMap.containsKey(ae)) {
            IBSTypedAttribute ta = attributesMap.get(ae);
            if (ta.isAttribute()){
		//might be uninitialized
		((AvatarIBSAbstractAttribute)(ta.getVal())).linkComp(_spec); 
	    }
	    return ta;
	}
	findString = _s;
	findSpec = _sp;
	keyElement = ae;
	return null;
    }
    
    protected static void addAttribute(Spec _spec, String _s, IBSTypedAttribute _att){
	AvatarElement ae;
	if ( _s == findString && _spec == findSpec )
	    ae = keyElement;
	else
	    ae = getElement(_s, (AvatarSpecification)_spec);
	if (ae != null){
	    if (_att.isAttribute() && !(_att.val instanceof AvatarIBSAbstractAttribute))
	        return; // should be an error
            attributesMap.put(ae, _att);
	}
    }
    
    protected static IBSTypedAttribute findAttribute(Comp _cmp, String _s){
	AvatarBlock _comp = (AvatarBlock)_cmp;
	AvatarElement ae = getElement(_s, _comp);
        if (ae != null && attributesMap.containsKey(ae)) {
            IBSTypedAttribute ta = attributesMap.get(ae);
	    return ta;
	}
	findString = _s;
	findComp = _cmp;
	keyElement = ae;
	return null;
    }

    protected static void addAttribute(Comp _comp, String _s, IBSTypedAttribute _att){
	AvatarElement ae;
	if ( _s == findString && _comp == findComp )
	    ae = keyElement;
	else
	    ae = getElement(_s, _comp);
	if (ae != null){
	    if (_att.isAttribute() && !(_att.val instanceof AvatarIBSAbstractAttribute))
	        return; // should be an error
            attributesMap.put(ae, _att);
	}
    }

    protected static IBSTypedAttribute findAttribute(Comp _comp, State _st){
	AvatarElement _state = (AvatarElement)_st;
        if (_state != null && attributesMap.containsKey(_state)) {
            IBSTypedAttribute ta = attributesMap.get(_state);
	    return ta;
	}
	return null;
    }
    
    protected static void addAttribute(Comp _comp, State _state, IBSTypedAttribute _att){
	if ( _att==null ||
	     ( _att.isAttribute() &&
	       ( !(instanceOfMe(_att)) ||
	         !(_att.isState) ||
	         _state != (AvatarIBSAbstractAttribute)_att.state
	       )
	     )
	   )
	    return; // should be an error
	attributesMap.put((AvatarElement)_state, _att);
    }

    public boolean instanceOfMe(IBSTypedAttribute _att) {
	return _att.getVal() instanceof AvatarIBSAbstractAttribute &&
	    _attr.getType() == (AvatarIBSAbstractAttribute).getType();
	    
 
    public int getValue(SpecState _specSt) {
	SpecificationState _ss = (SpecificationState) _specSt;
        int value;
        
        if (isState) {
            if (_ss.blocks == null || accessIndex == -1) {
                return 0;
            }
            if (_ss.blocks[blockIndex].values[SpecificationBlock.STATE_INDEX] == accessIndex) {
                return 1;
            } else {
                return 0;
            }
        }
        
        value = (_ss.blocks[blockIndex].values[accessIndex] >> shift) & mask;
        
        if (mask > 1 && value > (mask >> 1)) {
            //convert to negative
            value = ~((value ^ mask) + 1) + 1;
        }
        
        return value;
    }
    
    
    public int getValue(CompState _compSt) {
	SpecificationBlock _sb = (SpecificationBlock)_compSt;
        int value;
        
        if (isState) {
            if (_sb == null || accessIndex == -1) {
                return 0;
            }     
            if (_sb.values[SpecificationBlock.STATE_INDEX] == accessIndex) {
                return 1;
            } else {
                return 0;
            }
        }
        
        value = (_sb.values[accessIndex] >> shift) & mask;
        
        if (mask > 1 && value > (mask >> 1)) {
            //convert to negative
            value = ~((value ^ mask) + 1) + 1;
        }
        
        return value;
    }
    
    public int getValue(Object _quickstate) {
	int[] _attributesValues = (int[]) _quickstate;
        int value;
        
        if (isState) {
            return 0;
        }
        
        //Cancel offset based on Specification Blocks
        value = (_attributesValues[accessIndex - SpecificationBlock.ATTR_INDEX] >> shift) & mask;
        
        if (mask > 1 && value > (mask >> 1)) {
            //convert to negative
            value = ~(value ^ mask + 1) + 1;
        }
        
        return value;
    }
    
    public int getValue(SpecState _specSt, State _state) {
	SpecificationState _ss = (SpecificationState) _SpecSt;
	AvatarStateMachineElement _asme = (AvatarStateMachineElement) _state; 
        int value;
        
        if (isState) {
            return (state == _asme) ? 1 : 0;
        }
        
        value = (_ss.blocks[blockIndex].values[accessIndex] >> shift) & mask;
        
        if (mask > 1 && value > (mask >> 1)) {
            //convert to negative
            value = ~((value ^ mask) + 1) + 1;
        }
        
        return value;
    }

    public void setValue(SpecState _specSt, int _value) {
	SpecificationState _ss = (SpecificationState) _specSt; 
        if (isState) {
            return;
        }
        
        _ss.blocks[blockIndex].values[accessIndex] = (_ss.blocks[blockIndex].values[accessIndex] & (~(mask << shift))) | ((value & mask) << shift);
    }
    
    public void setValue(CompState _sb, int _value) {
	SpecificationBlock _sb = (SpecificationBlock) _compSt;
        if (isState) {
            return;
        }

        sb.values[accessIndex] = (_sb.values[accessIndex] & (~(mask << shift))) | ((value & mask) << shift);
    }
    
    public void linkComp(Spec spec) {
        if (blockIndex == -1) {
            //initialize it
            blockIndex = (AvatarSpecification)spec.getBlockIndex(block);
        }
    }

    public void linkState() {
        if (isState) {
            if (block != null) {
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
            } else {
                accessIndex = -1;
            }
        }
    }    
        
    public static AvatarElement getElement(String s, AvatarSpecification spec) {
        //Extract Block and Attribute
        String[] splitS;
        String blockString;
        String fieldString;
        AvatarBlock block;
        int blockIndex;
        
        if (spec == null) {
            return null;
        }
        
        if (s.matches(".+\\..+")) {
            splitS = s.split("\\.");
            blockString = splitS[0];
            fieldString = splitS[1];
        } else {
            return null;
        }
        
        block = spec.getBlockWithName(blockString);
        
        if (block == null) {
            return null;
        }
        
        blockIndex = spec.getBlockIndex(block);
        
        int attributeIndex = block.getIndexOfAvatarAttributeWithName(fieldString);
        
        if (attributeIndex == -1) {
            attributeIndex = block.getIndexOfConstantWithName(fieldString);
            if (attributeIndex == -1) {
                // state?
                return block.getStateMachine().getStateWithName(fieldString);
            } else {
                //not created for constants
                return null;
            }
        } else {
            return block.getAvatarAttributeWithName(fieldString);
        }
    }
    
    public static AvatarElement getElement(String s, AvatarBlock block) {
        //Extract Attribute        
        if (block == null) {
            return null;
        }
        
        
        int attributeIndex = block.getIndexOfAvatarAttributeWithName(s);
        
        if (attributeIndex == -1) {
            attributeIndex = block.getIndexOfConstantWithName(s);
            if (attributeIndex == -1) {
                // state?
                return block.getStateMachine().getStateWithName(s);
            } else {
                //not created for constants
                return null;
            }
        } else {
            return block.getAvatarAttributeWithName(s);
        }
    }
    
}
