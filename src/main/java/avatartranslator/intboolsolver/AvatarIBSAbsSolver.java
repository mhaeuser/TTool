package avatartranslator.intboolsolver;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachineElement;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
import myutil.intboolsolver.IBSolver;

public class AvatarIBSAbsSolver extends IBSolver<
        AvatarSpecification,
        AvatarBlock,
        AvatarStateMachineElement,
        SpecificationState,
        SpecificationBlock,
        AvatarIBSAbsAttribute,
        AvatarIBSAbsAttributeClass
        > {
    public AvatarIBSAbsSolver() {
        super(new AvatarIBSAbsAttributeClass());
    }
    void main(String[] args) { return;}
}
