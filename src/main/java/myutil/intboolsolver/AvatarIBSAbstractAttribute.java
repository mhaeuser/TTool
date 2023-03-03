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

package avatartranslator;

import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
import intboolsolver.IBSAttributeInterface;
/**
 * Class AvatarIBSttribute
 * Avatar IBS Attribute
 * Creation: 27/02/2023
 *
 * @author Sophie Coudert (rewrite from Alessandro TEMPIA CALVINO)
 * @version 0.0 27/02/2023
 */
public class AvatarIBSAttribute extends IBSAttribute {
    private AvatarBlock block;
    private int compIndex; // Similar to the one of the block "block". For performance reason
    private int accessIndex;
    private int shift;
    private int mask;

    private static Spec findSpec = null;
    private static Comp findComp = null;
    private static String findString = "";
    private static AvatarElement keyElement;
    private static Map<AvatarElement, IBSTypedAttribute> attributesMap;
    
    public class Spec; // must be extended by AvatarSpecification
    public class Comp; // must be extended by AvatarBlock
    public class State; // must be extended by AvatarStateMachineElement
    

    public AvatarIBSAttribute(Spec _spec, String _s) {
	super(_spec, _s);
        type = initAttributes((AvatarSpecification)_spec);
    }
    
    public AvatarIBSAttribute(Comp _comp, String _s) {
	super(_comp, _s);
        type = initAttributes((AvatarBloc)_comp);
    }
    @overrides
    public AvatarIBSAttribute(Comp _comp, State _state) {
	super(_comp,_state);
	// verify type
	AvatarBloc b = (AvatarBloc)_comp;
	AvatarStateMachineElement e = (AvatarStateMachineElement) _state;
        accessIndex = -1;
        shift = 0;
        mask = 0xFFFFFFFF;
        block = null;
    }

    @overrides
    public static String getStateName(Comp _comp, State _state){
	return ((AvatarStateMachineElement)_state).name;
    }

    private void initBuild_internal(){
        if (attributesMap == null) {
            attributesMap = new HashMap<AvatarElement, IBSTypedAttribute>();
	}
    }
    @overrides
    public static void initBuild(Spec _spec){initBuild_internal();}
    @overrides
    public static void initBuild(Comp _comp){initBuild_internal();}

    
    protected static IBSTypedAttribute findAttribute(Spec _spec, String _s){
	AvatarElement ae = getElement(_s, (AvatarSpecification)_spec);
        if (ae != null && attributesMap.containsKey(ae)) {
            IBSTypedAttribute ta = attributesMap.get(ae);
            if (ta.type == AttrType){
		//might be uninitialized
		((AvatarIBSAttribute)(ta.val)).setBlockIndex((AvatarSpecification)_spec); 
	    }
	    return ta;
	}
	findString = _s;
	findSpec = _spec;
	keyElement = ae;
	return null;
    }
    
    protected static void addAttribute(Spec _spec, String _s, IBSTypedAttribute _att){
	if (_att.isIBSAttribute() && !(_att.val instanceof AvatarIBSAttribute))
	    return; // should be an error
	AvatarElement ae;
	if ( _s == findString && _spec == findSpec )
	    ae = keyElement;
	else
	    ae = getElement(_s, (AvatarSpecification)_spec);
	if (ae != null)
            attributesMap.put(ae, _att);
    }
    
    protected static IBSTypedAttribute findAttribute(Comp _comp, String _s){
	AvatarElement ae = getElement(_s, (AvatarBlock)_comp);
        if (ae != null && attributesMap.containsKey(ae)) {
            IBSTypedAttribute ta = attributesMap.get(ae);
	    return ta;
	}
	findString = _s;
	findComp = _comp;
	keyElement = ae;
	return null;
    }

    protected static void addAttribute(Comp _comp, String _s, IBSTypedAttribute _att){
	if (_att.isIBSAttribute() && !(_att.val instanceof AvatarIBSAttribute))
	    return; // should be an error
	AvatarElement ae;
	if ( _s == findString && _comp == findComp )
	    ae = keyElement;
	else
	    ae = getElement(_s, _comp);
	if (ae != null)
            attributesMap.put(ae, _att);
    }

    protected static IBSTypedAttribute findAttribute(Comp _comp, State _state){
        if (_state != null && attributesMap.containsKey((AvatarElement)_state)) {
            IBSTypedAttribute ta = attributesMap.get((AvatarElement)_state);
	    return ta;
	}
	return null;
    }
    
    protected static void addAttribute(Comp _comp, State _state, IBSTypedAttribute _att){
	if ( _att==null ||
	     ( _att.isIBSAttribute() &&
	       ( !(_att.val instanceof AvatarIBSAttribute) ||
	         !(_att.isState) ||
	         _state != _att.state
	       )
	     )
	   )
	    return; // should be an error
	attributesMap.put((AvatarElement)_state, _att);
    }

