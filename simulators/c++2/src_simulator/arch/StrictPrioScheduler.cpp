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
#include <StrictPrioScheduler.h>
#include <TMLTransaction.h>
#include <TMLCommand.h>
#include <TMLTask.h>

StrictPrioScheduler::StrictPrioScheduler(const std::string& iName, Priority iPrio, TMLTime iTimeSlice ,TMLTime iMinSliceSize): WorkloadSource(iPrio), _name(iName), _nextTransaction(0), _timeSlice(iTimeSlice), _minSliceSize(iMinSliceSize), _elapsedTime(0), _lastSource(0){
}

StrictPrioScheduler::StrictPrioScheduler(const std::string& iName, Priority iPrio, TMLTime iTimeSlice ,TMLTime iMinSliceSize, WorkloadSource** aSourceArray, unsigned int iNbOfSources): WorkloadSource(iPrio, aSourceArray, iNbOfSources), _name(iName), _nextTransaction(0), _timeSlice(iTimeSlice), _minSliceSize(iMinSliceSize), _elapsedTime(0), _lastSource(0){
}

TMLTime StrictPrioScheduler::schedule(TMLTime iEndSchedule){
	TaskList::iterator i;
	//std::cout << _name << ": Schedule called \n";
	TMLTransaction *anOldTransaction=_nextTransaction, *aTempTrans, *isDelayTrans;
	TMLTime aLowestRunnableTimeFuture=-1,aRunnableTime, aLowestRunnableTimePast=-1, oTimeSlice;
	Priority aHighestPrioPast=-1, aHighestPrioFuture=-1;
	//WorkloadSource* anOldSource=_lastSource;
	
	WorkloadSource *aSourcePast=0, *aSourceFuture=0;
	bool aSameTaskFound=false;
	if (_lastSource!=0){
		_lastSource->schedule(iEndSchedule);
		if (_lastSource->getNextTransaction(iEndSchedule)!=0 && _lastSource->getNextTransaction(iEndSchedule)->getVirtualLength()!=0){
			isDelayTrans = _lastSource->getNextTransaction(iEndSchedule);
            if((!(isDelayTrans->getCommand()->getActiveDelay()) && isDelayTrans->getCommand()->isDelayTransaction())){
                 aSameTaskFound=false;
            }else if (anOldTransaction==0 || _lastSource->getNextTransaction(iEndSchedule)==anOldTransaction || _timeSlice >=_elapsedTime + anOldTransaction->getOperationLength() + _minSliceSize){
				if (anOldTransaction != 0)
				    std::cout << "Select same task, remaining: " << _timeSlice - anOldTransaction->getOperationLength() << "\n";
				aSameTaskFound=true; // here, _lastSource->getNextTransaction(iEndSchedule) must not be in the future
				aSourcePast=_lastSource;
				aHighestPrioPast=_lastSource->getPriority();
			}
		}
	}
	for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i){
		(*i)->schedule(iEndSchedule);
		aTempTrans=(*i)->getNextTransaction(iEndSchedule);
		if (aTempTrans != 0 && aTempTrans->getVirtualLength() != 0) {
			aRunnableTime = aTempTrans->getRunnableTime();
			if (aRunnableTime<=iEndSchedule){
				if ((*i)->getPriority() < aHighestPrioPast || ((*i)->getPriority() == aHighestPrioPast && aRunnableTime < aLowestRunnableTimePast && !aSameTaskFound)) {
					aHighestPrioPast = (*i)->getPriority();
					aLowestRunnableTimePast = aRunnableTime;
					aSourcePast = *i;
				}
			}
			else {
				if ( aRunnableTime < aLowestRunnableTimeFuture || (aRunnableTime == aLowestRunnableTimeFuture && (*i)->getPriority() < aHighestPrioFuture)) {
					aHighestPrioFuture = (*i)->getPriority();
					aLowestRunnableTimeFuture = aRunnableTime;
					aSourceFuture = *i;
			}
			}
		}
	}
	if (aSourcePast==0){
		_nextTransaction=(aSourceFuture==0)? 0 : aSourceFuture->getNextTransaction(iEndSchedule);
		_lastSource=aSourceFuture;		
	}else{  aSameTaskFound &= (_lastSource==aSourcePast);
		_nextTransaction=aSourcePast->getNextTransaction(iEndSchedule);
		_lastSource=aSourcePast;
	}
	if (aSameTaskFound){
		if (_nextTransaction!=anOldTransaction && anOldTransaction!=0){
			_elapsedTime +=  anOldTransaction->getOperationLength();
			oTimeSlice = _timeSlice-_elapsedTime;
		}
		//std::cout << "Not crashed\n" ;
	}else{
		//if (_lastSource==0)
			//std::cout << _name << ": No source found\n";
		//else
			//std::cout << _name << ": New  source found " << _lastSource->toString() << "\n";
		 oTimeSlice= _timeSlice;
	}
	if (_lastSource==aSourcePast && aSourceFuture!=0)
		 	 oTimeSlice = min(oTimeSlice,aLowestRunnableTimeFuture-iEndSchedule);
	//if (_nextTransaction!=0){
	//	_nextTransaction->setLength(min(_nextTransaction->getOperationLength(), _timeSlice-_elapsedTime));
	//}                                                                                                
	//std::cout << "End schedule\n" ;
	return oTimeSlice;
}

//TMLTransaction* StrictPrioScheduler::getNextTransaction(TMLTime iEndSchedule) const{
//	return _nextTransaction;
//}

void StrictPrioScheduler::reset(){
	WorkloadSource::reset();
	_nextTransaction=0;
	_elapsedTime=0;
	_lastSource=0;
}

//std::string StrictPrioScheduler::toString() const{
//	return _name;
//}

StrictPrioScheduler::~StrictPrioScheduler(){
  //std::cout << _name << ": Scheduler deleted\n";
}

std::istream& StrictPrioScheduler::readObject(std::istream &is){
	WorkloadSource::readObject(is);
	READ_STREAM(is,_elapsedTime);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: StrictScheduler " << _name << " elapsedTime: " << _elapsedTime << std::endl;
#endif
	int aLastSourceIndex;
	READ_STREAM(is, aLastSourceIndex);
#ifdef DEBUG_SERIALIZE
	std::cout << "Read: StrictScheduler " << _name << " lastSourceIndex: " << aLastSourceIndex << std::endl;
#endif
	if (aLastSourceIndex==-1){
		_lastSource=0;
	}else{
		WorkloadList::iterator i=_workloadList.begin();
		std::advance(i, aLastSourceIndex);
		_lastSource=*i;
	}
	return is;
}

std::ostream& StrictPrioScheduler::writeObject(std::ostream &os){
	WorkloadSource::writeObject(os);
	WRITE_STREAM(os,_elapsedTime);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: StrictScheduler " << _name << "  elapsedTime: " << _elapsedTime << std::endl;
#endif
	int aLastSourceIndex;
	if (_lastSource==0){
		aLastSourceIndex=-1;
	}else{
		aLastSourceIndex=0;
		for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i){
			if (*i==_lastSource)
				break;
			else
				aLastSourceIndex++;
		}
	}
	WRITE_STREAM(os, aLastSourceIndex);
#ifdef DEBUG_SERIALIZE
	std::cout << "Write: StrictScheduler " << _name << " lastSourceIndex: " << aLastSourceIndex << std::endl;
#endif
	return os;
}

//void StrictScheduler::transWasScheduled(SchedulableDevice* iDevice){
//	if (_lastSource!=0) _lastSource->transWasScheduled(iDevice);
//}

