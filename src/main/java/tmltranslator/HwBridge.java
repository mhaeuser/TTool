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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet; 
import java.util.Objects;

/**
 * Class HwBridge
 * Creation: 23/11/2007
 * @version 1.0 23/11/2007
 * @author Ludovic APVRILLE
 */
public class HwBridge extends HwCommunicationNode  {

    public static final int DEFAULT_BUFFER_BYTE_DATA_SIZE = 4;
    
    public boolean isFirewall;
    public List<String> firewallRules = new ArrayList<String>();
    public int latency = 0;
    public int bufferByteSize = DEFAULT_BUFFER_BYTE_DATA_SIZE; // In bytes. Should more than 0
    
    public HwBridge(String _name) {
        super(_name);
    }
    
    @Override
    public String toXML() {
	String s = "<BRIDGE name=\"" + name + "\" clockRatio=\"" + clockRatio + "\"  bufferByteSize=\"" + bufferByteSize + "\" />\n";
	return s;
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof HwBridge)) return false;
        if (!super.equalSpec(o)) return false;
        HwBridge hwBridge = (HwBridge) o;
        return latency == hwBridge.latency &&
                bufferByteSize == hwBridge.bufferByteSize &&
                isFirewall == hwBridge.isFirewall &&
                (new HashSet<>(firewallRules).equals(new HashSet<>(hwBridge.firewallRules)));
    }

    public HwBridge deepClone(TMLArchitecture _archi) throws TMLCheckingError {
        HwBridge newNode = new HwBridge(getName());
        fillValues(newNode, _archi);
        return newNode;
    }

    public void fillValues(HwBridge newNode, TMLArchitecture _archi)  throws TMLCheckingError {
        super.fillValues(newNode, _archi);
        newNode.isFirewall = isFirewall;
        newNode.bufferByteSize = bufferByteSize;
        newNode.latency = latency;
        newNode.firewallRules.addAll(firewallRules);
    }



}
