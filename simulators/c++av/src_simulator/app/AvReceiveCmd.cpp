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
#include<AvReceiveCmd.h>
#include<AvSignal.h>
#include<AvTransition.h>

AvReceiveCmd::AvReceiveCmd(ID iID, AvBlock* iBlock, AvSignal* iSignal, ParamSetFuncPointer iParamSetFunc): AvSingleTransCmd(iID, "Receive", iBlock,1), _signal(iSignal),  _paramSetFunc(iParamSetFunc){
}

AvReceiveCmd::~AvReceiveCmd(){
}

AvSignal* AvReceiveCmd::getSignal() const{
	return _signal;
}

AvNode* AvReceiveCmd::prepare(bool iControlTransfer){
	_signal->registerReader(_incomingTrans);
	return this;
}

AvNode* AvReceiveCmd::execute(const SystemTransition& iTrans){
	//std::cout << "execute receive 1\n";
	Parameter* aParamData = _signal->read(_incomingTrans);
	//std::cout << "execute receive 2\n";
	if (aParamData!=0){
		//std::cout << "execute receive 3\n";
		if(_paramSetFunc!=0) (_block->*_paramSetFunc)(aParamData);
		delete aParamData;
	}
	//std::cout << "execute receive 4\n";
	_outgoingTrans[0]->prepare(true);
	//std::cout << "execute receive 5\n";
	return this;
}

bool AvReceiveCmd::isEnabled(EnabledTransList& iEnaTransList, AvTransition* iIncomingTrans){
	return _signal->getSyncTransitionsReader(_block, _incomingTrans, iEnaTransList);
}

AvNode* AvReceiveCmd::cancel(){
	_signal->cancelReader(_incomingTrans);
	return this;
}
