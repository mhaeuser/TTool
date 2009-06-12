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

#include <ListenersSimCmd.h>
#include <SimComponents.h>
#include <SchedulableDevice.h>
#include <TMLCommand.h>
#include <TMLChoiceCommand.h>

RunXTransactions::RunXTransactions(SimComponents* iSimComp, unsigned int iTransToExecute):_simComp(iSimComp), _count(0), _transToExecute(iTransToExecute){
	for(SchedulingList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i)
		(*i)->registerListener(this);	
}

RunXTransactions::~RunXTransactions(){
	for(SchedulingList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i)
		(*i)->removeListener(this);
}
bool RunXTransactions::transExecuted(TMLTransaction* iTrans){
	_count++;
	if (_count>=_transToExecute){
		_simComp->setStopFlag(true);
		return true;
	}
	return false;
}
void RunXTransactions::setTransToExecute(unsigned int iTransToExecute){
	_transToExecute=iTransToExecute;
}



Breakpoint::Breakpoint(SimComponents* iSimComp):_simComp(iSimComp){
}

bool Breakpoint::commandEntered(TMLCommand* iComm){
	if (_enabled){
		_simComp->setStopFlag(true);
		return true;
	}
	return false;
}

void Breakpoint::setEnabled(bool iEnabled){
	_enabled=iEnabled;
}



RunTillNextRandomChoice::RunTillNextRandomChoice(SimComponents* iSimComp):_simComp(iSimComp), _enabled(false){
	TMLCommand::registerGlobalListenerForType<TMLChoiceCommand>(this);
}

bool RunTillNextRandomChoice::commandEntered(TMLCommand* iComm){
	TMLChoiceCommand* aChoice=dynamic_cast<TMLChoiceCommand*>(iComm);
	if (_enabled && aChoice!=0 && aChoice->isNonDeterministic()){
		_simComp->setStopFlag(true);
		return true;
	}
	return false;
}

void RunTillNextRandomChoice::setEnabled(bool iEnabled){
	_enabled=iEnabled;
}



RunXCommands::RunXCommands(SimComponents* iSimComp, unsigned int iCommandsToExecute):_simComp(iSimComp), _count(0), _commandsToExecute(iCommandsToExecute){
	TMLCommand::registerGlobalListener(this);
}

RunXCommands::~RunXCommands(){
	TMLCommand::removeGlobalListener(this);
}

bool RunXCommands::commandFinished(TMLCommand* iComm){
	_count++;
	if (_count>=_commandsToExecute){
		 _simComp->setStopFlag(true);
		return true;
	}
	return false;
}

void RunXCommands::setCmdsToExecute(unsigned int iCommandsToExecute){
	_commandsToExecute=iCommandsToExecute;
}



RunXTimeUnits::RunXTimeUnits(SimComponents* iSimComp, TMLTime iEndTime):_simComp(iSimComp), _endTime(iEndTime){
	for(SchedulingList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i)
		(*i)->registerListener(this);	
}

RunXTimeUnits::~RunXTimeUnits(){
	for(SchedulingList::const_iterator i=_simComp->getCPUList().begin(); i != _simComp->getCPUList().end(); ++i)
		(*i)->removeListener(this);
}
	
bool RunXTimeUnits::transExecuted(TMLTransaction* iTrans){
	if (SchedulableDevice::getSimulatedTime()>=_endTime){
		_simComp->setStopFlag(true);
		return true;
	}
	return false;
}

void RunXTimeUnits::setEndTime(TMLTime iEndTime){
	_endTime=iEndTime;
}



RunTillTransOnDevice::RunTillTransOnDevice(SimComponents* iSimComp, ListenerSubject<TransactionListener>* iSubject):_simComp(iSimComp), _subject(iSubject) {
	_subject->registerListener(this);
}
RunTillTransOnDevice::~RunTillTransOnDevice(){
	_subject->removeListener(this);
}

bool RunTillTransOnDevice::transExecuted(TMLTransaction* iTrans){
	_simComp->setStopFlag(true);
	return true;
}
