/*Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Niu Siyuan,
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
#include<OrderScheduler.h>
#include <TMLTransaction.h>

OrderScheduler::OrderScheduler(const std::string& iName, Priority iPrio): WorkloadSource(iPrio), _name(iName), _nextTransaction(0) {
}

OrderScheduler::OrderScheduler(const std::string& iName, Priority iPrio, WorkloadSource** aSourceArray, unsigned int iNbOfSources): WorkloadSource(iPrio, aSourceArray, iNbOfSources), _name(iName), _nextTransaction(0), _lastSource(0) {
}

TMLTime OrderScheduler::schedule(TMLTime iEndSchedule){
  std::cout<<"order scheduler "<<std::endl;
	TaskList::iterator i;

    //std::cout << _name << ": Schedule called \n";
    TMLTransaction *anOldTransaction = _nextTransaction, *aTempTrans;
    TMLTime aLowestRunnableTimeFuture=-1, aRunnableTime, aLowestRunnableTimePast=-1;
    WorkloadSource *aSourcePast=0, *aSourceFuture=0;
        //std::cout << _name << ": Second if\n";
        for(WorkloadList::iterator i=_workloadList.begin(); i != _workloadList.end(); ++i){
             (*i)->schedule(iEndSchedule);
            //std::cout << _name << " schedules, before getCurrTransaction " << std::endl;
            aTempTrans=(*i)->getNextTransaction(iEndSchedule);
            //std::cout << "after getCurrTransaction " << std::endl;
            if (aTempTrans!=0 && aTempTrans->getVirtualLength()!=0){
                aRunnableTime=aTempTrans->getRunnableTime();
                if (aRunnableTime<=iEndSchedule){
                    //Past
                    if (aRunnableTime<aLowestRunnableTimePast){
                        aLowestRunnableTimePast=aRunnableTime;
                        aSourcePast=*i;
                    }
                }else{
                    //Future
                    if(aRunnableTime<aLowestRunnableTimeFuture){
                        aLowestRunnableTimeFuture=aRunnableTime;
                        aSourceFuture=*i;
                    }
                }
            }
        }
    	if (aSourcePast==0){
    		_nextTransaction=(aSourceFuture==0)? 0 : aSourceFuture->getNextTransaction(iEndSchedule);
    		_lastSource=aSourceFuture;
    	}else{
    		_nextTransaction=aSourcePast->getNextTransaction(iEndSchedule);
    		_lastSource=aSourcePast;
    	}
#ifdef DEBUG_FPGA
	if(_nextTransaction) std::cout<<"order next trans is "<<_nextTransaction->toShortString()<<std::endl;
	else std::cout<<"order next trans is 0"<<std::endl;
	std::cout<<"end order scheduler"<<std::endl;
#endif
	return 0;
}

OrderScheduler::~OrderScheduler(){
  //std::cout << _name << ": Scheduler deleted\n";
}

void OrderScheduler::reset(){
	WorkloadSource::reset();
	_nextTransaction=0;
}
