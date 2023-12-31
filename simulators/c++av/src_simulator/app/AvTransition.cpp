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

#include<AvTransition.h>
#include<AvCmd.h>
#include<AvBlock.h>

AvTransition::AvTransition(ID iID, AvBlock* iBlock, CondFuncPointer iCondFunc, PtrValue<ParamType> iAfterMin, PtrValue<ParamType> iAfterMax, PtrValue<ParamType> iComputeMin, PtrValue<ParamType> iComputeMax, ActionFuncPointer iActionFunc): AvNode(iID, "Transition", iBlock), AvCheckpoint(), _outgoingCmd(0), _condFunc(iCondFunc), _afterMin(iAfterMin), _afterMax(iAfterMax), _computeMin(iComputeMin), _computeMax(iComputeMax), _computeFor(0), _actionFunc(iActionFunc), _state(DISABLED), _lastControlTransfer(true){
}

AvTransition::~AvTransition(){
}

AvNode* AvTransition::prepare(bool iControlTransfer){
	_lastControlTransfer=iControlTransfer;
	//std::cout << "trans prepare 1\n";
	if (_lastControlTransfer){
		//std::cout << "trans prepare 2\n";
		_block->setCurrCommand(this);
		if (_actionFunc!=0) (_block->*_actionFunc)();
	}
	//std::cout << "trans prepare 3\n";
	if(_condFunc!=0 && (_block->*_condFunc)()==0){
		_state=DISABLED;
	}else{
		//std::cout << "trans prepare 4\n";
		AVTTime aTime2Wait = myrand(_afterMin, _afterMax);
		if (aTime2Wait==0){
			//std::cout << "trans prepare 5\n";
			_computeFor = myrand(_computeMin, _computeMax);
			if(_computeFor==0){
				//std::cout << "trans prepare 5a\n";
				_state = WAIT4CMD;
				return _outgoingCmd->prepare(_lastControlTransfer);
			}else{
				//std::cout << "trans prepare 5b\n";
				//registerEventIn(_computeFor);
				_state = PREPCOMP;
			}
		}else{
			//std::cout << "trans prepare 6\n";
			registerEventIn(aTime2Wait);
			_state = WAIT4AFTER;
		}
			
	}
	//std::cout << "trans prepare 7\n";
	return this;
}

AvNode* AvTransition::execute(const SystemTransition& iSyncCmd){
	if (_state==PREPCOMP){
		registerEventIn(_computeFor);
		_block->setCurrCommand(this);
		_state=WAIT4COMP;
		return this;
	}
	if (_state==WAIT4CMD){
		if (!_lastControlTransfer && _actionFunc!=0) (_block->*_actionFunc)();
		return _outgoingCmd->execute(iSyncCmd);
	}
	return this;
}

bool AvTransition::isEnabled(EnabledTransList& iEnaTransList, AvTransition* iIncomingTrans){
	if (_state==PREPCOMP){
		std::ostringstream aTransText;
		aTransText << "compute for " << _computeFor << " in " << toString();
		iEnaTransList.push_back(SystemTransition(_block, this, 0, aTransText.str()));
		return true;
	}
	if (_state==WAIT4CMD) return _outgoingCmd->isEnabled(iEnaTransList,this);
	return false;
}

AvNode* AvTransition::cancel(){
	AvNode* aResult=0;
	switch(_state){
	case WAIT4AFTER:
		cancelEvent();
		break;
	case WAIT4CMD:
		aResult = _outgoingCmd->cancel();
		break;
	default: ;
	}
	return aResult;
}

void AvTransition::eventQCallback(){
	if(_state==WAIT4AFTER && (_computeFor = myrand(_computeMin, _computeMax))!=0){
		//registerEventIn(_computeFor);
		_state = PREPCOMP;
	}else{
		_state = WAIT4CMD;
		_outgoingCmd->prepare(_lastControlTransfer);
	}
	
}

void AvTransition::setOutgoingCmd(AvCmd* iCmd){
	_outgoingCmd = iCmd;
	_outgoingCmd->setIncomingTrans(this);
}

AvCmd* AvTransition::getOutgoingCmd(){
	return _outgoingCmd;
}

std::string AvTransition::toString() const{
	return AvNode::toString();
}

bool AvTransition::directExecution(){
	//std::cout << "let's crash\n";
	//std::cout << "not crashed\n";
	return !(_afterMin!=0 || _afterMax!=0 || (_actionFunc==0 && _computeMin==0 && _computeMax==0 && !_outgoingCmd->directExecution()));
}

bool AvTransition::evaluateGuard(){
	return (_condFunc==0 || (_block->*_condFunc)()!=0);
}