    @overrides
    public static IBSTypedAttribute getTypedAttribute(Spec _spec, String _s){
	return <AvatarIBSAttribute>make_getTypedAttribute(_spec, _s);
    }
    @overrides
    public static IBSTypedAttribute getTypedAttribute(Comp _comp, String _s){
	return <AvatarIBSAttribute>make_getTypedAttribute(_comp, _s);
    }
    @overrides
    public static IBSTypedAttribute getTypedAttribute(Comp _comp, State _state){
	return <AvatarIBSAttribute>make_getTypedAttribute(_spec, _state);
    }
    


    

    
    private int initAttributes(AvatarSpecification _spec) {
        //Extract Block and Attribute
        String[] splitS;
        String blockString;
        String fieldString;
        
        if (_spec == null) {
            return IBSTypedAttribute.NoneType;
        }
        
        if (s.matches(".+\\..+")) {
            splitS = s.split("\\.");
            blockString = splitS[0];
            fieldString = splitS[1];
        } else {
            return IBSTypedAttribute.NoneType;
        }
        
        block = _spec.getBlockWithName(blockString);
        comp = (Comp)block;
        if (block == null) {
            return IBSTypedAttribute.NoneType;
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
                    return IBSTypedAttribute.NoneType;
                }
                isState = true;
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
		return IBSTypedAttribute.AttrType;
            } else { //constant
                accessIndex = attributeIndex;
                AvatarAttribute attr = block.getConstantWithIndex(accessIndex);
		constantInt = attr.getInitialValueInInt();
                return (attr.isBool() ? IBSTypedAttribute.BoolType : IBSTypedAttribute.IntType);
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
            return IBSTypedAttribute.AttrType;
        }
    }
    
    private int initAttributes(AvatarBlock _block) {
        //Extract Attribute
        if (_block == null) {
            return IBSTypedAttribute.NoneType;
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
                    return IBSTypedAttribute.NoneType;
                }
                isState = true;
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
            } else { //constant
                accessIndex = attributeIndex;
                AvatarAttribute attr = block.getConstantWithIndex(accessIndex);
		constantInt = attr.getInitialValueInInt();
                return (attr.isBool() ? IBSTypedAttribute.BoolType : IBSTypedAttribute.IntType);
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
	    return IBSTypedAttribute.AttrType;
        }  
    }
    
    
    public void setBlockIndex(AvatarSpecification spec) {
        if (blockIndex == -1) {
            //initialize it
            blockIndex = spec.getBlockIndex(block);
        }
    }
    
 
    public int getValue(SpecificationState ss) {
        int value;
        
        if (isState) {
            if (ss.blocks == null || accessIndex == -1) {
                return 0;
            }
            if (ss.blocks[blockIndex].values[SpecificationBlock.STATE_INDEX] == accessIndex) {
                return 1;
            } else {
                return 0;
            }
        }
        
        value = (ss.blocks[blockIndex].values[accessIndex] >> shift) & mask;
        
        if (mask > 1 && value > (mask >> 1)) {
            //convert to negative
            value = ~((value ^ mask) + 1) + 1;
        }
        
        return value;
    }
    
    public int getValue(SpecificationState ss, AvatarStateMachineElement asme) {
        int value;
        
        if (isState) {
            return (state == asme) ? 1 : 0;
        }
        
        value = (ss.blocks[blockIndex].values[accessIndex] >> shift) & mask;
        
        if (mask > 1 && value > (mask >> 1)) {
            //convert to negative
            value = ~((value ^ mask) + 1) + 1;
        }
        
        return value;
    }
    
    public int getValue(SpecificationBlock sb) {
        int value;
        
        if (isState) {
            if (sb == null || accessIndex == -1) {
                return 0;
            }     
            if (sb.values[SpecificationBlock.STATE_INDEX] == accessIndex) {
                return 1;
            } else {
                return 0;
            }
        }
        
        value = (sb.values[accessIndex] >> shift) & mask;
        
        if (mask > 1 && value > (mask >> 1)) {
            //convert to negative
            value = ~((value ^ mask) + 1) + 1;
        }
        
        return value;
    }
    
    public int getValue(int[] attributesValues) {
        int value;
        
        if (isState) {
            return 0;
        }
        
        //Cancel offset based on Specification Blocks
        value = (attributesValues[accessIndex - SpecificationBlock.ATTR_INDEX] >> shift) & mask;
        
        if (mask > 1 && value > (mask >> 1)) {
            //convert to negative
            value = ~(value ^ mask + 1) + 1;
        }
        
        return value;
    }
    
    public void setValue(SpecificationState ss, int value) {        
        if (isState) {
            return;
        }
        
        ss.blocks[blockIndex].values[accessIndex] = (ss.blocks[blockIndex].values[accessIndex] & (~(mask << shift))) | ((value & mask) << shift);
    }
    
    public void setValue(SpecificationBlock sb, int value) {      
        if (isState) {
            return;
        }

        sb.values[accessIndex] = (sb.values[accessIndex] & (~(mask << shift))) | ((value & mask) << shift);
    }
    
    //Link state to access index in the state machine
    public void linkState() {
        if (isState) {
            if (block != null) {
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
            } else {
                accessIndex = -1;
            }
        }
    }
    
    
    public int getAttributeType() {
        if (isState) {
            return AvatarExpressionSolver.IMMEDIATE_BOOL;
        }
        int offset = block.getBooleanOffset();
        int ratio = block.getAttributeOptRatio();
        int attributeIndex = (accessIndex - SpecificationBlock.ATTR_INDEX) * ratio + shift * ratio / 32;
        if (offset == -1 || (attributeIndex < offset)) {
            if (block.getAttribute((accessIndex - SpecificationBlock.ATTR_INDEX)).getType() == AvatarType.BOOLEAN) {
                return AvatarExpressionSolver.IMMEDIATE_BOOL;
            } else {
                return AvatarExpressionSolver.IMMEDIATE_INT;
            }
        } else {
            return AvatarExpressionSolver.IMMEDIATE_BOOL;
        }
    }
    
    
    public boolean isState() {
        return isState;
    }
    
    public String toString() {
        return s;
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

// CHANTIER....
   //Link state to access index in the state machine
    public void linkState() {
        if (isState) {
            if (block != null) {
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
            } else {
                accessIndex = -1;
            }
        }
    }
  
