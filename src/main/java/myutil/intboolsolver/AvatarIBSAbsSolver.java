package myutil.intboolsolver;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachineElement;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;

public class AvatarIBSAbsSolver extends IBSolver <
        AvatarSpecification,
        AvatarBlock,
        AvatarStateMachineElement,
        SpecificationState,
        SpecificationBlock,
        AvatarIBSAbsAttribute,
        AvatarIBSAbsAttributeClass
        >{
    AvatarIBSAbsSolver() {
        super(new AvatarIBSAbsAttributeClass());
    }
    void main(String[] args) { return;}
}
