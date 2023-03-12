package avatartranslator.intboolsolver;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarElement;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachineElement;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
import myutil.intboolsolver.IBSStdAttributeClass;
import myutil.intboolsolver.IBSAttributeTypes;
import myutil.intboolsolver.IBSTypedAttribute;

import java.util.HashMap;
import java.util.Map;

public class AvatarIBSStdAttributeClass extends IBSStdAttributeClass<
        AvatarSpecification,
        AvatarBlock,
        AvatarStateMachineElement,
        SpecificationState,
        SpecificationBlock,
        AvatarIBSStdAttribute
        > {
    private static Map<AvatarElement, IBSTypedAttribute> attributesMap;
    // handling already covered attributes (memorisation)
    private static AvatarSpecification findSpec = null;
    private static AvatarBlock findComp = null;
    private static String findString = "";
    private static AvatarElement keyElement;

    AvatarIBSStdAttributeClass(){}

    public AvatarIBSStdAttribute getNewAttribute() { return new AvatarIBSStdAttribute(); }


    public boolean instanceOfMe(int _type, Object _val) {
        return (_val instanceof AvatarIBSStdAttribute &&
                _type == ((AvatarIBSStdAttribute)_val).getType());
    }

    public boolean instanceOfMe(IBSTypedAttribute _ta) {
        Object v = _ta.getVal();
        return (v instanceof AvatarIBSStdAttribute &&
                _ta.getType() == ((AvatarIBSStdAttribute)v).getType());
    }


    public void initBuild(){
        if (attributesMap == null) {
            attributesMap = new HashMap<AvatarElement, IBSTypedAttribute>();
        }
    }

    public void initBuild(AvatarSpecification _spec){ initBuild(); }

    public void initBuild(AvatarBlock _comp){ initBuild(); }

    public IBSTypedAttribute findAttribute(AvatarSpecification _spec, String _s){
        AvatarElement ae = getElement(_s, _spec);
        if (ae != null && attributesMap.containsKey(ae)) {
            IBSTypedAttribute ta = attributesMap.get(ae);
            if (ta.isAttribute()){
                //might be uninitialized
                ((AvatarIBSStdAttribute)(ta.getVal())).linkComp(_spec);
            }
            return ta;
        }
        findString = _s;
        findSpec = _spec;
        keyElement = ae;
        return null;
    }

    public void addAttribute(AvatarSpecification _spec, String _s, IBSTypedAttribute _att){
        AvatarElement ae;
        if ( _s == findString && _spec == findSpec )
            ae = keyElement;
        else
            ae = getElement(_s, _spec);
        if (ae != null){
            if (_att.isAttribute() && !(_att.getVal() instanceof AvatarIBSStdAttribute))
                return; // should be an error
            attributesMap.put(ae, _att);
        }
    }

    public IBSTypedAttribute findAttribute(AvatarBlock _cmp, String _s){
        AvatarBlock _comp = _cmp;
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
            if (_att.isAttribute() && !(_att.getVal() instanceof AvatarIBSStdAttribute))
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
                                !((AvatarIBSStdAttribute) _att.getVal()).isState() ||
                                _state != ((AvatarIBSStdAttribute) _att.getVal()).state
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

    /* uniquement pour test */

    public static boolean containsElementAttribute(AvatarElement ae) {
        if (attributesMap != null) {
            return attributesMap.containsKey(ae);
        } else {
            return false;
        }
    }

    public static IBSTypedAttribute getElementAttribute(AvatarElement ae) {
        if (attributesMap != null) {
            return attributesMap.get(ae);
        } else {
            return null;
        }
    }


    public static void addElementAttribute(AvatarElement ae, IBSTypedAttribute aexa) {
        if (attributesMap == null) {
            attributesMap = new HashMap<AvatarElement, IBSTypedAttribute>();
        }
        attributesMap.put(ae, aexa);
    }
     public void clearAttributes(){
        attributesMap = new HashMap<AvatarElement, IBSTypedAttribute>();
    }
}
