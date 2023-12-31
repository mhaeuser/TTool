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

#include <TMLDelayCommand.h>
#include <TMLTask.h>
#include <TMLTransaction.h>
#include <SimComponents.h>
//#include <CommandListener.h>
TMLDelayCommand::TMLDelayCommand(ID iID, TMLTask* iTask,TMLLength iStatLength, ActionFuncPointer iActionFunc, const char* iLiveVarList, bool iCheckpoint, bool isActiveDelay):TMLCommand(iID, iTask, 1, 1, iLiveVarList, iCheckpoint),_actionFunc(iActionFunc){
	if (!isActiveDelay){
	    _isActiveDelay = false;
	}else {
	  //_length = iStatLength;
	    _isActiveDelay = true;
	}
	_type=ACT;
}
void TMLDelayCommand::execute(){
	//std::cout << "execi: " << _currTransaction->toShortString() << std::endl;
	_progress+=_currTransaction->getVirtualLength();
	_task->addTransaction(_currTransaction);
	prepare(false);
}

TMLCommand* TMLDelayCommand::prepareNextTransaction(){
	if (_simComp->getStopFlag()){
		_simComp->setStoppedOnAction();
		//std::cout << "sim stopped in action command " << std::endl;
		_task->setCurrCommand(this);
		return this;  //for command which generates transactions this is returned anyway by prepareTransaction
	}

	if (_progress==0){
	  _length = (_task->*_actionFunc)();
//    _execTimes++;  this will recognize delay transaction one more time so remove it
    		if (_length==0){
    			//std::cout << "ExeciCommand len==0 " << std::endl;
    			TMLCommand* aNextCommand=getNextCommand();
    			#ifdef STATE_HASH_ENABLED
                    if (_liveVarList!=0) _task->refreshStateHash(_liveVarList);
                #endif
                _task->setCurrCommand(aNextCommand);
                    //FOR_EACH_CMDLISTENER (*i)->commandFinished(this);
                #ifdef LISTENERS_ENABLED
                    NOTIFY_CMD_FINISHED(this);
                    //NOTIFY_CMD_FINISHED(0);
                #endif
    			if (aNextCommand!=0) return aNextCommand->prepare(false);
    		}
    	}

    _currTransaction = new TMLTransaction(this, _length-_progress,_task->getEndLastTransaction());
    return this;
}

std::string TMLDelayCommand::toString() const{
	std::ostringstream outp;
    if(_isActiveDelay)
        outp << ": Delay in "  << TMLCommand::toString();
    else
        outp << ": IdleDL in "  << TMLCommand::toString();
    return outp.str();
}

std::string TMLDelayCommand::toString(TMLLength aLength, TMLLength aProgress) const{
	std::ostringstream outp;
    if(_isActiveDelay)
        outp << ": Delay in "  << TMLCommand::toString(aLength, aProgress);
    else
        outp << ": IdleDL in "  << TMLCommand::toString(aLength, aProgress);
    return outp.str();
}

std::string TMLDelayCommand::toShortString() const{
	std::ostringstream outp;
    if(_isActiveDelay)
        outp << _task->toString() << ": Delay in "  << TMLCommand::toString();
    else
        outp << _task->toString() << ": IdleDL in "  << TMLCommand::toString();
    return outp.str();
}

std::string TMLDelayCommand::toShortString(TMLLength aLength, TMLLength aProgress) const{
	std::ostringstream outp;
    if(_isActiveDelay)
        outp << _task->toString() << ": Delay in "  << TMLCommand::toString(aLength, aProgress);
    else
        outp << _task->toString() << ": IdleDL in "  << TMLCommand::toString(aLength, aProgress);
    return outp.str();
}

