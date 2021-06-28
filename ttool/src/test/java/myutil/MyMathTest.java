/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.
*/
package myutil;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyMathTest {

    @Test
    public void testGaussianDistributionLaw() {
        System.out.println("Testing gaussian distribution law between 1 and 10");

        int[] tab = new int[10];

        int loop = 10000;
        double average  = 0;
        double a = 1.0;
        double b = 10.0;

        for(int i=0; i<loop; i++) {
            double d = MyMath.gaussianDistribution(a-0.4999, b+0.49999, 2.0);
            average += d;
            int r = (int)(Math.round(d));
            //System.out.println("d=" + d + " r=" + r);
            //assertTrue(r>=a);
            //assertTrue(r<=b);
            tab[r-1]++;
        }

        System.out.println("Results of gaussian test:");
        for(int i=0; i<tab.length; i++) {
            System.out.println("tab[" + (i+1) + "]=" + tab[i]);
        }
        average = average / loop;
        System.out.println("Average:" + average);

        assertTrue((int)average > ((b-a)/2) - 1);
        assertTrue((int)average < ((b-a)/2) + 1);

    }

    @Test
    public void testMatrixMoveLine() {
        byte[][] testMatrix = {
                {0, 0, 0},
                {1, 1, 1},
                {2, 2, 2},
                {3, 3, 3},
                {4, 4, 4}};

        String bm0 = MyMath.byteMatrixToString(testMatrix);
        System.out.println("\nMatrix0:\n" + bm0 + "\n\n");

        byte[][] resultMatrix1 = MyMath.moveLineCreateNew(testMatrix, 3, 1);
        assertNotNull(resultMatrix1);
        String bm1 = MyMath.byteMatrixToString(resultMatrix1);
        assertNotNull(bm1);
        System.out.println("\nMatrix1:\n" + bm1 + "\n\n");

        assertEquals(resultMatrix1[0][0], 0);
        assertEquals(resultMatrix1[1][0], 3);
        assertEquals(resultMatrix1[2][0], 1);
        assertEquals(resultMatrix1[3][0], 2);
        assertEquals(resultMatrix1[4][0], 4);

        byte[][] resultMatrix2 = MyMath.moveLineCreateNew(testMatrix, 1, 3);
        assertNotNull(resultMatrix2);
        String bm2 = MyMath.byteMatrixToString(resultMatrix2);
        assertNotNull(bm2);
        System.out.println("\nMatrix2:\n" + bm2 + "\n\n");

        assertEquals(resultMatrix2[0][0], 0);
        assertEquals(resultMatrix2[1][0], 2);
        assertEquals(resultMatrix2[2][0], 3);
        assertEquals(resultMatrix2[3][0], 1);
        assertEquals(resultMatrix2[4][0], 4);

        byte[][] resultMatrix3 = MyMath.moveLineCreateNew(testMatrix, 0, 4);
        assertNotNull(resultMatrix3);
        String bm3 = MyMath.byteMatrixToString(resultMatrix3);
        assertNotNull(bm3);
        System.out.println("\nMatrix3:\n" + bm3 + "\n\n");

        assertEquals(resultMatrix3[0][0], 1);
        assertEquals(resultMatrix3[1][0], 2);
        assertEquals(resultMatrix3[2][0], 3);
        assertEquals(resultMatrix3[3][0], 4);
        assertEquals(resultMatrix3[4][0], 0);

        byte[][] resultMatrix4 = MyMath.moveLineCreateNew(testMatrix, 4, 0);
        assertNotNull(resultMatrix4);
        String bm4 = MyMath.byteMatrixToString(resultMatrix4);
        assertNotNull(bm4);
        System.out.println("\nMatrix4:\n" + bm4 + "\n\n");

        assertEquals(resultMatrix4[0][0], 4);
        assertEquals(resultMatrix4[1][0], 0);
        assertEquals(resultMatrix4[2][0], 1);
        assertEquals(resultMatrix4[3][0], 2);
        assertEquals(resultMatrix4[4][0], 3);
    }


}
