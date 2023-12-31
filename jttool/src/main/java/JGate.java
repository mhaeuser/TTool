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





package jttool;


/**
 * Class JGate
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 04/03/2005
 * @version 1.1 04/03/2005
 * @author Ludovic APVRILLE
 */
public class JGate {
    
    private String name;
    private boolean left;
    private JMasterGate jmg;
    public int protocol = 0;
    public int localPort = 0;
    public int destPort = 0;
    public String destHost = "";
    public String localHost = "";
    
    public JGate(String _name) {
	name = _name;
    }
    
    public JGate(String _name, JMasterGate _jmg) {
	name = name;
	jmg = _jmg;
    }
    
    public void setMasterGate(JMasterGate _jmg) {
        jmg = _jmg;
    }

    public JMasterGate getMasterGate() {
	return jmg;
    }
    
    public void setLeft() {
        left = true;
    }
    
    public void setRight() {
        left = false;
    }
    
    public boolean getLeft() {
        return left;
    }

    public String getName() {
	return name;
    }

    public void setProtocol(int _protocol) {
	protocol = _protocol;
    }

    public void setLocalPort(int _port) {
	localPort = _port;
    }

    public void setDestPort(int _port) {
	destPort = _port;
    }

    public void setDestHost(String _host) {
	destHost = _host;
    }

    public void setLocalHost(String _host) {
	localHost = _host;
    }
}
