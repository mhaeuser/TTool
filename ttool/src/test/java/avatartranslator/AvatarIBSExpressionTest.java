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

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import myutil.TraceManager;
import org.junit.Before;
import org.junit.Test;

import avatartranslator.intboolsolver.*;
import myutil.intboolsolver.*;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
public class AvatarIBSExpressionTest {

    private AvatarSpecification as;
    private AvatarBlock block1, block2;

    private AvatarIBSAbsSolver solver = new AvatarIBSAbsSolver();

    public AvatarIBSExpressionTest() {
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
    
    //@Test
    public void testImmediate() {
        TraceManager.addDev("Testing AvatarExpressionTest.testImmediate");
        AvatarIBSAbsSolver.Expr e1 = solver.new Expr("10 + 15 >= 20");
        assertTrue(e1.buildExpression());
        assertTrue(e1.getReturnType() == IBSolver.IMMEDIATE_BOOL);

        AvatarIBSAbsSolver.Expr e1bis = solver.new Expr("not( ( 0>10 ) and true)");
        assertTrue(e1bis.buildExpression());
        assertTrue(e1bis.getReturnType() == IBSolver.IMMEDIATE_BOOL);

        AvatarIBSAbsSolver.Expr e2 = solver.new Expr("-10 / 2 - 15 * 2 + 1 == -30 -4");
        assertTrue(e2.buildExpression());
        assertTrue(e2.getReturnType() == IBSolver.IMMEDIATE_BOOL);

        AvatarIBSAbsSolver.Expr e3 = solver.new Expr("not(-10 / 2 - 15 * 2 + 1 == -(60 - 26))");
        assertTrue(e3.buildExpression());
        assertTrue(e3.getReturnType() == IBSolver.IMMEDIATE_BOOL);

        //TraceManager.addDev("Testing AvatarExpressionTest.testImmediate.e3bis");


        AvatarIBSAbsSolver.Expr e4 = solver.new Expr("1 && 0 >= 1 || 0");
        assertFalse(e4.buildExpression());

        AvatarIBSAbsSolver.Expr e5 = solver.new Expr("true and not(false) == !(false or false)");
        assertTrue(e5.buildExpression());
        assertTrue(e5.getReturnType() == IBSolver.IMMEDIATE_BOOL);

        AvatarIBSAbsSolver.Expr e6 = solver.new Expr("10 -Cabin.match");
        assertFalse(e6.buildExpression());

        AvatarIBSAbsSolver.Expr e7 = solver.new Expr("not(10)");
        assertFalse(e7.buildExpression());

        assertFalse(e7.getReturnType() == IBSolver.IMMEDIATE_BOOL);
        AvatarIBSAbsSolver.Expr e8 = solver.new Expr("-(false)");
        assertFalse(e8.buildExpression());

        AvatarIBSAbsSolver.Expr e9 = solver.new Expr("-10 < 5 && 20/4 == 5");
        assertTrue(e9.buildExpression());
        assertTrue(e9.getReturnType() == IBSolver.IMMEDIATE_BOOL);

        AvatarIBSAbsSolver.Expr e9Bis = solver.new Expr("-10 < (5 && 20)/4 == 5");
        assertFalse(e9Bis.buildExpression());

        AvatarIBSAbsSolver.Expr e10 = solver.new Expr("true && 0 >= 1 || false");
        assertTrue(e10.buildExpression());
        assertEquals(IBSolver.IMMEDIATE_BOOL, e10.getReturnType());

        AvatarIBSAbsSolver.Expr e11 = solver.new Expr("8/2*(2+2)");
        assertTrue(e11.buildExpression());

        AvatarIBSAbsSolver.Expr e12 = solver.new Expr("not(!(not(true)))");
        assertTrue(e12.buildExpression());
        assertEquals(IBSolver.IMMEDIATE_BOOL, e12.getReturnType());

        AvatarIBSAbsSolver.Expr e13 = solver.new Expr("!(not(true))");
        assertTrue(e13.buildExpression());

        AvatarIBSAbsSolver.Expr e13bis = solver.new Expr("!(not(TRUE))");
        assertFalse(e13bis.buildExpression());

        AvatarIBSAbsSolver.Expr e13Ter = solver.new Expr("!(not(FALSE))");
        assertFalse(e13Ter.buildExpression());

        AvatarIBSAbsSolver.Expr e14 = solver.new Expr("3+2");
        assertTrue(e14.buildExpression());
        assertTrue(e14.getReturnType() == IBSolver.IMMEDIATE_INT);

        // Testing extra parenthesis
        AvatarIBSAbsSolver.Expr e15 = solver.new Expr("not((false))");
        assertTrue(e15.buildExpression());
        assertTrue(e15.getReturnType() == IBSolver.IMMEDIATE_BOOL);

        AvatarIBSAbsSolver.Expr e16 = solver.new Expr("(((10 + ((15)))) >= (((20))))");
        assertTrue(e16.buildExpression());
        assertTrue(e16.getReturnType() == IBSolver.IMMEDIATE_BOOL);

        AvatarIBSAbsSolver.Expr e17 = solver.new Expr("((true)) && (((((0 >= 1))))) || not((not(false)))");
        assertTrue(e17.buildExpression());
        assertEquals(IBSolver.IMMEDIATE_BOOL, e17.getReturnType());


        assertEquals(1, e1.getResult());
        assertEquals(1, e2.getResult());
        assertEquals(0, e3.getResult());
        assertEquals(1, e5.getResult());
        assertEquals(1, e9.getResult());
        assertEquals(0, e10.getResult());
        assertEquals(16, e11.getResult());
        assertEquals(0, e12.getResult());
        assertEquals(1, e13.getResult());
        assertEquals(5, e14.getResult());
        assertEquals(1, e15.getResult());
        assertEquals(1, e16.getResult());
        assertEquals(0, e17.getResult());


    }
    
    //@Test
    public void testBlock() {
        SpecificationBlock specBlock = new SpecificationBlock();
        specBlock.init(block1, false, false);
        int[] attributes = {2, 3, 7, 0, 1};
        
        solver.attC.clearAttributes();

        String expr = "x + y";
        assertTrue(solver.indexOfVariable(expr, "x") == 0);
        assertTrue(solver.indexOfVariable(expr, "y") == 4);
        assertTrue(solver.indexOfVariable(expr, "z") == -1);
        assertTrue(solver.indexOfVariable("xx+xxx", "x") == -1);
        assertTrue(solver.indexOfVariable("xx==xxx", "x") == -1);
        assertTrue(solver.indexOfVariable("xx==x", "x") == 4);
        assertTrue(solver.indexOfVariable("x==xx", "x") == 0);
        assertTrue(solver.indexOfVariable("x+1==xx", "x") == 0);

        System.out.println("Solver: " + solver.replaceVariable("x==y", "x", "z"));
        assertTrue(solver.replaceVariable("x==y", "x", "z").equals("z==y"));
        assertTrue(solver.replaceVariable("xx==y", "x", "z").equals("xx==y"));
        assertTrue(solver.replaceVariable("xx==x", "x", "z").equals("xx==z"));
        assertTrue(solver.replaceVariable("(foo==foo1)", "foo", "foo").equals("(foo==foo1)"));
        assertTrue(solver.replaceVariable("(foo==foo1)", "foo", "foo1").equals("(foo1==foo1)"));

        AvatarIBSAbsSolver.Expr e1 = solver.new Expr("x + y");
        assertTrue(e1.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e2 = solver.new Expr("-x / y - 15 * z + 1 == -31");
        assertTrue(solver.attC.containsElementAttribute(block1.getAttribute(0)));
        assertTrue(solver.attC.containsElementAttribute(block1.getAttribute(1)));
        assertFalse(solver.attC.containsElementAttribute(block1.getAttribute(2)));
        assertTrue(e2.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e3 = solver.new Expr("not(-x / z - (x + y) * 2 + 1 >= -(60 - 26))");
        assertTrue(e3.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e4 = solver.new Expr("(key1==true) and (key2==false)");
        assertTrue(e4.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e5 = solver.new Expr("(key1) and (key2)");
        assertTrue(e5.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e6 = solver.new Expr("(key1==key1) or (key2==key1)");
        assertTrue(e6.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e7 = solver.new Expr("((key1==key1) and not(key2==key1)) and (x - y == z + 3)");
        assertTrue(e7.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e8 = solver.new Expr("x + x*(y+z)/(x + z - x)");
        assertTrue(e8.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e9 = solver.new Expr("x + x*(y+z)*(x - z)");
        assertTrue(e9.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e10 = solver.new Expr("x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e10.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e11 = solver.new Expr("x + y");
        assertTrue(e11.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e12 = solver.new Expr("x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e12.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e13 = solver.new Expr("(key1==false) and (key2==true)");
        assertTrue(e13.buildExpression(block1));
        AvatarIBSAbsSolver.Expr e14 = solver.new Expr("x-40<3");
        assertTrue(e14.buildExpression(block1));
        assertTrue(e1.getResult(specBlock) == 15);
        assertTrue(e2.getResult(specBlock) == 1);
        assertTrue(e3.getResult(specBlock) == 0);
        assertTrue(e4.getResult(specBlock) == 1);
        assertTrue(e5.getResult(specBlock) == 0);
        assertTrue(e6.getResult(specBlock) == 1);
        assertTrue(e7.getResult(specBlock) == 1);
        assertTrue(e8.getResult(specBlock) == 45);
        assertTrue(e9.getResult(specBlock) == 570);
        assertTrue(e10.getResult(specBlock) == 36);
        assertTrue(e11.getResult(attributes) == 5);
        assertTrue(e12.getResult(attributes) == 36);
        assertTrue(e13.getResult(attributes) == 1);
        assertTrue(e14.getResult(attributes) == 1);

    }
    
    @Test
    public void testSpec() {
        as.sortAttributes();
        as.setAttributeOptRatio(2);
        SpecificationState ss = new SpecificationState();
        ss.setInit(as, false);
        solver.attC.clearAttributes();

        AvatarIBSAbsSolver.Expr e1 = solver.new Expr("block1.x + block2.y");
        assertTrue(e1.buildExpression(as));

        AvatarIBSAbsSolver.Expr e2 = solver.new Expr("-block1.x / block1.y - 15 * block2.z + 1 == -46");
        assertTrue(solver.attC.containsElementAttribute(block1.getAttribute(0)));
        assertTrue(solver.attC.containsElementAttribute(block2.getAttribute(1)));
        assertFalse(solver.attC.containsElementAttribute(block1.getAttribute(1)));
        assertFalse(solver.attC.containsElementAttribute(block2.getAttribute(0)));
        assertTrue(e2.buildExpression(as));
        assertTrue(solver.attC.containsElementAttribute(block2.getAttribute(2)));
        AvatarIBSAbsSolver.Expr e3 = solver.new Expr("not(-block2.x / block2.z - not(block1.x + block2.y) * -2 + -(1) <= -(-4 + 7))");
        assertFalse(e3.buildExpression(as));
        AvatarIBSAbsSolver.Expr e4 = solver.new Expr("block1.x + block2.w");
        assertTrue(e4.buildExpression(as));
        assertTrue(e1.getResult(ss) == 17);
        assertTrue(e2.getResult(ss) == 1);
//        assertTrue(e3.getResult(ss) == 0);
        assertTrue(e4.getResult(ss) == -2);
        
        as.removeConstants();
        as.sortAttributes();
        as.setAttributeOptRatio(4);
        ss = new SpecificationState();
        ss.setInit(as, false);
        
        e1 = solver.new Expr("block1.x + block2.y");
        assertTrue(e1.buildExpression(as));
        e2 = solver.new Expr("-block1.x / block1.y - 15 * block2.z + 1 == -46");
        assertTrue(e2.buildExpression(as));
        e3 = solver.new Expr("not(-block2.x / block2.z - not(block1.x + block2.y) * -2 + -(1) <= -(-4 + 7))");
        assertFalse(e3.buildExpression(as));
        e4 = solver.new Expr("block1.x + block2.w");
        assertTrue(e4.buildExpression(as));

        assertTrue(e1.getResult(ss) == 17);
        assertTrue(e2.getResult(ss) == 1);
//        assertTrue(e3.getResult(ss) == 0);
        assertTrue(e4.getResult(ss) == -2);
    }
}
