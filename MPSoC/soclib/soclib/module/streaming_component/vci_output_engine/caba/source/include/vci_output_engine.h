/* -*- c++ -*-
 * File      : soclib_vci_output_engine.h
 * Date      : 221/02/2008
 * Copyright : UPMC - LIP6
 *
**************************************************************************/
/*
   This componnent implements a vci output engine.
   It is designed to copy network packets from system memory
   into ethernet link(a text file).

*/


/**************************************************************************************************************************/
#ifndef SOCLIB_VCI_OUTPUT_ENGINE_H
#define SOCLIB_VCI_OUTPUT_ENGINE_H

// includes
#include <systemc>
#include <string>
#include "caba_base_module.h"
#include "vci_initiator.h"
#include "mapping_table.h"
#include "generic_fifo.h"
#include "fifo_ports.h"
#include "../../../../papr_slot/include/papr_slot.h"
#include "soclib_reordering_buffer.h"
namespace soclib { namespace caba {

template<typename vci_param>
class VciOutputEngine
  : public soclib::caba::BaseModule
{
  // we have tow FSM in this component SEND_CMD and GET_RSP

  //SEND_CMD
  //the register holding the SEND_CMD state
  sc_core::sc_signal<int> r_fsm_send_cmd_state;


  //GET_RSP
  //the register holding the GET_RSP state
  sc_core::sc_signal< int > r_fsm_get_rsp_state;

  //registers used to save useful data between command & response
  sc_core::sc_signal<bool> r_is_first;
  sc_core::sc_signal<uint32_t> r_config;
  sc_core::sc_signal<uint32_t> r_status;
  sc_core::sc_signal<size_t> r_vci_cmd_counter;
  sc_core::sc_signal<size_t> r_reordering_counter;
  sc_core::sc_signal<size_t> r_vci_rsp_counter;
  sc_core::sc_signal<int>	 r_mesure;
  FILE *m_foutput;
  FILE *m_fthrow;
  FILE *m_fordo;
  FILE *m_fthroughput;
  uint32_t m_ident;
  papr_desc_or_data_t m_slot_u;
  papr_desc_slot_or_data_t m_next_u;
  uint32_t m_advance;
  uint32_t m_delay;
  uint32_t m_cpt_debit; //DG 11.06.
  typename vci_param::fast_addr_t m_addr;
  uint32_t m_info;
  int  nember_of_readed_info;
protected:
  // Mandatory SystemC construct
  SC_HAS_PROCESS(VciOutputEngine);

public:
  // The ports
  sc_core::sc_in<bool> p_resetn;
  sc_core::sc_in<bool> p_clk;
  soclib::caba::VciInitiator<vci_param> p_vci;
  sc_core::sc_in<uint32_t> p_running;
   sc_core::sc_out<uint32_t> p_status;
  soclib::caba::FifoInput<uint32_t> p_desc;
  soclib::caba::FifoOutput<uint32_t> p_slin;
  soclib::caba::FifoOutput<uint32_t> p_slext;
  //soclib::caba::ReorderingBuffer<uint32_t,sc_core::sc_uint<6> > reordering_buffer;// a v�rifier 2
  soclib::caba::ReorderingBuffer<uint32_t,uint32_t> reordering_buffer;// a v�rifier 2
  // used to reorder pkt (to be changed)
  // sera modifie quand je travaillerai sur l'ordonnancement
  typename vci_param::fast_data_t m_data[380];
  bool test;
  

  // Constructor & descructor, explained above
  VciOutputEngine(
		  sc_core::sc_module_name insname,
		  const soclib::common::IntTab &index,
		  const soclib::common::MappingTable &mt,
                  uint32_t advance,
                  uint32_t delay,
                  uint32_t size,
		  const std::string &foutput,
		  const std::string &fthrow);
  ~VciOutputEngine();

private:
  // The FSM functions
  void transition();
  void genMoore();
  //void sortirpaquet( const uint32_t* buffer, papr_desc_t &);
  //};
  //void sortirpaquet( const uint32_t* buffer, papr_desc_slot_t &desc_slot, papr_desc_t &desc);
void sortirpaquet( const uint32_t* buffer, papr_desc_slot_t &desc_slot, uint32_t d);
};

// Namespace closing
}}
#endif
