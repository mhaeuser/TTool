/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Daniel Knorreck,
Ludovic Apvrille, Renaud Pacalet
 *
 * ludovic.apvrille AT telecom-paristech.fr
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
 */ 
#include<AvSendCmd.h>
#include<AvSignal.h>
#include<AvTransition.h>

AvSendCmd::AvSendCmd(ID iID, AvBlock* iBlock, AvSignal* iSignal, ParamGetFuncPointer iParamGetFunc): AvSingleTransCmd(iID, "Send", iBlock,1), _signal(iSignal), _paramGetFunc(iParamGetFunc){
}

AvSendCmd::~AvSendCmd(){
}

AvSignal* AvSendCmd::getSignal() const{
	return _signal;
}

AvNode* AvSendCmd::prepare(bool iControlTransfer){
	_signal->registerWriter(_incomingTrans);
	return this;
}

AvNode* AvSendCmd::execute(const SystemTransition& iTrans){
	Parameter* aParamData;
	//std::cout << "execute send 1\n";
	aParamData = (_paramGetFunc==0)? 0: (_block->*_paramGetFunc)();
	//std::cout << "execute send 2\n";
	_signal->write(_incomingTrans, iTrans.syncTrans, aParamData);
	//std::cout << "execute send 3\n";
	_outgoingTrans[0]->prepare(true);
	//std::cout << "execute send 4\n";
	return this;
}

bool AvSendCmd::isEnabled(EnabledTransList& iEnaTransList, AvTransition* iIncomingTrans){
	return _signal->getSyncTransitionsWriter(_block, _incomingTrans, iEnaTransList);
}

AvNode* AvSendCmd::cancel(){
	_signal->cancelWriter(_incomingTrans);
	return this;
}
