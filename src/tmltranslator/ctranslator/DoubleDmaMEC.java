/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

   ludovic.apvrille AT enst.fr
   andrea.enrici AT enstr.fr

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

   /**
   * Class DoubleDmaMEC, Model Extension Construct (MEC) class for a double DMA data transfer
   * Creation: 05/02/2014
   * @version 1.0 05/02/2014
   * @author Andrea ENRICI
   * @see
   */

package tmltranslator.ctranslator;;

import java.util.*;

public class DoubleDmaMEC extends CPMEC	{

	public static final int MaxParameters = 6;
	public static final int destinationAddress1Index = 0;
	public static final int sourceAddress1Index = 1;
	public static final int counter1Index = 2;
	public static final int destinationAddress2Index = 3;
	public static final int sourceAddress2Index = 4;
	public static final int counter2Index = 5;

	public static final String destinationAddress1 = "destinationAddress1";
	public static final String sourceAddress1 = "sourceAddress1";
	public static final String counter1 = "counter1";
	public static final String destinationAddress2 = "destinationAddress2";
	public static final String sourceAddress2 = "sourceAddress2";
	public static final String counter2 = "counter2";

	private String memoryBaseAddress = "0";

	public DoubleDmaMEC( String ctxName, ArchUnitMEC archMEC, int srcMemoryType, int dstMemoryType, int transferType, String sizeString )	{

		switch( srcMemoryType )	{
			case Buffer.FepBuffer:
				memoryBaseAddress = "fep_mss";
				break;
			case Buffer.AdaifBuffer:
				memoryBaseAddress = "adaif_mss";
			break;
			case Buffer.InterleaverBuffer:
				memoryBaseAddress = "intl_mss";
			break;
			case Buffer.MapperBuffer:
				memoryBaseAddress = "mapper_mss";
			break;
			case Buffer.MainMemoryBuffer:
				memoryBaseAddress = "0";
			break;
			default:
				memoryBaseAddress = "0";
			break;
		}

		switch( transferType )	{
			case CPMEC.mem2IP:
				exec_code = TAB + "embb_mem2ip((EMBB_CONTEXT *)&" + ctxName + ", (uintptr_t) " + memoryBaseAddress + ", /*USER TODO: *SRC */, " + sizeString + " );" + CR;	
				init_code = TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName + ", " + "(uintptr_t) " + memoryBaseAddress + " );" + CR;
				cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName + ");";
			break;
			case CPMEC.IP2mem:
				exec_code = TAB + "embb_ip2mem( /* USER TODO: *DST */, (EMBB_CONTEXT *)&" + ctxName + ", (uintptr_t) " + memoryBaseAddress + ", " + sizeString + " );" + CR;	
				init_code = TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName + ", " + "(uintptr_t) " + memoryBaseAddress + " );" + CR;
				cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName + ");";
			break;
			case CPMEC.IP2IP:
				exec_code = TAB + "embb_ip2ip((EMBB_CONTEXT *)&" + ctxName + "_0, (uintptr_t) " + memoryBaseAddress + ", (EMBB_CONTEXT *)&" + ctxName + "_1, (uintptr_t) " + memoryBaseAddress + ", " + sizeString + " );" + CR;	
				init_code = TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName + "_0, " + "(uintptr_t) " + memoryBaseAddress + " );" + CR;
				init_code += TAB + archMEC.getCtxInitCode() + "((EMBB_CONTEXT *)&" + ctxName + "_1, " + "(uintptr_t) " + memoryBaseAddress + " );" + CR;
				cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName + "_0);";
				cleanup_code = TAB + archMEC.getCtxCleanupCode() + "(&" + ctxName + "_1);";
			break;
		}
	}

	public static Vector<String> sortAttributes( Vector<String> assignedAttributes )	{
		
		Vector<String> newVector = new Vector<String>( assignedAttributes );
		for( String s: assignedAttributes )	{
			if( s.contains( destinationAddress1 ) )	{
				newVector.set( destinationAddress1Index, getAttributeValue(s) );
			}
			if( s.contains( sourceAddress1 ) )	{
				newVector.set( sourceAddress1Index, getAttributeValue(s) );
			}
			if( s.contains( counter1 ) )	{
				newVector.set( counter1Index, getAttributeValue(s) );
			}
			if( s.contains( destinationAddress2 ) )	{
				newVector.set( destinationAddress2Index, getAttributeValue(s) );
			}
			if( s.contains( sourceAddress2 ) )	{
				newVector.set( sourceAddress2Index, getAttributeValue(s) );
			}
			if( s.contains( counter2 ) )	{
				newVector.set( counter2Index, getAttributeValue(s) );
			}
		}
		return newVector;
	}

}	//End of class
