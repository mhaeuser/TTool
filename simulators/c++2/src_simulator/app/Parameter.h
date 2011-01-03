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

#ifndef ParameterH
#define ParameterH

#include <definitions.h>
#include <HashAlgo.h>
///This class encapsulates three parameters
template <typename T>
class Parameter{
public:
	///Constructor
	/**
	\param ip1 Value 1
	\param ip2 Value 2
	\param ip3 Value 3
	*/
	Parameter(const T& ip1,const T& ip2,const T& ip3):_p1(ip1),_p2(ip2),_p3(ip3){}
	///Constructor
	Parameter():_p1(0),_p2(0),_p3(0){}
	///Constructor
	/**
	\param s Stream providing the three parameter values
	*/
	Parameter(std::istream& s){
		READ_STREAM(s, _p1);
		READ_STREAM(s, _p2);
		READ_STREAM(s, _p3);
#ifdef DEBUG_SERIALIZE
		print();
#endif
	}
	/////Assignement operator, copies all parameters
	/*const Parameter<T>& operator=(const Parameter<T>& rhs){
		_p1=rhs._p1;
		_p2=rhs._p2;
		_p3=rhs._p3;
		return *this;
	}*/
	///Print function for testing purposes
	void print() const{
		//if (_p1!=0 || _p2!=0 || _p3!=0)
		std::cout << "p1:" << _p1 << " p2:" << _p2 << " p3:" << _p3 << std::endl;
	}
	inline std::ostream& writeObject(std::ostream& s){
		WRITE_STREAM(s, _p1);
		WRITE_STREAM(s, _p2);
		WRITE_STREAM(s, _p3);
#ifdef DEBUG_SERIALIZE
		print();
#endif
		return s;
	}
	///Stream operator >>
	friend std::istream& operator >>(std::istream &is,Parameter<T> &obj){
		is >> obj._p1 >> obj._p2 >> obj._p3;
 		return is;
	}
	///Streams the parameter in XML format
	/**
	\param s Stream
	*/
	void streamStateXML(std::ostream& s) const{
		s << TAG_PARAMo << TAG_E1o << _p1 << TAG_E1c << TAG_E2o << _p2 << TAG_E2c << TAG_E3o << _p3 << TAG_E3c << TAG_PARAMc;
	}
	//inline static void * operator new(size_t size){
	//	return memPool.pmalloc(size);
	//}
	//inline static void operator delete(void *p, size_t size){
	//	memPool.pfree(p, size);
	//}
	///Returns parameter no 1
	/**
	\return Parameter no 1
	*/
	inline T getP1() const { return _p1;}
	///Returns parameter no 2
	/**
	\return Parameter no 2
	*/
	inline T getP2() const { return _p2;}
	///Returns parameter no 3
	/**
	\return Parameter no 3
	*/
	inline T getP3()const { return _p3;}
	///Sets parameter no 1
	/**
	\param iP1 Parameter no 1
	*/
	inline void setP1(T iP1){ _p1=iP1;}
	///Sets parameter no 2
	/**
	\param iP2 Parameter no 2
	*/
	inline void setP2(T iP2){ _p2=iP2;}
	///Sets parameter no 3
	/**
	\param iP3 Parameter no 3
	*/
	inline void setP3(T iP3){ _p3=iP3;}
	///Sets all parameters
	/**
	\param iP1 Parameter no 1
	\param iP2 Parameter no 2
	\param iP3 Parameter no 3
	*/
	inline void setP(T iP1, T iP2, T iP3){ _p1=iP1; _p2=iP2; _p3=iP3;}
	///Gets all parameters
	/**
	\param oP1 Variable no 1
	\param oP2 Variable no 2
	\param oP3 Variable no 3
	*/
	inline void getP(T& oP1, T& oP2, T& oP3) const { oP1=_p1; oP2=_p2; oP3=_p3;}
	inline void getP(T& oP1, T& oP2, const T& oP3) const { oP1=_p1; oP2=_p2;}
	inline void getP(T& oP1, const T& oP2, const T& oP3) const { oP1=_p1;}
	
	inline void getStateHash(HashAlgo* iHash) const{
		std::cout << "add param vals:\n";
		iHash->addValue((HashValueType)_p1);
		iHash->addValue((HashValueType)_p2);
		iHash->addValue((HashValueType)_p3);
	}
		
	/*inline void removeStateHash(HashAlgo* iHash) const{
		iHash->removeValue((HashValueType)_p1);
		iHash->removeValue((HashValueType)_p2);
		iHash->removeValue((HashValueType)_p3);
	}*/
	
protected:
	///Parameter no 1
	T _p1;
	///Parameter no 2
	T _p2;
	///Parameter no 3
	T _p3;
	//static Pool<Parameter<T> > memPool;
};
#endif
