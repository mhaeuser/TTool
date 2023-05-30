/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class AvatarExpressionTest
 * Creation: 29/04/2020
 * @version 1.0 29/04/2020
 * @author Alessandro TEMPIA CALVINO
 * @see
 */
package avatartranslator;
import avatartranslator.intboolsolver.*;

import avatartranslator.intboolsolver.AvatarIBSExpressions;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
import myutil.TraceManager;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;

public class AvatarIBSStdParserTest {

    private AvatarSpecification as;
    private AvatarBlock block1, block2;

    public AvatarIBSStdParserTest() {
    }
    
    @Before
    public void test () {
        as = new AvatarSpecification("avatarspecification", null);

        block1 = new AvatarBlock("block1", as, null);
        as.addBlock(block1);
        AvatarAttribute x1 = new AvatarAttribute("x", AvatarType.INTEGER, block1, null);
        block1.addAttribute(x1);
        AvatarAttribute y1 = new AvatarAttribute("y", AvatarType.INTEGER, block1, null);
        block1.addAttribute(y1);
        AvatarAttribute z1 = new AvatarAttribute("z", AvatarType.INTEGER, block1, null);
        block1.addAttribute(z1);
        AvatarAttribute a1 = new AvatarAttribute("key1", AvatarType.BOOLEAN, block1, null);
        block1.addAttribute(a1);
        AvatarAttribute b1 = new AvatarAttribute("key2", AvatarType.BOOLEAN, block1, null);
        block1.addAttribute(b1);
        
        x1.setInitialValue("10");
        y1.setInitialValue("5");
        z1.setInitialValue("2");
        a1.setInitialValue("true");
        b1.setInitialValue("false");
        
        block2 = new AvatarBlock("block2", as, null);
        as.addBlock(block2);
        AvatarAttribute x2 = new AvatarAttribute("x", AvatarType.INTEGER, block2, null);
        block2.addAttribute(x2);
        AvatarAttribute y2 = new AvatarAttribute("y", AvatarType.INTEGER, block2, null);
        block2.addAttribute(y2);
        AvatarAttribute z2 = new AvatarAttribute("z", AvatarType.INTEGER, block2, null);
        block2.addAttribute(z2);
        AvatarAttribute w2 = new AvatarAttribute("w", AvatarType.INTEGER, block2, null);
        block2.addAttribute(w2);
        
        x2.setInitialValue("9");
        y2.setInitialValue("7");
        z2.setInitialValue("3");
        w2.setInitialValue("-12");
    }
    
