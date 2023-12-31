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

import translator.CheckingError;

import java.util.ArrayList;
import java.util.Objects;


/**
* Class TMLActivityElementChannel
 * Creation: 23/11/2005
 *
 * @author Ludovic APVRILLE
 * @version 1.1 18/02/2015
 */
public abstract class TMLActivityElementChannel extends TMLActivityElement {
    protected ArrayList<TMLChannel> channels;
    protected String nbOfSamples;
    private boolean isAttacker;
    protected boolean isEncForm; //If the Cryptographic

    public TMLActivityElementChannel(String _name, Object _referenceObject) {
        super(_name, _referenceObject);
        channels = new ArrayList<TMLChannel>();
        isAttacker = false;
    }

    public void addChannel(TMLChannel _channel) {
        channels.add(_channel);
    }

    public int getNbOfChannels() {
        return channels.size();
    }

    public boolean hasChannel(TMLChannel ch) {
        return channels.contains(ch);
    }

    public TMLChannel getChannel(int _index) {
        return channels.get(_index);
    }

    public boolean isAttacker() {
        return isAttacker;
    }

    public void setAttacker(boolean attacker) {
        isAttacker = attacker;
    }

    public void setNbOfSamples(String _nbOfSamples) {
        nbOfSamples = _nbOfSamples;
    }

    public String getNbOfSamples() {
        return nbOfSamples;
    }

    public boolean getEncForm() {
        return isEncForm;
    }

    public void setEncForm(boolean form) {
        isEncForm = form;
    }

    public void replaceChannelWith(TMLChannel oldChan, TMLChannel newChan) {
        if (channels.contains(oldChan)) {
            channels.remove(oldChan);
            channels.add(newChan);
        }
    }
    
    @Override
    public String customExtraToXML() {
        String s = " nbOfSamples=\"" + nbOfSamples + "\" ";
        String chan = "";
        String chanType = "0";
        for (TMLChannel ch : channels) {
            chan += ch.getName() + " ";
            chanType = "" + ch.getType();
        }
        s += " channels=\"" + chan + "\" ";
        s += " type=\"" + chanType + "\" ";
        return s;
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof TMLActivityElementChannel)) return false;
        if (!super.equalSpec(o)) return false;
        TMLActivityElementChannel tmlActEltChannel = (TMLActivityElementChannel) o;
        return Objects.equals(nbOfSamples, ((TMLActivityElementChannel) o).getNbOfSamples()) &&
                isAttacker == tmlActEltChannel.isAttacker() &&
                isEncForm == tmlActEltChannel.getEncForm();

    }

    public void fillValues(TMLActivityElementChannel newElt, TMLModeling tmlm) throws TMLCheckingError {
        super.fillValues(newElt, tmlm);
        newElt.setNbOfSamples(getNbOfSamples());
        newElt.setAttacker(isAttacker);
        newElt.setEncForm(getEncForm());

        for(TMLChannel channel: channels) {
            TMLChannel ch = tmlm.getChannelByName(channel.getName());
            if (ch == null) {
                throw new TMLCheckingError(CheckingError.STRUCTURE_ERROR, "Unknown channel in cloned model: " + channel.getName());
            }
            newElt.addChannel(ch);
        }
    }
}
