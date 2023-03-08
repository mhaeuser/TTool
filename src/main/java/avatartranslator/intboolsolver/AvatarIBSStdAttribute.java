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

package avatartranslator.intboolsolver;
import avatartranslator.*;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
import myutil.intboolsolver.IBSStdAttribute;
import myutil.intboolsolver.IBSAttributeTypes;

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
// - AvatarSpecificationState must extend AvatarIBSAbstractAttribute.SpecificationState
// - AvatarSpecificationBlock must extend AvatarIBSAbstractAttribute.SpecificationBlock 

public class AvatarIBSStdAttribute extends IBSStdAttribute<
        AvatarSpecification,
        AvatarBlock,
        AvatarStateMachineElement,
        SpecificationState,
        SpecificationBlock
        > {

    // attribute access information
    private AvatarBlock block;
    private int blockIndex; // Similar to the one of the block "block".
                            // For performance reason
    private int accessIndex;
    private int shift;
    private int mask;

    protected int initAttribute(AvatarSpecification _spec) {

        //Extract Block and Attribute
        String[] splitS;
        String blockString;
        String fieldString;
        
        if (_spec == null) {
            return IBSAttributeTypes.NullAttr;
        }
        
        if (s.matches(".+\\..+")) {
            splitS = s.split("\\.");
            blockString = splitS[0];
            fieldString = splitS[1];
        } else {
            return IBSAttributeTypes.NullAttr;
        }
        
        block = _spec.getBlockWithName(blockString);

        if (block == null) {
            return IBSAttributeTypes.NullAttr;
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
                    return IBSAttributeTypes.NullAttr;
                }
                isState = true;
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
		return IBSAttributeTypes.BoolAttr;
            } else { //constant
                accessIndex = attributeIndex;
                AvatarAttribute attr = block.getConstantWithIndex(accessIndex);
		constantInt = attr.getInitialValueInInt();
                return (attr.isBool() ? IBSAttributeTypes.BoolConst : IBSAttributeTypes.IntConst);
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
    
    
    protected int initAttribute(AvatarBlock _block) {

        //Extract Attribute
        if (_block == null) {
            return IBSAttributeTypes.NullAttr;
        }
        
        this.block = _block;
        this.blockIndex = -1; //not initialized
        
        int attributeIndex = block.getIndexOfAvatarAttributeWithName(s);
        
        shift = 0;
        mask = 0xFFFFFFFF;
        
        if (attributeIndex == -1) {
            attributeIndex = _block.getIndexOfConstantWithName(s);
            if (attributeIndex == -1) {
                // state?
                state = block.getStateMachine().getStateWithName(s);
                if (state == null) {
                    return IBSAttributeTypes.NullAttr;
                }
                isState = true;
                accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
                return IBSAttributeTypes.BoolAttr;
            } else { //constant
                accessIndex = attributeIndex;
                AvatarAttribute attr = block.getConstantWithIndex(accessIndex);
		constantInt = attr.getInitialValueInInt();
                return (attr.isBool() ? IBSAttributeTypes.BoolConst : IBSAttributeTypes.IntConst);
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

    protected int initStateAttribute(AvatarBlock _comp) {
        accessIndex = -1;
        shift = 0;
        mask = 0xFFFFFFFF;
        block = null;
	return IBSAttributeTypes.BoolAttr;
    }
    
    private int attributeType() {
        if (isState) { // should not happen
            return IBSAttributeTypes.BoolAttr;
        }
        int offset = block.getBooleanOffset();
        int ratio = block.getAttributeOptRatio();
        int attributeIndex = (accessIndex - SpecificationBlock.ATTR_INDEX) * ratio + shift * ratio / 32;
        if (offset == -1 || (attributeIndex < offset)) {
            if (block.getAttribute((accessIndex - SpecificationBlock.ATTR_INDEX)).getType()
		== AvatarType.BOOLEAN) {
                return IBSAttributeTypes.BoolAttr;
            } else {
                return IBSAttributeTypes.IntAttr;
            }
        } else {
            return IBSAttributeTypes.BoolAttr;
        }
    }



    public String getStateName(AvatarBlock _comp, AvatarStateMachineElement _state){
	return (_state).getName();
    }

    public int getValue(SpecificationState _ss) {
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
    
    
    public int getValue(SpecificationBlock  _sb) {
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
    
    public int getValue(SpecificationState _ss, AvatarStateMachineElement _asme) {
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

    public void setValue(SpecificationState _ss, int _value) {
        if (isState) {
            return;
        }
        
        _ss.blocks[blockIndex].values[accessIndex] = (_ss.blocks[blockIndex].values[accessIndex] & (~(mask << shift))) | ((_value & mask) << shift);
    }
    
    public void setValue(SpecificationBlock  _sb, int _value) {
        if (isState) {
            return;
        }
        _sb.values[accessIndex] = (_sb.values[accessIndex] & (~(mask << shift))) | ((_value & mask) << shift);
    }
    
    public void linkComp(AvatarSpecification spec) {
        if (blockIndex == -1) {
            //initialize it
            blockIndex = spec.getBlockIndex(block);
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

}
