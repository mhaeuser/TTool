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
        AvatarIBSStdAttribute,
        AvatarIBSStdAttributeClass
        > {
    public AvatarIBSAbsSolver() {
        super(new AvatarIBSStdAttributeClass());
    }
    void main(String[] args) { return;}
}
