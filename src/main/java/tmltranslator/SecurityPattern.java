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

import avatartranslator.AvatarState;
import myutil.TraceManager;

import java.util.Locale;
import java.util.Objects;


public class SecurityPattern {
    public final static int ENCRYPTION_PROCESS = 1;
    public final static int DECRYPTION_PROCESS = 2;

    public final static String SYMMETRIC_ENC_PATTERN = "Symmetric Encryption";
    public final static String ASYMMETRIC_ENC_PATTERN = "Asymmetric Encryption";
    public final static String MAC_PATTERN = "MAC";
    public final static String HASH_PATTERN = "Hash";
    public final static String NONCE_PATTERN = "Nonce";
    public final static String ADVANCED_PATTERN = "Advanced";

    public String name = "";
    public String type = "";
    public int overhead = 0;
    public int size = 0;
    public int encTime = 0;
    public int decTime = 0;

    public String originTask;
    public AvatarState state1;
    public AvatarState state2;

    public String nonce;
    public String formula;
    public String key;
    public String algorithm = "";
    public int process = 0;  // encrypt or decrypt

    public SecurityPattern(String _name, String _type, String _overhead, String _size, String _enctime, String _dectime, String _nonce,
                           String _formula, String _key) {
        this.name = _name;
        this.type = _type;

        if (_nonce != null) {
            if (_nonce.compareTo("-") == 0) {
                this.nonce = "";
            } else {
                this.nonce = _nonce;
            }
        } else {
            this.nonce = "";
        }

        this.formula = _formula;
        this.key = _key;


        try {
            TraceManager.addDev("overhead=" + _overhead);
            this.overhead = Integer.parseInt(_overhead);
        } catch (NumberFormatException e) {}
        try {
            TraceManager.addDev("size=" + _size);
            this.size = Integer.parseInt(_size);
        } catch (NumberFormatException e) {}
        try {
            TraceManager.addDev("decTime=" + _dectime);
            this.decTime = Integer.parseInt(_dectime);
        } catch (NumberFormatException e) {}
        try {
            TraceManager.addDev("enctime=" + _enctime);
            this.encTime = Integer.parseInt(_enctime);
        } catch (NumberFormatException e) {}
    }


    public SecurityPattern(SecurityPattern secPattern) {
        this.name = secPattern.name;
        this.type = secPattern.type;
        this.nonce = secPattern.nonce;
        this.formula = secPattern.formula;
        this.key = secPattern.key;
        this.overhead = secPattern.overhead;
        this.size = secPattern.size;
        this.decTime = secPattern.decTime;
        this.encTime = secPattern.encTime;
    }

    public String toXML() {
        String s = "<SECURITYPATTERN ";

        s += " name=\"" + name;
        s += "\" type=\"" + type;
        s += "\" overhead=\"" + overhead;
        s += "\" size=\"" + size;
        s += "\" encTime=\"" + encTime;
        s += "\" decTime=\"" + decTime;
        s += "\" originTask=\"" + originTask;
        if (state1 != null) {
            s += "\" state1=\"" + state1.getName();
        }
        if (state2 != null) {
            s += "\" state2=\"" + state2.getName();
        }
        s += "\" nonce=\"" + nonce;
        s += "\" formula=\"" + formula;
        s += "\" key=\"" + key;
        s += "\" process=\"" + process;
        s += "\" />\n";

        return s;
    }

    public String getName() {
        return name;
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof SecurityPattern)) return false;

        SecurityPattern securityPattern = (SecurityPattern) o;
        return overhead == securityPattern.overhead &&
                size == securityPattern.size &&
                encTime == securityPattern.encTime &&
                decTime == securityPattern.decTime &&
                Objects.equals(name,securityPattern.name) &&
                Objects.equals(type,securityPattern.type) &&
                Objects.equals(originTask,securityPattern.originTask) &&
                Objects.equals(nonce,securityPattern.nonce) &&
                Objects.equals(formula,securityPattern.formula) &&
                Objects.equals(key,securityPattern.key) &&
                Objects.equals(algorithm,securityPattern.algorithm);

    }

    public boolean isNonceType() {
        if (type == null) {
            return false;
        }
        return type.equals(SecurityPattern.NONCE_PATTERN);
    }

    public void setProcess(int _process) {
        this.process = _process;
    }

    public SecurityPattern deepClone(TMLModeling tmlm) throws TMLCheckingError {
        SecurityPattern sp = new SecurityPattern(name, type, ""+overhead, ""+size, ""+encTime, ""+decTime,
                nonce, formula, key);

        return sp;
    }

}
