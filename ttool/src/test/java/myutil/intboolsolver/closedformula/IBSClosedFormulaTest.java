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
package myutil.intboolsolver.closedformula;
import myutil.TraceManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class IBSClosedFormulaTest {

    private IBSClosedSpec spec = new IBSClosedSpec();
    private IBSClosedComp comp = new IBSClosedComp();
    private IBSClosedSpecState specState = new IBSClosedSpecState();
    private IBSClosedCompState compState = new IBSClosedCompState();
    private int[] quickState = {};
    private IBSClosedFormulaAttributes attC = new IBSClosedFormulaAttributes();
    private IBSClosedFormulaExpressions expr = new IBSClosedFormulaExpressions();

    private IBSClosedFormulaParser parser = new IBSClosedFormulaParser(attC,expr);
    public IBSClosedFormulaTest() {
    }
    
    @Test
    public void testImmediate() {
        TraceManager.addDev("Testing IBSClosedFormulaTest.testImmediate");
        IBSClosedFormulaExpressions.BExpr e1 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("10 + 15 >= 20");
        assertTrue(e1!=null);
        e1 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"10 + 15 >= 20");
        assertTrue(e1!=null);
        e1 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"10 + 15 >= 20");
        assertTrue(e1!=null);

        IBSClosedFormulaExpressions.BExpr e1bis = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("not( ( 0>10 ) and true)");
        assertTrue(e1bis!=null);
        e1bis = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"not( ( 0>10 ) and true)");
        assertTrue(e1bis!=null);
        e1bis = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"not( ( 0>10 ) and true)");
        assertTrue(e1bis!=null);

        IBSClosedFormulaExpressions.BExpr e2 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("-10 / 2 - 15 * 2 + 1 == -30 -4");
        assertTrue(e2!=null);
        e2 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"-10 / 2 - 15 * 2 + 1 == -30 -4");
        assertTrue(e2!=null);
        e2 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"-10 / 2 - 15 * 2 + 1 == -30 -4");
        assertTrue(e2!=null);

        IBSClosedFormulaExpressions.BExpr e3 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("not(-10 / 2 - 15 * 2 + 1 == -(60 - 26))");
        assertTrue(e3!=null);
        e3 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"not(-10 / 2 - 15 * 2 + 1 == -(60 - 26))");
        assertTrue(e3!=null);
        e3 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"not(-10 / 2 - 15 * 2 + 1 == -(60 - 26))");
        assertTrue(e3!=null);

        //TraceManager.addDev("Testing AvatarExpressionTest.testImmediate.e3bis");

        IBSClosedFormulaExpressions.BExpr e4 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("false || -1 >= 0 && true");
        assertTrue(e4!=null);
        e4 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"false || -1 >= 0 && true");
        assertTrue(e4!=null);
        e4 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"false || -1 >= 0 && true");
        assertTrue(e4!=null);

        IBSClosedFormulaExpressions.BExpr e5 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("true and not(false) == !(false or false)");
        assertTrue(e5!=null);
        e5 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"true and not(false) == !(false or false)");
        assertTrue(e5!=null);
        e5 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"true and not(false) == !(false or false)");
        assertTrue(e5!=null);

        IBSClosedFormulaExpressions.IExpr e6 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt("10 -Cabin.match");
        assertFalse(e6!=null);
        e6 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp,"10 -Cabin.match");
        assertFalse(e6!=null);
        e6 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(spec,"10 -Cabin.match");
        assertFalse(e6!=null);

        IBSClosedFormulaExpressions.BExpr e7 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("not(10)");
        assertFalse(e7!=null);
        e7 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"not(10)");
        assertFalse(e7!=null);
        e7 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"not(10)");
        assertFalse(e7!=null);

        IBSClosedFormulaExpressions.BExpr e8 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("-(false)");
        assertFalse(e8!=null);
        e8 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"-(false)");
        assertFalse(e8!=null);
        e8 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"-(false)");
        assertFalse(e8!=null);

        IBSClosedFormulaExpressions.BExpr e9 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("-10 < 5 && 20/4 == 5");
        assertTrue(e9!=null);
        e9 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"-10 < 5 && 20/4 == 5");
        assertTrue(e9!=null);
        e9 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"-10 < 5 && 20/4 == 5");
        assertTrue(e9!=null);

        IBSClosedFormulaExpressions.IExpr e9Bis = (IBSClosedFormulaExpressions.IExpr) parser.parseInt("-10 < (5 && 20)/4 == 5");
        assertFalse(e9Bis!=null);
        e9Bis = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp,"-10 < (5 && 20)/4 == 5");
        assertFalse(e9Bis!=null);
        e9Bis = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(spec,"-10 < (5 && 20)/4 == 5");
        assertFalse(e9Bis!=null);

        IBSClosedFormulaExpressions.BExpr e10 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("true && 0 >= 1 || false");
        assertTrue(e10!=null);
        e10 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"true && 0 >= 1 || false");
        assertTrue(e10!=null);
        e10 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"true && 0 >= 1 || false");
        assertTrue(e10!=null);

        IBSClosedFormulaExpressions.IExpr e11 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt("8/2*(2+2)");
        assertTrue(e11!=null);
        e11 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp,"8/2*(2+2)");
        assertTrue(e11!=null);
        e11 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(spec,"8/2*(2+2)");
        assertTrue(e11!=null);

        IBSClosedFormulaExpressions.BExpr e12 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("not(!(not(true)))");
        assertTrue(e12!=null);
        e12 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"not(!(not(true)))");
        assertTrue(e12!=null);
        e12 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"not(!(not(true)))");
        assertTrue(e12!=null);

        IBSClosedFormulaExpressions.BExpr e13 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("!(not(true))");
        assertTrue(e13!=null);
        e13 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"!(not(true))");
        assertTrue(e13!=null);
        e13 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"!(not(true))");
        assertTrue(e13!=null);

        IBSClosedFormulaExpressions.BExpr e13bis = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("!(not(TRUE))");
        assertFalse(e13bis!=null);
        e13bis = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"!(not(TRUE))");
        assertFalse(e13bis!=null);
        e13bis = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"!(not(TRUE))");
        assertFalse(e13bis!=null);

        IBSClosedFormulaExpressions.BExpr e13Ter = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("!(not(FALSE))");
        assertFalse(e13Ter!=null);
        e13Ter = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"!(not(FALSE))");
        assertFalse(e13Ter!=null);
        e13Ter = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"!(not(FALSE))");
        assertFalse(e13Ter!=null);

        IBSClosedFormulaExpressions.IExpr e14 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt("3+2");
        assertTrue(e14!=null);
        e14 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp,"3+2");
        assertTrue(e14!=null);
        e14 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(spec,"3+2");
        assertTrue(e14!=null);

        // Testing extra parenthesis
        IBSClosedFormulaExpressions.BExpr e15 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("not((false))");
        assertTrue(e15!=null);
        e15 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"not((false))");
        assertTrue(e15!=null);
        e15 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"not((false))");
        assertTrue(e15!=null);

        IBSClosedFormulaExpressions.BExpr e16 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("(((10 + ((15)))) >= (((20))))");
        assertTrue(e16!=null);
        e16 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"(((10 + ((15)))) >= (((20))))");
        assertTrue(e16!=null);
        e16 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"(((10 + ((15)))) >= (((20))))");
        assertTrue(e16!=null);

        IBSClosedFormulaExpressions.BExpr e17 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool("((true)) && (((((0 >= 1))))) || not((not(false)))");
        assertTrue(e17!=null);
        e17 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp,"((true)) && (((((0 >= 1))))) || not((not(false)))");
        assertTrue(e17!=null);
        e17 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec,"((true)) && (((((0 >= 1))))) || not((not(false)))");
        assertTrue(e17!=null);

        IBSClosedFormulaExpressions.IExpr e18 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt("2 * -3 + -5 * 4 + 27"); // 1
        assertTrue(e18!=null);
        e18 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp,"2 * -3 + -5 * 4 + 27"); // 1
        assertTrue(e18!=null);
        e18 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(spec,"2 * -3 + -5 * 4 + 27"); // 1
        assertTrue(e18!=null);

        IBSClosedFormulaExpressions.IExpr e19 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt("-6 / -2 * 3 - -4 "); // 13
        assertTrue(e19!=null);
        e19 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp,"-6 / -2 * 3 - -4 "); // 13
        assertTrue(e19!=null);
        e19 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(spec,"-6 / -2 * 3 - -4 "); // 13
        assertTrue(e19!=null);

        IBSClosedFormulaExpressions.IExpr e20 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt("-(2 * 3)+ -(1+1) * -3"); // 0
        assertTrue(e20!=null);
        e20 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp,"-(2 * 3)+ -(1+1) * -3"); // 0
        assertTrue(e20!=null);
        e20 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(spec,"-(2 * 3)+ -(1+1) * -3"); // 0
        assertTrue(e20!=null);

        assertEquals(true, e1.eval());
        assertEquals(true, e1.eval(compState));
        assertEquals(true, e1.eval(specState));

        assertEquals(true, e2.eval());
        assertEquals(true, e2.eval(compState));
        assertEquals(true, e2.eval(specState));
        
        assertEquals(false, e3.eval());
        assertEquals(false, e3.eval(compState));
        assertEquals(false, e3.eval(specState));
        
        assertEquals(true, e5.eval());
        assertEquals(true, e5.eval(compState));
        assertEquals(true, e5.eval(specState));
        
        assertEquals(true, e9.eval());
        assertEquals(true, e9.eval(compState));
        assertEquals(true, e9.eval(specState));
        
        assertEquals(false, e10.eval());
        assertEquals(false, e10.eval(compState));
        assertEquals(false, e10.eval(specState));
        
        assertEquals(16, e11.eval());
        assertEquals(16, e11.eval(compState));
        assertEquals(16, e11.eval(specState));
        
        assertEquals(false, e12.eval());
        assertEquals(false, e12.eval(compState));
        assertEquals(false, e12.eval(specState));
        
        assertEquals(true, e13.eval());
        assertEquals(true, e13.eval(compState));
        assertEquals(true, e13.eval(specState));
        
        assertEquals(5, e14.eval());
        assertEquals(5, e14.eval(quickState));
        assertEquals(5, e14.eval(specState));
        
        assertEquals(true, e15.eval());
        assertEquals(true, e15.eval(compState));
        assertEquals(true, e15.eval(specState));
        
        assertEquals(true, e16.eval());
        assertEquals(true, e16.eval(compState));
        assertEquals(true, e16.eval(specState));
        
        assertEquals(false, e17.eval());
        assertEquals(false, e17.eval(compState));
        assertEquals(false, e17.eval(specState));
        
        assertEquals(1, e18.eval());
        assertEquals(1, e18.eval(compState));
        assertEquals(1, e18.eval(specState));
        
        assertEquals(13, e19.eval());
        assertEquals(13, e19.eval(compState));
        assertEquals(13, e19.eval(specState));
        
        assertEquals(0, e20.eval());
        assertEquals(0, e20.eval(compState));
        assertEquals(0, e20.eval(specState));

    }
    
    @Test
    public void testBlock() {
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

        IBSClosedFormulaExpressions.IExpr e1 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp, "x + y");
        assertTrue(e1 == null);
        IBSClosedFormulaExpressions.BExpr e2 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp, "-x / y - 15 * z + 1 == -31");
        assertTrue(e2 == null);
        IBSClosedFormulaExpressions.BExpr e3 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp, "not(-x / z - (x + y) * 2 + 1 >= -(60 - 26))");
        assertTrue(e3 == null);
        IBSClosedFormulaExpressions.BExpr e4 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp, "(key1==true) and (key2==false)");
        assertTrue(e4 == null);
        IBSClosedFormulaExpressions.BExpr e5 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp, "(key1) and (key2)");
        assertTrue(e5 == null);
        IBSClosedFormulaExpressions.BExpr e6 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp, "(key1==key1) or (key2==key1)");
        assertTrue(e6 == null);
        IBSClosedFormulaExpressions.BExpr e7 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp, "((key1==key1) and not(key2==key1)) and (x - y == z + 3)");
        assertTrue(e7 == null);
        IBSClosedFormulaExpressions.IExpr e8 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp, "x + x*(y+z)/(x + z - x)");
        assertTrue(e8 == null);
        IBSClosedFormulaExpressions.IExpr e9 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp, "x + x*(y+z)*(x - z)");
        assertTrue(e9 == null);
        IBSClosedFormulaExpressions.IExpr e10 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp, "x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e10 == null);
        IBSClosedFormulaExpressions.IExpr e11 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp, "x + y");
        assertTrue(e11 == null);
        IBSClosedFormulaExpressions.IExpr e12 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(comp, "x*((x + y)*z + (x+z)/z)/x");
        assertTrue(e12 == null);
        IBSClosedFormulaExpressions.BExpr e13 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp, "(key1==false) and (key2==true)");
        assertTrue(e13 == null);
        IBSClosedFormulaExpressions.BExpr e14 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(comp, "x-40<3");
        assertTrue(e14 == null);
        IBSClosedFormulaExpressions.IExpr e15 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(spec, "block1.x + block2.y");
        assertTrue(e15 == null);
        IBSClosedFormulaExpressions.BExpr e16 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec, "-block1.x / block1.y - 15 * " + "block2.z + 1 == -46");
        assertTrue(e16 == null);
        IBSClosedFormulaExpressions.BExpr e17 = (IBSClosedFormulaExpressions.BExpr) parser.parseBool(spec, "not(-block2.x / block2.z - not" + "(block1.x + block2.y) * -2 + -(1) <= -(-4 + 7))");
        assertTrue(e17 == null);
        IBSClosedFormulaExpressions.IExpr e18 = (IBSClosedFormulaExpressions.IExpr) parser.parseInt(spec, "block1.x + block2.w");
        assertTrue(e18 == null);
    }
}
