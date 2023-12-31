/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 */




package tmltranslator;

import java.util.Objects;

/**
 * Class HwMemory
 * Creation: 23/11/2007
 * @version 1.0 23/11/2007
 * @author Ludovic APVRILLE
 */
public class HwMemory extends HwCommunicationNode  {

    public static final int DEFAULT_BYTE_DATA_SIZE = 4;
    public static final int DEFAULT_MEMORY_SIZE = 1024;
    public static final int DEFAULT_BUFFER_TYPE = 0;

    public int bufferType = DEFAULT_BUFFER_TYPE;
    public int byteDataSize = DEFAULT_BYTE_DATA_SIZE; // In bytes. Should more than 0
    public int memorySize = DEFAULT_MEMORY_SIZE; // In bytes. Should more than 0

    public HwMemory(String _name) {
        super(_name);
    }

    
    @Override
    public String toXML() {
	String s = "<MEMORY name=\"" + name + "\" clockRatio=\"" + clockRatio + "\"  byteDataSize=\"" + byteDataSize + "\" memorySize=\"" + memorySize + "\"  bufferType=\"" + bufferType + "\" />\n";
	return s;
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof HwMemory)) return false;
        if(!super.equalSpec(o)) return false;
        HwMemory hwMemory = (HwMemory) o;
        return bufferType == hwMemory.bufferType &&
                byteDataSize == hwMemory.byteDataSize &&
                memorySize == hwMemory.memorySize;
    }

    public HwMemory deepClone(TMLArchitecture _archi) throws TMLCheckingError {
        HwMemory newNode = new HwMemory(getName());
        fillValues(newNode, _archi);
        return newNode;
    }

    public void fillValues(HwMemory newNode, TMLArchitecture _archi)  throws TMLCheckingError {
        super.fillValues(newNode, _archi);
        newNode.byteDataSize = byteDataSize;
        newNode.bufferType = bufferType;
        newNode.memorySize = memorySize;
    }

}