    @Test
    public void testImmediate() {
        TraceManager.addDev("Testing AvatarExpressionTest.testImmediate");
        AvatarIBSExpressions.BExpr e1 = AvatarIBSolver.parseBool("10 + 15 >= 20");
        assertTrue(e1!=null);

        AvatarIBSExpressions.BExpr e1bis = AvatarIBSolver.parseBool("not( ( 0>10 ) and true)");
        assertTrue(e1bis!=null);

        AvatarIBSExpressions.BExpr e2 = AvatarIBSolver.parseBool("-10 / 2 - 15 * 2 + 1 == -30 -4");
        assertTrue(e2!=null);
        AvatarIBSExpressions.IExpr a1 =AvatarIBSolver.parseInt("-10 / 2 - 15 * 2 + 1");
        AvatarIBSExpressions.IExpr a2 =AvatarIBSolver.parseInt("-30 -4");

        AvatarIBSExpressions.BExpr e3 = AvatarIBSolver.parseBool("not(-10 / 2 - 15 * 2 + 1 == -(60 - 26))");
        assertTrue(e3!=null);

        //TraceManager.addDev("Testing AvatarExpressionTest.testImmediate.e3bis");

        AvatarIBSExpressions.BExpr e4 = AvatarIBSolver.parseBool("false || -1 >= 0 && true");
        assertTrue(e4!=null);

        AvatarIBSExpressions.BExpr e5 = AvatarIBSolver.parseBool("true and not(false) == !(false or false)");
        assertTrue(e5!=null);

        AvatarIBSExpressions.IExpr e6 = AvatarIBSolver.parseInt("10 -Cabin.match");
        assertFalse(e6!=null);

        AvatarIBSExpressions.BExpr e7 = AvatarIBSolver.parseBool("not(10)");
        assertFalse(e7!=null);

        AvatarIBSExpressions.BExpr e8 = AvatarIBSolver.parseBool("-(false)");
        assertFalse(e8!=null);

        AvatarIBSExpressions.BExpr e9 = AvatarIBSolver.parseBool("-10 < 5 && 20/4 == 5");
        assertTrue(e9!=null);

        AvatarIBSExpressions.IExpr e9Bis = AvatarIBSolver.parseInt("-10 < (5 && 20)/4 == 5");
        assertFalse(e9Bis!=null);

        AvatarIBSExpressions.BExpr e10 = AvatarIBSolver.parseBool("true && 0 >= 1 || false");
        assertTrue(e10!=null);

        AvatarIBSExpressions.IExpr e11 = AvatarIBSolver.parseInt("8/2*(2+2)");
        assertTrue(e11!=null);

        AvatarIBSExpressions.BExpr e12 = AvatarIBSolver.parseBool("not(!(not(true)))");
        assertTrue(e12!=null);

        AvatarIBSExpressions.BExpr e13 = AvatarIBSolver.parseBool("!(not(true))");
        assertTrue(e13!=null);

        AvatarIBSExpressions.BExpr e13bis = AvatarIBSolver.parseBool("!(not(TRUE))");
        assertFalse(e13bis!=null);

        AvatarIBSExpressions.BExpr e13Ter = AvatarIBSolver.parseBool("!(not(FALSE))");
        assertFalse(e13Ter!=null);

        HashSet<String> H = AvatarIBSolver.getBadIdents();
        for(String s:H){System.out.println(s);}
        AvatarIBSolver.clearBadIdents();
        
        AvatarIBSExpressions.IExpr e14 = AvatarIBSolver.parseInt("3+2");
        assertTrue(e14!=null);

        // Testing extra parenthesis
        AvatarIBSExpressions.BExpr e15 = AvatarIBSolver.parseBool("not((false))");
        assertTrue(e15!=null);

        AvatarIBSExpressions.BExpr e16 = AvatarIBSolver.parseBool("(((10 + ((15)))) >= (((20))))");
        assertTrue(e16!=null);

        AvatarIBSExpressions.BExpr e17 = AvatarIBSolver.parseBool("((true)) && (((((0 >= 1))))) || not((not(false)))");
        assertTrue(e17!=null);
         AvatarIBSExpressions.IExpr e18 = AvatarIBSolver.parseInt("2 * -3 + -5 * 4 + 27"); // 1
        assertTrue(e17!=null);
        AvatarIBSExpressions.IExpr e19 = AvatarIBSolver.parseInt("-6 / -2 * 3 - -4 "); // 13
        assertTrue(e17!=null);
       AvatarIBSExpressions.IExpr e20 = AvatarIBSolver.parseInt("-(2 * 3)+ -(1+1) * -3"); // 0
        assertTrue(e17!=null);

        assertEquals(true, e1.eval());
        assertEquals(true, e2.eval());
        assertEquals(false, e3.eval());
        assertEquals(true, e5.eval());
        assertEquals(true, e9.eval());
        assertEquals(false, e10.eval());
        assertEquals(16, e11.eval());
        assertEquals(false, e12.eval());
        assertEquals(true, e13.eval());
        assertEquals(5, e14.eval());
        assertEquals(true, e15.eval());
        assertEquals(true, e16.eval());
        assertEquals(false, e17.eval());
        assertEquals(1, e18.eval());
        assertEquals(13, e19.eval());
        assertEquals(0, e20.eval());

    }
    
