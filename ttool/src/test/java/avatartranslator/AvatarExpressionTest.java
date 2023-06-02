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

import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;

import java.util.ArrayList;

public class AvatarExpressionTest {
    
    private AvatarSpecification as;
    private AvatarBlock block1, block2;
    
    public AvatarExpressionTest() {
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
        AvatarExpressionSolver e1 = new AvatarExpressionSolver("10 + 15 >= 20");
        assertTrue(e1.buildExpression());
        assertTrue(e1.getReturnType() == AvatarExpressionSolver.IMMEDIATE_BOOL);

        AvatarExpressionSolver e1bis = new AvatarExpressionSolver("not( true)");//"not( ( 0>10 ) and true)");
        assertTrue(e1bis.buildExpression());
        assertTrue(e1bis.getReturnType() == AvatarExpressionSolver.IMMEDIATE_BOOL);

        AvatarExpressionSolver e2 = new AvatarExpressionSolver("-10 / 2 - 15 * 2 + 1 == -30 -4");
        assertTrue(e2.buildExpression());
        assertTrue(e2.getReturnType() == AvatarExpressionSolver.IMMEDIATE_BOOL);

        AvatarExpressionSolver e3 = new AvatarExpressionSolver("not(-10 / 2 - 15 * 2 + 1 == -(60 - 26))");
        assertTrue(e3.buildExpression());
        assertTrue(e3.getReturnType() == AvatarExpressionSolver.IMMEDIATE_BOOL);

        //TraceManager.addDev("Testing AvatarExpressionTest.testImmediate.e3bis");


        AvatarExpressionSolver e4 = new AvatarExpressionSolver("1 && 0 >= 1 || 0");
        assertFalse(e4.buildExpression());

        AvatarExpressionSolver e5 = new AvatarExpressionSolver("true and not(false) == !(false or false)");
        assertTrue(e5.buildExpression());
        assertTrue(e5.getReturnType() == AvatarExpressionSolver.IMMEDIATE_BOOL);

        AvatarExpressionSolver e6 = new AvatarExpressionSolver("10 -Cabin.match");
        assertFalse(e6.buildExpression());

        AvatarExpressionSolver e7 = new AvatarExpressionSolver("not(10)");
        assertFalse(e7.buildExpression());

        assertFalse(e7.getReturnType() == AvatarExpressionSolver.IMMEDIATE_BOOL);
        AvatarExpressionSolver e8 = new AvatarExpressionSolver("-(false)");
        assertFalse(e8.buildExpression());

        AvatarExpressionSolver e9 = new AvatarExpressionSolver("-10 < 5 && 20/4 == 5");
        assertTrue(e9.buildExpression());
        assertTrue(e9.getReturnType() == AvatarExpressionSolver.IMMEDIATE_BOOL);

        AvatarExpressionSolver e9Bis = new AvatarExpressionSolver("-10 < (5 && 20)/4 == 5");
        assertFalse(e9Bis.buildExpression());

        AvatarExpressionSolver e10 = new AvatarExpressionSolver("true && 0 >= 1 || false");
        assertTrue(e10.buildExpression());
        assertEquals(AvatarExpressionSolver.IMMEDIATE_BOOL, e10.getReturnType());

        AvatarExpressionSolver e11 = new AvatarExpressionSolver("8/2*(2+2)");
        assertTrue(e11.buildExpression());

        AvatarExpressionSolver e12 = new AvatarExpressionSolver("not(!(not(true)))");
        assertTrue(e12.buildExpression());
        assertEquals(AvatarExpressionSolver.IMMEDIATE_BOOL, e12.getReturnType());

        AvatarExpressionSolver e13 = new AvatarExpressionSolver("!(not(true))");
        assertTrue(e13.buildExpression());

        AvatarExpressionSolver e13bis = new AvatarExpressionSolver("!(not(TRUE))");
        assertFalse(e13bis.buildExpression());

        AvatarExpressionSolver e13Ter = new AvatarExpressionSolver("!(not(FALSE))");
        assertFalse(e13Ter.buildExpression());

        AvatarExpressionSolver e14 = new AvatarExpressionSolver("3+2");
        assertTrue(e14.buildExpression());
        assertTrue(e14.getReturnType() == AvatarExpressionSolver.IMMEDIATE_INT);

        // Testing extra parenthesis
        AvatarExpressionSolver e15 = new AvatarExpressionSolver("not((false))");
        assertTrue(e15.buildExpression());
        assertTrue(e15.getReturnType() == AvatarExpressionSolver.IMMEDIATE_BOOL);

        AvatarExpressionSolver e16 = new AvatarExpressionSolver("(((10 + ((15)))) >= (((20))))");
        assertTrue(e16.buildExpression());
        assertTrue(e16.getReturnType() == AvatarExpressionSolver.IMMEDIATE_BOOL);

        AvatarExpressionSolver e17 = new AvatarExpressionSolver("((true)) && (((((0 >= 1))))) || not((not(false)))");
        assertTrue(e17.buildExpression());
        assertEquals(AvatarExpressionSolver.IMMEDIATE_BOOL, e17.getReturnType());


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
        
        AvatarExpressionSolver.emptyAttributesMap();

        String expr = "x + y";
        assertTrue(AvatarExpressionSolver.indexOfVariable(expr, "x") == 0);
        assertTrue(AvatarExpressionSolver.indexOfVariable(expr, "y") == 4);
        assertTrue(AvatarExpressionSolver.indexOfVariable(expr, "z") == -1);
        assertTrue(AvatarExpressionSolver.indexOfVariable("xx+xxx", "x") == -1);
        assertTrue(AvatarExpressionSolver.indexOfVariable("xx==xxx", "x") == -1);
        assertTrue(AvatarExpressionSolver.indexOfVariable("xx==x", "x") == 4);
        assertTrue(AvatarExpressionSolver.indexOfVariable("x==xx", "x") == 0);
        assertTrue(AvatarExpressionSolver.indexOfVariable("x+1==xx", "x") == 0);

        System.out.println("Solver: " + AvatarExpressionSolver.replaceVariable("x==y", "x", "z"));
        assertTrue(AvatarExpressionSolver.replaceVariable("x==y", "x", "z").equals("z==y"));
        assertTrue(AvatarExpressionSolver.replaceVariable("xx==y", "x", "z").equals("xx==y"));
        assertTrue(AvatarExpressionSolver.replaceVariable("xx==x", "x", "z").equals("xx==z"));
        assertTrue(AvatarExpressionSolver.replaceVariable("(foo==foo1)", "foo", "foo").equals("(foo==foo1)"));
        assertTrue(AvatarExpressionSolver.replaceVariable("(foo==foo1)", "foo", "foo1").equals("(foo1==foo1)"));

        AvatarExpressionSolver e1 = new AvatarExpressionSolver("x + y");
        assertTrue(e1.buildExpression(block1));
        AvatarExpressionSolver e2 = new AvatarExpressionSolver("-x / y - 15 * z + 1 == -31");
        assertTrue(AvatarExpressionSolver.containsElementAttribute(block1.getAttribute(0)));
        assertTrue(AvatarExpressionSolver.containsElementAttribute(block1.getAttribute(1)));
        assertFalse(AvatarExpressionSolver.containsElementAttribute(block1.getAttribute(2)));
        assertTrue(e2.buildExpression(block1));
        AvatarExpressionSolver e3 = new AvatarExpressionSolver("not(-x / z - (x + y) * 2 + 1 >= -(60 - 26))");
        assertTrue(e3.buildExpression(block1));
        AvatarExpressionSolver e4 = new AvatarExpressionSolver("(key1==true) and (key2==false)");
        assertTrue(e4.buildExpression(block1));
        AvatarExpressionSolver e5 = new AvatarExpressionSolver("(key1) and (key2)");
        assertTrue(e5.buildExpression(block1));
        AvatarExpressionSolver e6 = new AvatarExpressionSolver("(key1==key1) or (key2==key1)");
        assertTrue(e6.buildExpression(block1));
        AvatarExpressionSolver e7 = new AvatarExpressionSolver("((key1==key1) and not(key2==key1)) and (x - y == z + 3)");
        assertTrue(e7.buildExpression(block1));
        AvatarExpressionSolver e8 = new AvatarExpressionSolver("x + x*(y+z)/(x + z - x)");
        assertTrue(e8.buildExpression(block1));
        AvatarExpressionSolver e9 = new AvatarExpressionSolver("x + x*(y+z)*(x - z)");
        assertTrue(e9.buildExpression(block1));
        AvatarExpressionSolver e10 = new AvatarExpressionSolver("x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e10.buildExpression(block1));
        AvatarExpressionSolver e11 = new AvatarExpressionSolver("x + y");
        assertTrue(e11.buildExpression(block1));
        AvatarExpressionSolver e12 = new AvatarExpressionSolver("x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e12.buildExpression(block1));
        AvatarExpressionSolver e13 = new AvatarExpressionSolver("(key1==false) and (key2==true)");
        assertTrue(e13.buildExpression(block1));
        AvatarExpressionSolver e14 = new AvatarExpressionSolver("x-40<3");
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
        AvatarExpressionSolver.emptyAttributesMap();

        AvatarExpressionSolver e1 = new AvatarExpressionSolver("block1.x + block2.y");
        assertTrue(e1.buildExpression(as));

        AvatarExpressionSolver e2 = new AvatarExpressionSolver("-block1.x / block1.y - 15 * block2.z + 1 == -46");
        assertTrue(AvatarExpressionSolver.containsElementAttribute(block1.getAttribute(0)));
        assertTrue(AvatarExpressionSolver.containsElementAttribute(block2.getAttribute(1)));
        assertFalse(AvatarExpressionSolver.containsElementAttribute(block1.getAttribute(1)));
        assertFalse(AvatarExpressionSolver.containsElementAttribute(block2.getAttribute(0)));
        assertTrue(e2.buildExpression(as));
        assertTrue(AvatarExpressionSolver.containsElementAttribute(block2.getAttribute(2)));
        AvatarExpressionSolver e3 = new AvatarExpressionSolver("not(-block2.x / block2.z - not(block1.x + block2.y) * -2 + -(1) <= -(-4 + 7))");
        assertFalse(e3.buildExpression(as));
        AvatarExpressionSolver e4 = new AvatarExpressionSolver("block1.x + block2.w");
        assertTrue(e4.buildExpression(as));
        assertTrue(e1.getResult(ss) == 17);
        assertTrue(e2.getResult(ss) == 1);
        assertTrue(e3.getResult(ss) == 0);

/* BUG uncomment --------------------------
        AvatarExpressionSolver bug1 = new AvatarExpressionSolver("3 + -2 * 2");
        AvatarExpressionSolver bug2 = new AvatarExpressionSolver("3 - +2 * 2");
        AvatarExpressionSolver bug3 = new AvatarExpressionSolver("--2");
        System.out.println("bug1 (3 + -2 * 2) building : " + bug1.buildExpression(as));
        System.out.println("bug1 getResult : " + bug1.getResult(ss));
        System.out.println("\nbug2 (3 - +2 * 2) building : " + bug2.buildExpression(as));
        System.out.println("bug2 getResult : " + bug2.getResult(ss));
        System.out.println("\nbug3 (--2) building : " + bug3.buildExpression(as));
        System.out.println("bug3 getResult : " + bug3.getResult(ss));
*/

        assertTrue(e4.getResult(ss) == -2);
        
        as.removeConstants();
        as.sortAttributes();
        as.setAttributeOptRatio(4);
        ss = new SpecificationState();
        ss.setInit(as, false);
        
        e1 = new AvatarExpressionSolver("block1.x + block2.y");
        assertTrue(e1.buildExpression(as));
        e2 = new AvatarExpressionSolver("-block1.x / block1.y - 15 * block2.z + 1 == -46");
        assertTrue(e2.buildExpression(as));
        e3 = new AvatarExpressionSolver("not(-block2.x / block2.z - not(block1.x + block2.y) * -2 + -(1) <= -(-4 + 7))");
        assertFalse(e3.buildExpression(as));
        e4 = new AvatarExpressionSolver("block1.x + block2.w");
        assertTrue(e4.buildExpression(as));

        assertTrue(e1.getResult(ss) == 17);
        assertTrue(e2.getResult(ss) == 1);
        assertTrue(e3.getResult(ss) == 0);
        assertTrue(e4.getResult(ss) == -2);

        // PERFORMANCE TEST
        AvatarExpressionSolver E = new AvatarExpressionSolver("(block1.x + block2.y) * 5 - (((block1.x + block2.y)) + block2.y) * 3");
        assertTrue(E.buildExpression(as));


        int i,j;
        String s = "(block1.x + block2.y) * 5 - (((block1.x + block2.y)) + block2.y) * ";
        ArrayList<AvatarExpressionSolver> arr = new ArrayList<AvatarExpressionSolver>();

        long t1 =  System.currentTimeMillis();
            for(i=0;i<10000;i++){
                E = new AvatarExpressionSolver(s+i);
                E.buildExpression(as);
            arr.add(E);
            }

        long t2 =  System.currentTimeMillis();
        int e=2;
        for(j=0;j<100;j++) {
            for(i=0;i<10000;i++) {
                e += arr.get(i).getResult(ss);
            }
        }
        long t3 = System.currentTimeMillis();
        System.out.println(arr.get(5));
        System.out.println(" Durations, parsing: " + (t2 - t1) + " , evaluation: " + (t3 - t2));

    }
}
