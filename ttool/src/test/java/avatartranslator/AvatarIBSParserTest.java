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
import avatartranslator.intboolsolver2.AvatarIBSOriginExpressionClass;
import avatartranslator.intboolsolver2.AvatarIBSOriginParser;
import avatartranslator.intboolsolver2.AvatarIBSStdAttributeClass;
import avatartranslator.modelchecker.SpecificationBlock;
import avatartranslator.modelchecker.SpecificationState;
import myutil.TraceManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AvatarIBSParserTest {

    private AvatarSpecification as;
    private AvatarBlock block1, block2;
    private AvatarIBSStdAttributeClass attC = new AvatarIBSStdAttributeClass();
    private AvatarIBSOriginExpressionClass expr = new AvatarIBSOriginExpressionClass();

    private AvatarIBSOriginParser parser = new AvatarIBSOriginParser(attC,expr);
    public AvatarIBSParserTest() {
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
        AvatarIBSOriginExpressionClass.BExpr e1 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("10 + 15 >= 20");
        assertTrue(e1!=null);

        AvatarIBSOriginExpressionClass.BExpr e1bis = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("not( ( 0>10 ) and true)");
        assertTrue(e1bis!=null);

        AvatarIBSOriginExpressionClass.BExpr e2 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("-10 / 2 - 15 * 2 + 1 == -30 -4");
        assertTrue(e2!=null);

        AvatarIBSOriginExpressionClass.BExpr e3 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("not(-10 / 2 - 15 * 2 + 1 == -(60 - 26))");
        assertTrue(e3!=null);

        //TraceManager.addDev("Testing AvatarExpressionTest.testImmediate.e3bis");
        
        AvatarIBSOriginExpressionClass.BExpr e4 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("0 || -1 >= 0 && 1");
        assertTrue(e4!=null);
        System.out.println(" TEST " + e4.eval());

        AvatarIBSOriginExpressionClass.BExpr e5 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("true and not(false) == !(false or false)");
        assertTrue(e5!=null);

        AvatarIBSOriginExpressionClass.IExpr e6 = (AvatarIBSOriginExpressionClass.IExpr) parser.parseInt("10 -Cabin.match");
        assertFalse(e6!=null);

        AvatarIBSOriginExpressionClass.BExpr e7 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("not(10)");
        assertFalse(e7!=null);

        AvatarIBSOriginExpressionClass.BExpr e8 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("-(false)");
        assertFalse(e8!=null);

        AvatarIBSOriginExpressionClass.BExpr e9 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("-10 < 5 && 20/4 == 5");
        assertTrue(e9!=null);

        AvatarIBSOriginExpressionClass.IExpr e9Bis = (AvatarIBSOriginExpressionClass.IExpr) parser.parseInt("-10 < (5 && 20)/4 == 5");
        assertFalse(e9Bis!=null);

        AvatarIBSOriginExpressionClass.BExpr e10 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("true && 0 >= 1 || false");
        assertTrue(e10!=null);

        AvatarIBSOriginExpressionClass.IExpr e11 = (AvatarIBSOriginExpressionClass.IExpr) parser.parseInt("8/2*(2+2)");
        assertTrue(e11!=null);

        AvatarIBSOriginExpressionClass.BExpr e12 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("not(!(not(true)))");
        assertTrue(e12!=null);

        AvatarIBSOriginExpressionClass.BExpr e13 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("!(not(true))");
        assertTrue(e13!=null);

        AvatarIBSOriginExpressionClass.BExpr e13bis = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("!(not(TRUE))");
        assertFalse(e13bis!=null);

        AvatarIBSOriginExpressionClass.BExpr e13Ter = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("!(not(FALSE))");
        assertFalse(e13Ter!=null);

        AvatarIBSOriginExpressionClass.IExpr e14 = (AvatarIBSOriginExpressionClass.IExpr) parser.parseInt("3+2");
        assertTrue(e14!=null);

        // Testing extra parenthesis
        AvatarIBSOriginExpressionClass.BExpr e15 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("not((false))");
        assertTrue(e15!=null);

        AvatarIBSOriginExpressionClass.BExpr e16 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("(((10 + ((15)))) >= (((20))))");
        assertTrue(e16!=null);

        AvatarIBSOriginExpressionClass.BExpr e17 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("((true)) && (((((0 >= 1))))) || not((not(false)))");
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
    }
    
    @Test
    public void testBlock() {
        SpecificationBlock specBlock = new SpecificationBlock();
        specBlock.init(block1, false, false);
        int[] attributes = {2, 3, 7, 0, 1};

        attC.clearAttributes();

        String expr = "x + y";
        assertTrue(parser.indexOfVariable(expr, "x") == 0);
        assertTrue(parser.indexOfVariable(expr, "y") == 4);
        assertTrue(parser.indexOfVariable(expr, "z") == -1);
        assertTrue(parser.indexOfVariable("xx+xxx", "x") == -1);
        assertTrue(parser.indexOfVariable("xx==xxx", "x") == -1);
        assertTrue(parser.indexOfVariable("xx==x", "x") == 4);
        assertTrue(parser.indexOfVariable("x==xx", "x") == 0);
        assertTrue(parser.indexOfVariable("x+1==xx", "x") == 0);

        System.out.println("Solver: " + parser.replaceVariable("x==y", "x", "z"));
        assertTrue(parser.replaceVariable("x==y", "x", "z").equals("z==y"));
        assertTrue(parser.replaceVariable("xx==y", "x", "z").equals("xx==y"));
        assertTrue(parser.replaceVariable("xx==x", "x", "z").equals("xx==z"));
        assertTrue(parser.replaceVariable("(foo==foo1)", "foo", "foo").equals("(foo==foo1)"));
        assertTrue(parser.replaceVariable("(foo==foo1)", "foo", "foo1").equals("(foo1==foo1)"));

        AvatarIBSOriginExpressionClass.IExpr e1 = (AvatarIBSOriginExpressionClass.IExpr)parser.parseInt(block1,"x + y");
        assertTrue(e1!=null);
        AvatarIBSOriginExpressionClass.BExpr e2 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool(block1,"-x / y - 15 * z + 1 == -31");
        assertTrue(attC.containsElementAttribute(block1.getAttribute(0)));
        assertTrue(attC.containsElementAttribute(block1.getAttribute(1)));
        assertFalse(attC.containsElementAttribute(block1.getAttribute(2)));
        assertTrue(e2!=null);
        AvatarIBSOriginExpressionClass.BExpr e3 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool(block1,"not(-x / z - (x + y) * 2 + 1 >= -(60 - 26))");
        assertTrue(e3!=null);
        AvatarIBSOriginExpressionClass.BExpr e4 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool(block1,"(key1==true) and (key2==false)");
        assertTrue(e4!=null);
        AvatarIBSOriginExpressionClass.BExpr e5 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool(block1,"(key1) and (key2)");
        assertTrue(e5!=null);
        AvatarIBSOriginExpressionClass.BExpr e6 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool(block1,"(key1==key1) or (key2==key1)");
        assertTrue(e6!=null);
        AvatarIBSOriginExpressionClass.BExpr e7 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool(block1,"((key1==key1) and not(key2==key1)) and (x - y == z + 3)");
        assertTrue(e7!=null);
        AvatarIBSOriginExpressionClass.IExpr e8 = (AvatarIBSOriginExpressionClass.IExpr)parser.parseInt(block1,"x + x*(y+z)/(x + z - x)");
        assertTrue(e8!=null);
        AvatarIBSOriginExpressionClass.IExpr e9 = (AvatarIBSOriginExpressionClass.IExpr)parser.parseInt(block1,"x + x*(y+z)*(x - z)");
        assertTrue(e9!=null);
        AvatarIBSOriginExpressionClass.IExpr e10 = (AvatarIBSOriginExpressionClass.IExpr)parser.parseInt(block1,"x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e10!=null);
        AvatarIBSOriginExpressionClass.IExpr e11 = (AvatarIBSOriginExpressionClass.IExpr)parser.parseInt(block1,"x + y");
        assertTrue(e11!=null);
        AvatarIBSOriginExpressionClass.IExpr e12 = (AvatarIBSOriginExpressionClass.IExpr)parser.parseInt(block1,"x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e12!=null);
        AvatarIBSOriginExpressionClass.BExpr e13 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool(block1,"(key1==false) and (key2==true)");
        assertTrue(e13!=null);
        AvatarIBSOriginExpressionClass.BExpr e14 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool(block1,"x-40<3");
        assertTrue(e14!=null);
        assertTrue(e1.eval(specBlock) == 15);
        assertTrue(e2.eval(specBlock) == true);
        assertTrue(e3.eval(specBlock) == false);
        assertTrue(e4.eval(specBlock) == true);
        assertTrue(e5.eval(specBlock) == false);
        assertTrue(e6.eval(specBlock) == true);
        assertTrue(e7.eval(specBlock) == true);
        assertTrue(e8.eval(specBlock) == 45);
        assertTrue(e9.eval(specBlock) == 570);
        assertTrue(e10.eval(specBlock) == 36);
        assertTrue(e11.eval(attributes) == 5);
        assertTrue(e12.eval(attributes) == 36);
        assertTrue(e13.eval(attributes) == true);
        assertTrue(e14.eval(attributes) == true);

    }
    
    @Test
    public void testSpec() {
        as.sortAttributes();
        as.setAttributeOptRatio(2);
        SpecificationState ss = new SpecificationState();
        ss.setInit(as, false);
        attC.clearAttributes();

        AvatarIBSOriginExpressionClass.IExpr e1 = (AvatarIBSOriginExpressionClass.IExpr) parser.parseInt("block1.x + block2.y");
        assertTrue(e1!=null);

        AvatarIBSOriginExpressionClass.BExpr e2 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("-block1.x / block1.y - 15 * block2.z + 1 == -46");
        assertTrue(attC.containsElementAttribute(block1.getAttribute(0)));
        assertTrue(attC.containsElementAttribute(block2.getAttribute(1)));
        assertFalse(attC.containsElementAttribute(block1.getAttribute(1)));
        assertFalse(attC.containsElementAttribute(block2.getAttribute(0)));
        assertTrue(e2!=null);
        assertTrue(attC.containsElementAttribute(block2.getAttribute(2)));
        AvatarIBSOriginExpressionClass.BExpr e3 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("not(-block2.x / block2.z - not(block1.x + block2.y) * -2 + -(1) <= -(-4 + 7))");
        assertFalse(e3!=null);
        AvatarIBSOriginExpressionClass.IExpr e4 = (AvatarIBSOriginExpressionClass.IExpr) parser.parseInt("block1.x + block2.w");
        assertTrue(e4!=null);

        assertTrue(e1.eval(ss) == 17);
        assertTrue(e2.eval(ss) == true);
//        assertTrue(e3.eval(ss) == 0);
        assertTrue(e4.eval(ss) == -2);
        
        as.removeConstants();
        as.sortAttributes();
        as.setAttributeOptRatio(4);
        ss = new SpecificationState();
        ss.setInit(as, false);
        
        e1 = (AvatarIBSOriginExpressionClass.IExpr) parser.parseInt("block1.x + block2.y");
        assertTrue(e1!=null);
        e2 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("-block1.x / block1.y - 15 * block2.z + 1 == -46");
        assertTrue(e2!=null);
        e3 = (AvatarIBSOriginExpressionClass.BExpr) parser.parseBool("not(-block2.x / block2.z - not(block1.x + block2.y) * -2 + -(1) <= -(-4 + 7))");
        assertFalse(e3!=null);
        e4 = (AvatarIBSOriginExpressionClass.IExpr) parser.parseInt("block1.x + block2.w");
        assertTrue(e4!=null);

        assertTrue(e1.eval(ss) == 17);
        assertTrue(e2.eval(ss) == true);
//        assertTrue(e3.eval(ss) == 0);
        assertTrue(e4.eval(ss) == -2);
    }
}