    @Test
    public void testBlock() {
        SpecificationBlock specBlock = new SpecificationBlock();
        specBlock.init(block1, false, false);
        int[] attributes = {2, 3, 7, 0, 1};

        AvatarIBSolver.clearAttributes();

        String expr = "x + y";
        assertTrue(AvatarIBSolver.parser.indexOfVariable(expr, "x") == 0);
        assertTrue(AvatarIBSolver.parser.indexOfVariable(expr, "y") == 4);
        assertTrue(AvatarIBSolver.parser.indexOfVariable(expr, "z") == -1);
        assertTrue(AvatarIBSolver.parser.indexOfVariable("xx+xxx", "x") == -1);
        assertTrue(AvatarIBSolver.parser.indexOfVariable("xx==xxx", "x") == -1);
        assertTrue(AvatarIBSolver.parser.indexOfVariable("xx==x", "x") == 4);
        assertTrue(AvatarIBSolver.parser.indexOfVariable("x==xx", "x") == 0);
        assertTrue(AvatarIBSolver.parser.indexOfVariable("x+1==xx", "x") == 0);

        System.out.println("Solver: " + AvatarIBSolver.parser.replaceVariable("x==y", "x", "z"));
        assertTrue(AvatarIBSolver.parser.replaceVariable("x==y", "x", "z").equals("z==y"));
        assertTrue(AvatarIBSolver.parser.replaceVariable("xx==y", "x", "z").equals("xx==y"));
        assertTrue(AvatarIBSolver.parser.replaceVariable("xx==x", "x", "z").equals("xx==z"));
        assertTrue(AvatarIBSolver.parser.replaceVariable("(foo==foo1)", "foo", "foo").equals("(foo==foo1)"));
        assertTrue(AvatarIBSolver.parser.replaceVariable("(foo==foo1)", "foo", "foo1").equals("(foo1==foo1)"));

        AvatarIBSExpressions.IExpr e1 = AvatarIBSolver.parseInt(block1,"x + y");
        assertTrue(e1!=null);
        System.out.println("" + e1 + e1.eval(specBlock));
        assertTrue(AvatarIBSAttributes.containsElementAttribute(block1.getAttribute(0)));
        assertTrue(AvatarIBSAttributes.containsElementAttribute(block1.getAttribute(1)));
        assertFalse(AvatarIBSAttributes.containsElementAttribute(block1.getAttribute(2)));
        AvatarIBSExpressions.BExpr e2 = AvatarIBSolver.parseBool(block1,"-x / y - 15 * z + 1 == -31");
        assertTrue(e2!=null);
        AvatarIBSExpressions.BExpr e3 = AvatarIBSolver.parseBool(block1,"not(-x / z - (x + y) * 2 + 1 >= -(60 - 26))");
        assertTrue(e3!=null);
        AvatarIBSExpressions.BExpr e4 = AvatarIBSolver.parseBool(block1,"(key1==true) and (key2==false)");
        assertTrue(e4!=null);
        AvatarIBSExpressions.BExpr e5 = AvatarIBSolver.parseBool(block1,"(key1) and (key2)");
        assertTrue(e5!=null);
        AvatarIBSExpressions.BExpr e6 = AvatarIBSolver.parseBool(block1,"(key1==key1) or (key2==key1)");
        assertTrue(e6!=null);
        AvatarIBSExpressions.BExpr e7 = AvatarIBSolver.parseBool(block1,"((key1==key1) and not(key2==key1)) and (x - y == z + 3)");
        assertTrue(e7!=null);
        AvatarIBSExpressions.IExpr e8 = AvatarIBSolver.parseInt(block1,"x + x*(y+z)/(x + z - x)");
        assertTrue(e8!=null);
        AvatarIBSExpressions.IExpr e9 = AvatarIBSolver.parseInt(block1,"x + x*(y+z)*(x - z)");
        assertTrue(e9!=null);
        AvatarIBSExpressions.IExpr e10 = AvatarIBSolver.parseInt(block1,"x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e10!=null);
        AvatarIBSExpressions.IExpr e11 = AvatarIBSolver.parseInt(block1,"x + y");
        assertTrue(e11!=null);
        AvatarIBSExpressions.IExpr e12 = AvatarIBSolver.parseInt(block1,"x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e12!=null);
        AvatarIBSExpressions.BExpr e13 = AvatarIBSolver.parseBool(block1,"(key1==false) and (key2==true)");
        assertTrue(e13!=null);
        AvatarIBSExpressions.BExpr e14 = AvatarIBSolver.parseBool(block1,"x-40<3");
        assertTrue(e14!=null);
        assertTrue(e1.eval(specBlock) == 15);
        assertTrue(e2.eval(specBlock));
        assertTrue(!e3.eval(specBlock));
        assertTrue(e4.eval(specBlock));
        assertTrue(!e5.eval(specBlock));
        assertTrue(e6.eval(specBlock));
        assertTrue(e7.eval(specBlock));
        assertTrue(e8.eval(specBlock) == 45);
        assertTrue(e9.eval(specBlock) == 570);
        assertTrue(e10.eval(specBlock) == 36);
        assertTrue(e11.eval(attributes) == 5);
        assertTrue(e12.eval(attributes) == 36);
        assertTrue(e13.eval(attributes));
        assertTrue(e14.eval(attributes));
    }
    
    @Test
    public void testSpec() {
        as.sortAttributes();
        as.setAttributeOptRatio(2);
        SpecificationState ss = new SpecificationState();
        ss.setInit(as, false);
        AvatarIBSolver.clearAttributes();

        AvatarIBSExpressions.IExpr e1 = AvatarIBSolver.parseInt(as, "block1.x + block2.y");
        assertTrue(e1 != null);
        assertTrue(AvatarIBSAttributes.containsElementAttribute(block1.getAttribute(0)));
        assertTrue(AvatarIBSAttributes.containsElementAttribute(block2.getAttribute(1)));
        assertFalse(AvatarIBSAttributes.containsElementAttribute(block1.getAttribute(1)));
        assertFalse(AvatarIBSAttributes.containsElementAttribute(block2.getAttribute(0)));

        AvatarIBSExpressions.BExpr e2 = AvatarIBSolver.parseBool(as, "-block1.x / block1.y - 15 * block2.z + 1 == -46");
        assertTrue(e2 != null);
        assertTrue(AvatarIBSAttributes.containsElementAttribute(block2.getAttribute(2)));
        AvatarIBSExpressions.BExpr e3 = AvatarIBSolver.parseBool(as, "not(-block2.x / block2.z - not(block1.x + block2.y) * -2 + -(1) <= -(-4 + 7))");
        assertFalse(e3 != null);
        AvatarIBSExpressions.IExpr e4 = AvatarIBSolver.parseInt(as, "block1.x + block2.w");

        assertTrue(e4 != null);
        assertTrue(e1.eval(ss) == 17);
        assertTrue(e2.eval(ss));
        assertTrue(e4.eval(ss) == -2);

        // visual test (among others, test toString)
        int i,j;
        String[] str = {
                "block1.key1",
                "block1.key2",
                "block1.x + block2.y > 3",
                "block1.x - block2.y < 5 ",
                "not(block1.x + block2.y<=3)",
                "not(block1.x - block2.y>=5)",
                "block1.key2 == true",
                "(true && block1.key1) == false",
                "true || false == false && true",
                "true && false == false || true",
                "true && false == false && true",
                "true || false == false || true"
      };

        for (i = 0; i < str.length; i++) {
            e2 = AvatarIBSolver.parseBool(as, str[i]);
            assertTrue(e2 != null);
            System.out.println(str[i] + " ---toString---> " + e2 + "  $$  " + e2.eval(ss));
        }
        // end of visual test

        as.removeConstants();
        as.sortAttributes();
        as.setAttributeOptRatio(4);
        ss = new SpecificationState();
        ss.setInit(as, false);
        
        e1 = AvatarIBSolver.parseInt(as,"block1.x + block2.y");
        assertTrue(e1!=null);
        e2 = AvatarIBSolver.parseBool(as,"-block1.x / block1.y - 15 * block2.z + 1 == -46");
        assertTrue(e2!=null);
        e3 = AvatarIBSolver.parseBool(as,"not(-block2.x / block2.z - not(block1.x + block2.y) * -2 + -(1) <= -(-4 + 7))");
        assertFalse(e3!=null);
        e4 = AvatarIBSolver.parseInt(as,"block1.x + block2.w");
        assertTrue(e4!=null);
        assertTrue(e1.eval(ss) == 17);
        assertTrue(e2.eval(ss));
        assertTrue(e4.eval(ss) == -2);

        // PERFORMANCE TEST (VISUAL) =============================
        int OUTER_LOOP = 1000;
        int INNER_LOOP = 10000;

        String s = "(block1.x + block2.y) * 5 - (((block2.x + block1.y)) + block2.w) * ";
        ArrayList<AvatarIBSExpressions.IExpr> arr = new ArrayList<AvatarIBSExpressions.IExpr>();

        long parse_beg =  System.currentTimeMillis();
        for(i=0;i<INNER_LOOP;i++){
            arr.add(AvatarIBSolver.parseInt(as, s+i));}
        long parse_end =  System.currentTimeMillis();

        System.gc();
        int e=0;

        long eval_beg =  System.currentTimeMillis();
        for(j=0;j<OUTER_LOOP;j++) {
            for (i = 0; i < INNER_LOOP; i++) {
                e += arr.get(i).eval(ss);
            }
        }
        long eval_end = System.currentTimeMillis();
        System.out.println("Expression : " + s + "some_integer");
        System.out.println("INNER LOOP : " + INNER_LOOP + ", OUTER_LOOP : " + OUTER_LOOP +
                           "  ( " + OUTER_LOOP + " times " + INNER_LOOP + " expressions )");
        System.out.println("Parsing time : " + (parse_end - parse_beg));
        System.out.println("Evaluation time : " + (eval_end - eval_beg));
    }
    @Test
    public void testExtensions() {
        as.sortAttributes();
        as.setAttributeOptRatio(2);
        SpecificationState ss = new SpecificationState();
        ss.setInit(as, false);
        AvatarIBSolver.clearAttributes();
        AvatarIBSExpressions.IExpr ie1 = AvatarIBSolver.parseInt(as,"block1.x + 3");
        assertTrue(AvatarIBSExpressions.getClassCode(ie1)== AvatarIBSExpressions.classIIIBinOp);
        assertFalse(AvatarIBSExpressions.isBool(ie1));
        assertFalse(AvatarIBSExpressions.isInverted(ie1));
        assertFalse(AvatarIBSExpressions.isConst(ie1));
        assertFalse(AvatarIBSExpressions.isBConst(ie1));
        assertFalse(AvatarIBSExpressions.isIConst(ie1));
        assertFalse(AvatarIBSExpressions.isVariable(ie1));
        assertFalse(AvatarIBSExpressions.isBVariable(ie1));
        assertFalse(AvatarIBSExpressions.isIVariable(ie1));
        assertFalse(AvatarIBSExpressions.isVar(ie1));
        assertFalse(AvatarIBSExpressions.isBVar(ie1));
        assertFalse(AvatarIBSExpressions.isIVar(ie1));
        assertTrue(AvatarIBSExpressions.isBinary(ie1));
        assertFalse(AvatarIBSExpressions.isBBBBinary(ie1));
        assertFalse(AvatarIBSExpressions.isBIIBinary(ie1));
        assertTrue(AvatarIBSExpressions.isIIIBinary(ie1));
        assertFalse(AvatarIBSExpressions.isBBBBinOp(ie1));
        assertFalse(AvatarIBSExpressions.isBIIBinOp(ie1));
        assertTrue(AvatarIBSExpressions.isIIIBinOp(ie1));
        assertTrue(AvatarIBSExpressions.getOpSymbol(ie1)== AvatarIBSExpressions.opPlus);
        AvatarIBSExpressions.IExpr ie2 = AvatarIBSolver.getLeftArg((AvatarIBSExpressions.IIIBinOp)ie1);
        AvatarIBSExpressions.IExpr ie3 = AvatarIBSolver.getRightArg((AvatarIBSExpressions.IIIBinOp)ie1);
        assertFalse(AvatarIBSExpressions.isBool(ie2));
        assertFalse(AvatarIBSExpressions.isInverted(ie2));
        assertFalse(AvatarIBSExpressions.isConst(ie2));
        assertFalse(AvatarIBSExpressions.isBConst(ie2));
        assertFalse(AvatarIBSExpressions.isIConst(ie2));
        assertTrue(AvatarIBSExpressions.isVariable(ie2));
        assertFalse(AvatarIBSExpressions.isBVariable(ie2));
        assertTrue(AvatarIBSExpressions.isIVariable(ie2));
        assertTrue(AvatarIBSExpressions.isVar(ie2));
        assertFalse(AvatarIBSExpressions.isBVar(ie2));
        assertTrue(AvatarIBSExpressions.isIVar(ie2));
        assertFalse(AvatarIBSExpressions.isBinary(ie2));
        assertFalse(AvatarIBSExpressions.isBBBBinary(ie2));
        assertFalse(AvatarIBSExpressions.isBIIBinary(ie2));
        assertFalse(AvatarIBSExpressions.isIIIBinary(ie2));
        assertFalse(AvatarIBSExpressions.isBBBBinOp(ie2));
        assertFalse(AvatarIBSExpressions.isBIIBinOp(ie2));
        assertFalse(AvatarIBSExpressions.isIIIBinOp(ie2));
        assertTrue(AvatarIBSExpressions.getOpSymbol(ie2)==-1);
        assertTrue(AvatarIBSExpressions.getClassCode(ie2)== AvatarIBSExpressions.classIVar);
        assertFalse(AvatarIBSExpressions.isBool(ie3));
        assertFalse(AvatarIBSExpressions.isInverted(ie3));
        assertTrue(AvatarIBSExpressions.isConst(ie3));
        assertFalse(AvatarIBSExpressions.isBConst(ie3));
        assertTrue(AvatarIBSExpressions.isIConst(ie3));
        assertFalse(AvatarIBSExpressions.isVariable(ie3));
        assertFalse(AvatarIBSExpressions.isBVariable(ie3));
        assertFalse(AvatarIBSExpressions.isIVariable(ie3));
        assertFalse(AvatarIBSExpressions.isVar(ie3));
        assertFalse(AvatarIBSExpressions.isBVar(ie3));
        assertFalse(AvatarIBSExpressions.isIVar(ie3));
        assertFalse(AvatarIBSExpressions.isBinary(ie3));
        assertFalse(AvatarIBSExpressions.isBBBBinary(ie3));
        assertFalse(AvatarIBSExpressions.isBIIBinary(ie3));
        assertFalse(AvatarIBSExpressions.isIIIBinary(ie3));
        assertFalse(AvatarIBSExpressions.isBBBBinOp(ie3));
        assertFalse(AvatarIBSExpressions.isBIIBinOp(ie3));
        assertFalse(AvatarIBSExpressions.isIIIBinOp(ie3));
        assertTrue(AvatarIBSExpressions.getOpSymbol(ie3)==-1);
        assertTrue(AvatarIBSExpressions.getClassCode(ie3)== AvatarIBSExpressions.classIConst);
        ie1 = AvatarIBSolver.parseInt(as,"-(block1.x + 3)");
        assertTrue(AvatarIBSExpressions.getClassCode(ie1)== AvatarIBSExpressions.classIIIBinOp);
        assertTrue(AvatarIBSExpressions.getOpSymbol(ie1)== AvatarIBSExpressions.opNeg);
        assertFalse(AvatarIBSExpressions.isBool(ie1));
        assertTrue(AvatarIBSExpressions.isInverted(ie1));
        assertFalse(AvatarIBSExpressions.isConst(ie1));
        assertFalse(AvatarIBSExpressions.isBConst(ie1));
        assertFalse(AvatarIBSExpressions.isIConst(ie1));
        assertFalse(AvatarIBSExpressions.isVariable(ie1));
        assertFalse(AvatarIBSExpressions.isBVariable(ie1));
        assertFalse(AvatarIBSExpressions.isIVariable(ie1));
        assertFalse(AvatarIBSExpressions.isVar(ie1));
        assertFalse(AvatarIBSExpressions.isBVar(ie1));
        assertFalse(AvatarIBSExpressions.isIVar(ie1));
        assertFalse(AvatarIBSExpressions.isBinary(ie1));
        assertFalse(AvatarIBSExpressions.isBBBBinary(ie1));
        assertFalse(AvatarIBSExpressions.isBIIBinary(ie1));
        assertFalse(AvatarIBSExpressions.isIIIBinary(ie1));
        assertFalse(AvatarIBSExpressions.isBBBBinOp(ie1));
        assertFalse(AvatarIBSExpressions.isBIIBinOp(ie1));
        assertTrue(AvatarIBSExpressions.isIIIBinOp(ie1));
        AvatarIBSExpressions.BExpr be1 = AvatarIBSolver.parseGuard(as,"");
        assertTrue(AvatarIBSExpressions.getClassCode(be1)== AvatarIBSExpressions.classBConst);
        assertTrue(AvatarIBSExpressions.getOpSymbol(be1)==-1);
        assertTrue(AvatarIBSExpressions.isBool(be1));
        assertFalse(AvatarIBSExpressions.isInverted(be1));
        assertTrue(AvatarIBSExpressions.isConst(be1));
        assertTrue(AvatarIBSExpressions.isBConst(be1));
        assertFalse(AvatarIBSExpressions.isIConst(be1));
        assertFalse(AvatarIBSExpressions.isVariable(be1));
        assertFalse(AvatarIBSExpressions.isBVariable(be1));
        assertFalse(AvatarIBSExpressions.isIVariable(be1));
        assertFalse(AvatarIBSExpressions.isVar(be1));
        assertFalse(AvatarIBSExpressions.isBVar(be1));
        assertFalse(AvatarIBSExpressions.isIVar(be1));
        assertFalse(AvatarIBSExpressions.isBinary(be1));
        assertFalse(AvatarIBSExpressions.isBBBBinary(be1));
        assertFalse(AvatarIBSExpressions.isBIIBinary(be1));
        assertFalse(AvatarIBSExpressions.isIIIBinary(be1));
        assertFalse(AvatarIBSExpressions.isBBBBinOp(be1));
        assertFalse(AvatarIBSExpressions.isBIIBinOp(be1));
        assertFalse(AvatarIBSExpressions.isIIIBinOp(be1));
    }
}
