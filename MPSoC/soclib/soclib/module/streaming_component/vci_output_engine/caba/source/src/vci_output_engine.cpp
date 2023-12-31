// -*- c++ -*-

#include "../include/vci_output_engine.h"
#include "soclib_endian.h"
#include "exception.h"

#define OUTPUT_ENGINE_DEBUG 0

namespace soclib { namespace caba {

enum send_cmd_state_e{
  FSM_SEND_CMD_IDLE,
  FSM_SEND_CMD_GET_ADDR,
  FSM_SEND_CMD_GET_INFO,
  FSM_SEND_CMD_SET_FIRST,
  FSM_SEND_CMD_START,
  FSM_SEND_CMD_VCI_CMD,
  FSM_SEND_CMD_TEST
};
enum get_rsp_state_e{
  FSM_GET_RSP_IDLE,
  FSM_GET_RSP_DESC_ADDRESS,
  FSM_GET_RSP_DESC_INFO,
  FSM_GET_RSP_PACKET,
  FSM_GET_RSP_WRITE_FILE,
  FSM_GET_RSP_WRITE_FIFO
};

// This macro is an helper function to factor out the template parameters
#define tmpl(x) template<typename vci_param> x VciOutputEngine<vci_param>

tmpl(/**/)::VciOutputEngine(
			    sc_module_name insname,
			    const soclib::common::IntTab &index,
			    const soclib::common::MappingTable &mt,
			    uint32_t advance,
                            uint32_t delay,
                            uint32_t size,
			    const std::string &output_file,
			    const std::string &throw_file)
		   : soclib::caba::BaseModule(insname),
		   r_fsm_send_cmd_state("fsm_send_cmd_state"),
		   r_is_first("is_first"),
		   r_config("config"),
		   r_status("status"),
		   r_mesure("mesure"),
		   r_vci_cmd_counter("vci_cmd_counter"),
		   r_vci_rsp_counter("vci_rsp_counter"),
		   r_reordering_counter("reordering_counter"),
	           reordering_buffer(size,advance,delay),
	           m_advance(advance),
	           m_delay(delay),
		   m_ident(mt.indexForId(index))
{
  m_foutput = fopen(output_file.c_str(), "w+");
  if (!m_foutput)
    throw soclib::exception::RunTimeError(name() + ": Cant open file " + output_file);

  m_fordo = fopen("ordo.txt", "w+");
  if (!m_fordo)
    throw soclib::exception::RunTimeError(name() + ": Cant open file ordo.txt");

  // ce_ci a �t� ajout� pour l affichage des paquets jet�s
  m_fthrow=fopen(throw_file.c_str(), "w+");
  
  m_fthroughput = fopen("throughput.txt", "w+");
  if (!m_fthroughput)
    throw soclib::exception::RunTimeError(name() + ": Cant open file throughput.txt");

  SC_METHOD(transition);
  dont_initialize();
  sensitive << p_clk.pos();

  SC_METHOD(genMoore);
  dont_initialize();
  sensitive << p_clk.neg();
}


tmpl(/**/)::~VciOutputEngine()
{
  fclose(m_foutput);
  fclose(m_fthrow);
  fclose(m_fthroughput);
  std::cout << name() << " m_cpt_debit " << (double)m_cpt_debit<< " r_mesure " << (double)r_mesure.read() << "m_cpt_debit/r_mesure " << (double)((double)m_cpt_debit/(double)r_mesure.read()) <<std::endl;

}

tmpl(void)::sortirpaquet( const uint32_t* buffer, papr_desc_slot_t &desc_slot, uint32_t d )
{
 
  uint32_t data=0;

#if OUTPUT_ENGINE_DEBUG
  std::cout << name() << " sortirpaquet " << std::hex << info << std::endl;
#endif

  //fprintf(m_foutput,"%f %d %d ",sc_simulation_time(), desc_slot.total_size, d);
  fprintf(m_foutput,"%d ",desc_slot.total_size);
  //printf("%d ",desc_slot.total_size);
  
 //data = buffer[j++];
  //data = (uint32_t) m_data[desc.offset-4];
  //data = soclib::endian::uint32_swap(data);
  //fprintf(m_foutput , "%d ", data); // date d'entr�e du paquet
  //fprintf(m_foutput , "  ");

  uint8_t *packet = (uint8_t*)m_data;

  data=data+(packet[desc_slot.offset-1]<<24);
  data=data+(packet[desc_slot.offset-2]<<16);
  data=data+(packet[desc_slot.offset-3]<<8);
  data=data+(packet[desc_slot.offset-4]);

  //fprintf(m_foutput, "%d ", data);
  //fprintf(m_fordo, "%d ", data);

  //m_cpt_debit+=128*8;//DG 10.7. un slot
  m_cpt_debit+=desc_slot.slot_size*8;//DG 8.8. un paquet
  for ( size_t i=desc_slot.offset; i<desc_slot.slot_size+desc_slot.offset; ++i )
    {fprintf(m_foutput, "%02x", packet[i]);
      //printf("%02x", packet[i]);
	//m_cpt_debit+=8; ///11.06. comptage de debit: on compte les bits de l'en-tete et du payload uniquement, pas les bits du descripteur. Avantage: vaut aussi pour les grands paquets!!!
    }

  fprintf(m_foutput, "\n");
  fflush(m_foutput);
}

tmpl(void)::transition()
{
  // On reset condition, we initialize the component,
  if (!p_resetn.read()) {
    r_fsm_send_cmd_state = FSM_SEND_CMD_IDLE;
    r_fsm_get_rsp_state =  FSM_GET_RSP_IDLE;
    r_is_first = true;
    r_config = 0;
    r_status = 0;
    r_mesure = 0;
    m_cpt_debit = 0; //DG 11.06.
    return;
  }

  // We are not on reset case.

// DG 11.06. measure local time and calculate debit
  r_mesure = r_mesure.read()+1;
  if (r_mesure.read() == 100000){
  std::cout << name() << " m_cpt_debit " << (double)m_cpt_debit<< " r_mesure " << (double)r_mesure.read() << " m_cpt_debit/r_mesure " << (double)((double)m_cpt_debit/(double)r_mesure.read()) <<std::endl;

    if(m_cpt_debit!=0){
	fprintf(m_fthroughput, "%f\n", (double)((double)m_cpt_debit/(double)r_mesure.read()));
    } //DG print to file for avarage calculus
  r_mesure = 0; //reset interval counter
  m_cpt_debit = 0; 
	}

  r_config = p_running.read();
  if (!r_config)
    return;

  // Implement the SEND CMD FSM
  switch ((enum send_cmd_state_e)r_fsm_send_cmd_state.read()){

  case FSM_SEND_CMD_IDLE:
    if ( (enum get_rsp_state_e)r_fsm_get_rsp_state.read() == FSM_GET_RSP_IDLE )
      r_fsm_send_cmd_state = FSM_SEND_CMD_GET_ADDR;
    break;

  case FSM_SEND_CMD_GET_ADDR :
    if (p_desc.rok.read()) {
#if OUTPUT_ENGINE_DEBUG 
      std::cout << name() << " ####r_addr_slot: "<< std::hex << p_desc.data.read()<<std::endl;
#endif
      m_slot_u.binary[0]=p_desc.data.read();

#if OUTPUT_ENGINE_DEBUG 
 std::cout << name() << " #####m_slot_u.binary[0] before: "<< std::hex <<m_slot_u.binary[0] <<std::endl;
#endif
      r_fsm_send_cmd_state=FSM_SEND_CMD_GET_INFO;
    }
    break;

  case FSM_SEND_CMD_GET_INFO :
    if (p_desc.rok.read()) {
#if OUTPUT_ENGINE_DEBUG 
       std::cout << name() << " #####r_info_slot: "<< std::hex << p_desc.data.read()<<std::endl;
#endif
      m_slot_u.binary[1]=p_desc.data.read();

      r_fsm_send_cmd_state=FSM_SEND_CMD_SET_FIRST;
    }
    break;

  case FSM_SEND_CMD_SET_FIRST :
    r_is_first=true;
    r_fsm_send_cmd_state=FSM_SEND_CMD_START;
    break;

  case FSM_SEND_CMD_START :
    if(r_fsm_get_rsp_state.read()==FSM_GET_RSP_IDLE){
      r_vci_cmd_counter=0;
      r_fsm_send_cmd_state=FSM_SEND_CMD_VCI_CMD;
    }
    break;

  case FSM_SEND_CMD_VCI_CMD :

    if (p_vci.cmdack.read()){
#if OUTPUT_ENGINE_DEBUG
      std::cout << name() << " send cmd vci ack" << std::endl;
      std::cout << name() << " r_vci_cmd_counter:" << r_vci_cmd_counter.read()<< std::endl;
#endif
      r_vci_cmd_counter=r_vci_cmd_counter.read()+4;
      if(r_vci_cmd_counter.read()==124){
	r_fsm_send_cmd_state=FSM_SEND_CMD_TEST;
      } else {
#if OUTPUT_ENGINE_DEBUG
	std::cout << name() << " send cmd vci not ack" << std::endl;
#endif
      }
    }
    break;

  case FSM_SEND_CMD_TEST :
    //std::cout <<"***"<< m_next_u.descripteur_slot.address << std::endl;//DG 8.8. faux, c'est un debut du paquet!! J'ai pas ici descripteur_slot car dans les transferts VCI en RSP??
    //std::cout <<"***"<< m_slot_u.descripteur_slot.address << std::endl;
    //DG 9.8. hier stimmt etwas nicht!
    //std::cout <<"****"<< m_next_u.descripteur_slot.is_internal << std::endl;
    if(m_next_u.descripteur_slot.is_internal==0){
    //if((m_slot_u.descripteur_slot.address!=0)){ 
    //if((m_next_u.descripteur_slot.address!=0)){ 
	//if((m_next_u.descripteur_slot.address!=0)||(m_next_u.descripteur_slot.is_internal==0)){
      r_is_first=false;//DG 8.8. : ?????
      r_fsm_send_cmd_state=FSM_SEND_CMD_START;
    }
    else{
      r_fsm_send_cmd_state=FSM_SEND_CMD_IDLE;
    }
    break;
  }
  //if (r_fsm_send_cmd_state.read()>0)
  //printf("r_fsm_send_cmd_state: %x\n",r_fsm_send_cmd_state.read());

  // Implement the GET RSP FSM
  switch ((enum get_rsp_state_e)r_fsm_get_rsp_state.read()) {

  case FSM_GET_RSP_IDLE :
    if(r_fsm_send_cmd_state.read()==FSM_SEND_CMD_START){
      //printf("debug \n");
      r_fsm_get_rsp_state=FSM_GET_RSP_DESC_ADDRESS;
    }
    break;

  case FSM_GET_RSP_DESC_ADDRESS :
    if (p_vci.rspval.read()){
      m_next_u.binary[0]=p_vci.rdata.read();

      r_fsm_get_rsp_state=FSM_GET_RSP_DESC_INFO;
    }
    break;

  case FSM_GET_RSP_DESC_INFO :
    if (p_vci.rspval.read()){
      m_next_u.binary[1]=p_vci.rdata.read();
      #if OUTPUT_ENGINE_DEBUG
      std::cout<<"rdata0: "<<p_vci.rdata.read()<<std::endl;
      #endif
      r_fsm_get_rsp_state = FSM_GET_RSP_PACKET;
      r_vci_rsp_counter = 0;
    }
    break;

  case FSM_GET_RSP_PACKET:
    if (p_vci.rspval.read())
      {
	#if OUTPUT_ENGINE_DEBUG
	std::cout<<"rdata1: "<<p_vci.rdata.read()<<std::endl;
	#endif
	m_data[r_vci_rsp_counter] = machine_to_be((uint32_t)p_vci.rdata.read());
	r_vci_rsp_counter = r_vci_rsp_counter + 1;
	if (p_vci.reop.read()){
	  //c'est le dernier mot d'un slot
	  r_fsm_get_rsp_state = FSM_GET_RSP_WRITE_FILE;
	}
      }

    break;

  case FSM_GET_RSP_WRITE_FILE:
    sortirpaquet( m_data, m_next_u.descripteur_slot,m_slot_u.descripteur.date );
    r_fsm_get_rsp_state = FSM_GET_RSP_WRITE_FIFO;
    break;

  case FSM_GET_RSP_WRITE_FIFO :
    if ( r_is_first.read() ? p_slin.wok.read() : p_slext.wok.read() ) {
      r_fsm_get_rsp_state = FSM_GET_RSP_IDLE;
    }
    break;
  }
  //if (r_fsm_get_rsp_state.read()>0)
  //printf("r_fsm_get_rsp_state: %x \n",r_fsm_get_rsp_state.read());

  /* if (p_desc.rok.read() && p_desc.r.read()){
    std::cout << name()
	      << " data recue sur desc "
	      << " : "  << std::hex << p_desc.data.read()
	      << std::endl;
	      }*/
}


tmpl(void)::genMoore()
{
  //Emission de la valeur du status
  p_status = r_fsm_send_cmd_state.read();
  p_vci.trdid = 0x0;
  p_vci.pktid = 0x0;
  p_vci.srcid = m_ident;
  p_vci.plen = 0x0;
  p_vci.contig = 0x0;
  p_vci.clen = 0x0;
  p_vci.cfixed = true;
  p_vci.cons = 0x0;
  p_vci.wrap = 0x0;
  p_vci.be = 0xF;

  if ( ! r_config.read() ) {
    p_vci.cmdval = false;
    p_desc.r = false;
    return;
  }

  switch (r_fsm_send_cmd_state) {
  case FSM_SEND_CMD_GET_ADDR:
  case FSM_SEND_CMD_GET_INFO:
    p_vci.cmdval = false;
    p_desc.r = true;
    p_vci.eop = false;
    break;

  case FSM_SEND_CMD_IDLE:
  case FSM_SEND_CMD_TEST:
  case FSM_SEND_CMD_START:
  case FSM_SEND_CMD_SET_FIRST:
    p_vci.cmdval = false;
    p_desc.r = false;
    break;

  case FSM_SEND_CMD_VCI_CMD:
    p_vci.cmdval = true;
    p_vci.cmd = vci_param::CMD_READ;
    {
      typename vci_param::fast_addr_t addr = m_slot_u.descripteur.address+r_vci_cmd_counter.read();
      p_vci.address = addr;
      #if OUTPUT_ENGINE_DEBUG
      std::cout << name() << " sent on VCI "
		<< "ADDRESS " << std::hex << addr
		<< " eop: " << (r_vci_cmd_counter.read()==124)
		<< std::endl;
      #endif
    }
    p_vci.eop = r_vci_cmd_counter.read()==124;
    p_desc.r = false;
    break;
  }// switch r_fsm_send_cmd_state closing

  switch (r_fsm_get_rsp_state) {
  case FSM_GET_RSP_IDLE:
    p_vci.rspack= false;
    p_slin.w = false;
    break;

  case FSM_GET_RSP_DESC_ADDRESS:
  case FSM_GET_RSP_DESC_INFO:
  case FSM_GET_RSP_PACKET:
    p_vci.rspack= true;
    p_slin.w = false;
    break;

  case FSM_GET_RSP_WRITE_FILE:
    p_vci.rspack= false;
    p_slin.w = false;
    break;

  case FSM_GET_RSP_WRITE_FIFO:
    p_vci.rspack= false;
    #if OUTPUT_ENGINE_DEBUG
    printf("adresse rendue : %x\n",m_slot_u.descripteur.address);
    printf("bis adresse rendue : %x\n",m_slot_u.binary[0]);
    #endif
    //if ((r_is_first.read())||(m_next_u.descripteur_slot.is_internal)) { //DG 8.8. deuxieme condition!!
      if (r_is_first.read()){
      p_slin.w = true;
      p_slin.data = m_slot_u.binary[0]; 
      //printf("internal adresse rendue : %x\n",m_slot_u.binary[0]);
      //p_slin.data = m_slot_u.descripteur.address;
      p_slext.w = false;
    } else {//DG 8.8. faelschlicherweise interne addresse in externen kanal gesendet
      p_slext.w = true; 
      p_slext.data = m_slot_u.binary[0];
      //printf("external adresse rendue : %x\n",m_slot_u.binary[0]);
      //printf("internal oui/non : %d\n",m_next_u.descripteur_slot.is_internal);//DG 9.8. faux!!
      //p_slext.data = m_slot_u.descripteur.address;
      p_slin.w = false;
    }
    break;
  }// switch r_fsm_get_rsp_state closing
}

}}
