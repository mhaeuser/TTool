package avatartranslator.intboolsolver;

public class AvatarIBSolver {
    public static AvatarIBSAttributeClass attrC = new AvatarIBSAttributeClass();
    protected static AvatarIBSExpressionClass exprC = new AvatarIBSExpressionClass();

    public static AvatarIBSStdParser parser = new AvatarIBSStdParser(attrC,exprC);
}
