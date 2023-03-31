package avatartranslator.intboolsolver;

import avatartranslator.*;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
import myutil.intboolsolver.IBSStdAttributeClass;

import java.util.HashMap;
import java.util.Map;

public class AvatarIBSAttributeClass extends IBSStdAttributeClass<
        AvatarSpecification,
        AvatarBlock,
        AvatarStateMachineElement,
        SpecificationState,
        SpecificationBlock
        > {
    private static Map<AvatarElement, TypedAttribute> attributesMap;
    // handling already covered attributes (memorisation)
    private static AvatarSpecification findSpec = null;
    private static AvatarBlock findComp = null;
    private static String findString = "";
    private static AvatarElement keyElement;
    public AvatarIBSAttributeClass(){
        attributesMap = new HashMap<AvatarElement, TypedAttribute>();
    }
    public TypedAttribute findAttribute(AvatarSpecification _spec, String _s){
        AvatarElement ae = getElement(_s, _spec);
        if (ae != null && attributesMap.containsKey(ae)) {
            TypedAttribute ta = attributesMap.get(ae);
            if (ta.isAttribute()){
                //might be uninitialized
                ((Attribute)(ta.getAttribute())).linkComp(_spec);
            }
            return ta;
        }
        findString = _s;
        findSpec = _spec;
        keyElement = ae;
        return null;
    }
    public void addAttribute(AvatarSpecification _spec, String _s, TypedAttribute _att){
        AvatarElement ae;
        if ( _s == findString && _spec == findSpec )
            ae = keyElement;
        else
            ae = getElement(_s, _spec);
        if (ae != null){
            if (_att.isAttribute() && !(_att.getAttribute() instanceof Attribute))
                return; // should be an error
            attributesMap.put(ae, _att);
        }
    }

    public TypedAttribute findAttribute(AvatarBlock _cmp, String _s){
        AvatarBlock _comp = _cmp;
        AvatarElement ae = getElement(_s, _comp);
        if (ae != null && attributesMap.containsKey(ae)) {
            TypedAttribute ta = attributesMap.get(ae);
            return ta;
        }
        findString = _s;
        findComp = _cmp;
        keyElement = ae;
        return null;
    }

    public void addAttribute(AvatarBlock _comp, String _s, TypedAttribute _att){
        AvatarElement ae;
        if ( _s == findString && _comp == findComp )
            ae = keyElement;
        else
            ae = getElement(_s, _comp);
        if (ae != null){
            if (_att.isAttribute() && !(_att.getAttribute() instanceof Attribute))
                return; // should be an error
            attributesMap.put(ae, _att);
        }
    }

    public TypedAttribute findAttribute(AvatarBlock _comp, AvatarStateMachineElement _st){
        AvatarElement _state = (AvatarElement)_st;
        if (_state != null && attributesMap.containsKey(_state)) {
            TypedAttribute ta = attributesMap.get(_state);
            return ta;
        }
        return null;
    }

    public void addAttribute(AvatarBlock _comp, AvatarStateMachineElement _state, TypedAttribute _att) {
        if (_att == null ||
                (_att.isAttribute() &&
                        (!(_att.getAttribute() instanceof Attribute) ||
                                !( _att.getAttribute()).isState() ||
                                _state != ( (Attribute)_att.getAttribute()).state
                        )
                )
        )
            return; // should be an error
        attributesMap.put((AvatarElement) _state, _att);
    }
    protected Attribute getAttribute() {return new Attribute();}
    //@Override
    public String getStateName(AvatarBlock _comp, AvatarStateMachineElement _state)
    {
        return _state.getName();
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
    public class Attribute extends IBSStdAttributeClass<
            AvatarSpecification,
            AvatarBlock,
            AvatarStateMachineElement,
            SpecificationState,
            SpecificationBlock
            >.Attribute implements AvatarExpressionAttributeInterface {

        // attribute access information
        private AvatarBlock block;
        private int blockIndex; // Similar to the one of the block "block".
        // For performance reason
        private int accessIndex;
        private int shift;
        private int mask;
        @Override
        protected int initAttribute(AvatarSpecification _spec) {

            //Extract Block and Attribute
            String[] splitS;
            String blockString;
            String fieldString;

            if (_spec == null) {
                return NullAttr;
            }

            if (s.matches(".+\\..+")) {
                splitS = s.split("\\.");
                blockString = splitS[0];
                fieldString = splitS[1];
            } else {
                return NullAttr;
            }

            block = _spec.getBlockWithName(blockString);

            if (block == null) {
                return NullAttr;
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
                        return NullAttr;
                    }
                    isState = true;
                    accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
                    return BoolAttr;
                } else { //constant
                    accessIndex = attributeIndex;
                    AvatarAttribute attr = block.getConstantWithIndex(accessIndex);
                    constantInt = attr.getInitialValueInInt();
                    return (attr.isBool() ? BoolConst : IntConst);
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
                return NullAttr;
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
                        return NullAttr;
                    }
                    isState = true;
                    accessIndex = block.getStateMachine().getIndexOfState((AvatarStateElement) state);
                    return BoolAttr;
                } else { //constant
                    accessIndex = attributeIndex;
                    AvatarAttribute attr = block.getConstantWithIndex(accessIndex);
                    constantInt = attr.getInitialValueInInt();
                    return (attr.isBool() ? BoolConst : IntConst);
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
            return BoolAttr;
        }

        private int attributeType() {
            if (isState) { // should not happen
                return BoolAttr;
            }
            int offset = block.getBooleanOffset();
            int ratio = block.getAttributeOptRatio();
            int attributeIndex = (accessIndex - SpecificationBlock.ATTR_INDEX) * ratio + shift * ratio / 32;
            if (offset == -1 || (attributeIndex < offset)) {
                if (block.getAttribute((accessIndex - SpecificationBlock.ATTR_INDEX)).getType()
                        == AvatarType.BOOLEAN) {
                    return BoolAttr;
                } else {
                    return IntAttr;
                }
            } else {
                return BoolAttr;
            }
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
        public int getValue(int[] _quickstate) { return getValue((Object) _quickstate);}

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
    /*===============================================================*/
    /* uniquement pour test */

    public static boolean containsElementAttribute(AvatarElement ae) {
        if (attributesMap != null) {
            return attributesMap.containsKey(ae);
        } else {
            return false;
        }
    }
    public static TypedAttribute getElementAttribute(AvatarElement ae) {
        if (attributesMap != null) {
            return attributesMap.get(ae);
        } else {
            return null;
        }
    }
    public static void addElementAttribute(AvatarElement ae, TypedAttribute aexa) {
        if (attributesMap == null) {
            attributesMap = new HashMap<AvatarElement, TypedAttribute>();
        }
        attributesMap.put(ae, aexa);
    }
     public void clearAttributes(){
        attributesMap = new HashMap<AvatarElement, TypedAttribute>();
    }
}
