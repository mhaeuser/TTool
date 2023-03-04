package myutil.intboolsolver;

class IBSTypedAttribute {
    private Object val = null;
    private int type = IBSAttributeTypes.NullAttr;
    public static final IBSTypedAttribute NullAttribute =
            new IBSTypedAttribute(IBSAttributeTypes.NullAttr,null);

    private IBSTypedAttribute() {
    }

    public IBSTypedAttribute(int _type, Object _val) {
        type = _type;
        val = _val;
        if ((val == null && type != IBSAttributeTypes.NullAttr) ||
                (type == IBSAttributeTypes.IntConst && !(val instanceof Integer)) ||
                (type == IBSAttributeTypes.BoolConst && !(val instanceof Integer)) )
            ; // error case
    }
	public int getType(){return type;}
	public Object getVal(){return val;}
	public boolean isAttribute(){return (type >= IBSAttributeTypes.BoolAttr);}
}
