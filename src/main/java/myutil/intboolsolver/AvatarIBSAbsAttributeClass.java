package myutil.intboolsolver;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarElement;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachineElement;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;

import java.util.HashMap;
import java.util.Map;

public class AvatarIBSAbsAttributeClass extends IBSAbsAttributeClass<
        AvatarSpecification,
        AvatarBlock,
        AvatarStateMachineElement,
        SpecificationState,
        SpecificationBlock,
        AvatarIBSAbsAttribute
        >  {
    private static Map<AvatarElement, IBSTypedAttribute> attributesMap;
    // handling already covered attributes (memorisation)
    private static AvatarSpecification findSpec = null;
    private static AvatarBlock findComp = null;
    private static String findString = "";
    private static AvatarElement keyElement;

    AvatarIBSAbsAttributeClass(){}

    public IBSTypedAttribute getTypedAttribute(AvatarSpecification _spec, String _s) {
        IBSTypedAttribute a = findAttribute(_spec, _s);
        if (a == null) {
            AvatarIBSAbsAttribute x = new AvatarIBSAbsAttribute(); // replaced...
            x.classInitAttribute(_spec,_s);
            switch (x.getType()) {
                case IBSAttributeTypes.NullAttr:{
                    return IBSTypedAttribute.NullAttribute;
                }
                case IBSAttributeTypes.BoolConst:
                case IBSAttributeTypes.IntConst:{
                    a = new IBSTypedAttribute(x.type,(Object) Integer.valueOf(x.getConstant()));
                    break;
                }
                default: {
                    a = new IBSTypedAttribute(x.type,(Object) x);

                }
            }
            addAttribute(_spec, _s, a);
        }
        return a;
    }

    public IBSTypedAttribute getTypedAttribute(AvatarBlock _comp, String _s) {
        IBSTypedAttribute a = findAttribute(_comp, _s);
        if (a == null) {
            AvatarIBSAbsAttribute x = new AvatarIBSAbsAttribute(); // replaced
            x.classInitAttribute(_comp,_s);
            switch (x.type) {
                case IBSAttributeTypes.NullAttr:{
                    return IBSTypedAttribute.NullAttribute;
                }
                case IBSAttributeTypes.BoolConst:
                case IBSAttributeTypes.IntConst:{
                    a = new IBSTypedAttribute(x.type,(Object) Integer.valueOf(x.getConstant()));
                    break;
                }
                default: {
                    a = new IBSTypedAttribute(x.type,(Object) x);
                }
            }
            addAttribute(_comp, _s, a);
        }
        return a;
    }

    public IBSTypedAttribute getTypedAttribute(AvatarBlock _comp, AvatarStateMachineElement _state) {
        IBSTypedAttribute a = findAttribute(_comp, _state);
        if (a == null) {
            AvatarIBSAbsAttribute x = new AvatarIBSAbsAttribute(); // replaced
            x.classInitAttribute(_comp,_state);
            switch (x.type) {
                case IBSAttributeTypes.NullAttr:{
                    return IBSTypedAttribute.NullAttribute;
                }
                case IBSAttributeTypes.BoolConst:
                case IBSAttributeTypes.IntConst:{
                    a = new IBSTypedAttribute(x.type,(Object) Integer.valueOf(x.getConstant()));
                    break;
                }
                default: {
                    a = new IBSTypedAttribute(x.type,(Object) x);
                }
            }
            addAttribute(_comp, _state, a);
        }
        return a;
    }


    public boolean instanceOfMe(int _type, Object _val) {
        return (_val instanceof AvatarIBSAbsAttribute &&
                _type == ((AvatarIBSAbsAttribute)_val).getType());
    }

    public boolean instanceOfMe(IBSTypedAttribute _ta) {
        Object v = _ta.getVal();
        return (v instanceof AvatarIBSAbsAttribute &&
                _ta.getType() == ((AvatarIBSAbsAttribute)v).getType());
    }


    private void initBuild_internal(){
        if (attributesMap == null) {
            attributesMap = new HashMap<AvatarElement, IBSTypedAttribute>();
        }
    }

    public void initBuild(AvatarSpecification _spec){initBuild_internal();}

    public void initBuild(AvatarBlock _comp){initBuild_internal();}

    public IBSTypedAttribute findAttribute(AvatarSpecification _sp, String _s){
        AvatarSpecification _spec = (AvatarSpecification)_sp;
        AvatarElement ae = getElement(_s, _spec);
        if (ae != null && attributesMap.containsKey(ae)) {
            IBSTypedAttribute ta = attributesMap.get(ae);
            if (ta.isAttribute()){
                //might be uninitialized
                ((AvatarIBSAbsAttribute)(ta.getVal())).linkComp(_spec);
            }
            return ta;
        }
        findString = _s;
        findSpec = _sp;
        keyElement = ae;
        return null;
    }

    public void addAttribute(AvatarSpecification _spec, String _s, IBSTypedAttribute _att){
        AvatarElement ae;
        if ( _s == findString && _spec == findSpec )
            ae = keyElement;
        else
            ae = getElement(_s, (AvatarSpecification)_spec);
        if (ae != null){
            if (_att.isAttribute() && !(_att.getVal() instanceof AvatarIBSAbsAttribute))
                return; // should be an error
            attributesMap.put(ae, _att);
        }
    }

    public IBSTypedAttribute findAttribute(AvatarBlock _cmp, String _s){
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

    public void addAttribute(AvatarBlock _comp, String _s, IBSTypedAttribute _att){
        AvatarElement ae;
        if ( _s == findString && _comp == findComp )
            ae = keyElement;
        else
            ae = getElement(_s, _comp);
        if (ae != null){
            if (_att.isAttribute() && !(_att.getVal() instanceof AvatarIBSAbsAttribute))
                return; // should be an error
            attributesMap.put(ae, _att);
        }
    }

    public IBSTypedAttribute findAttribute(AvatarBlock _comp, AvatarStateMachineElement _st){
        AvatarElement _state = (AvatarElement)_st;
        if (_state != null && attributesMap.containsKey(_state)) {
            IBSTypedAttribute ta = attributesMap.get(_state);
            return ta;
        }
        return null;
    }

    public void addAttribute(AvatarBlock _comp, AvatarStateMachineElement _state, IBSTypedAttribute _att) {
        if (_att == null ||
                (_att.isAttribute() &&
                        (!(instanceOfMe(_att)) ||
                                !((AvatarIBSAbsAttribute) _att.getVal()).isState() ||
                                _state != ((AvatarIBSAbsAttribute) _att.getVal()).state
                        )
                )
        )
            return; // should be an error
        attributesMap.put((AvatarElement) _state, _att);
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
