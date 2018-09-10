/* -*- c++ -*-
 *
 * SOCLIB_LGPL_HEADER_BEGIN
 * 
 * This file is part of SoCLib, GNU LGPLv2.1.
 * 
 * SoCLib is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; version 2.1 of the License.
 * 
 * SoCLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with SoCLib; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * SOCLIB_LGPL_HEADER_END
 *
 * Copyright (c) UPMC, Lip6, Asim
 *         Etienne Le Grand <etilegr@hotmail.com>, 2009
 *
 */
#ifndef SOCLIB_CABA_SIGNAL_HHT_SIGNALS_H_
#define SOCLIB_CABA_SIGNAL_HHT_SIGNALS_H_

#include <string>
#include <systemc>
#include "hht_param.h"
#include "hht_flits.h"
#include "fifo_signals.h"

namespace soclib { namespace caba {

/**
 * HHT Initiator port
 */
template <typename hht_param>
class HhtSignals
{
public:
	soclib::caba::FifoSignals<typename hht_param::ctrl_t> ctrlPC;
	soclib::caba::FifoSignals<typename hht_param::ctrl_t> ctrlNPC;
	soclib::caba::FifoSignals<typename hht_param::data_t> dataPC;
	soclib::caba::FifoSignals<typename hht_param::data_t> dataNPC;
	soclib::caba::FifoSignals<typename hht_param::ctrl_t> ctrlR;
	soclib::caba::FifoSignals<typename hht_param::data_t> dataR;

#define ren(x) x(((std::string)(name_ + "_"#x)).c_str())

    HhtSignals(std::string name_ = (std::string)sc_core::sc_gen_unique_name("hht"))
        : ren(ctrlPC),
          ren(ctrlNPC),
          ren(dataPC), 
          ren(dataNPC),  
          ren(ctrlR),
          ren(dataR)
    {
    }
#undef ren

    void trace( sc_core::sc_trace_file* tf, const std::string &name )
    {
        ctrlPC.trace(tf,name+"_ctrlPC");
        ctrlNPC.trace(tf,name+"_ctrlNPC");
        dataPC.trace(tf,name+"_dataPC");
        dataNPC.trace(tf,name+"_dataNPC");
        ctrlR.trace(tf,name+"_ctrlR");
        dataR.trace(tf,name+"_dataR");
    }
};

}}

#endif /* SOCLIB_CABA_SIGNAL_HHT_SIGNALS_H_ */

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4

