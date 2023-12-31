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
 * Copyright (c) UPMC, Lip6
 *         Nicolas Pouillon <nipo@ssji.net>, 2009
 *
 * Maintainers: nipo
 */
#ifndef SOCLIB_VCI_XICU_H
#define SOCLIB_VCI_XICU_H

#include <systemc>
#include "vci_target_fsm.h"
#include "caba_base_module.h"
#include "mapping_table.h"

namespace soclib {
namespace caba {

template<typename vci_param>
class VciXicu
	: public caba::BaseModule
{
private:
    std::list<soclib::common::Segment>              m_seglist;
    soclib::caba::VciTargetFsm<vci_param, true>     m_vci_fsm;

    const size_t                                    m_pti_count;
    const size_t                                    m_hwi_count;
    const size_t                                    m_wti_count;
    const size_t                                    m_irq_count;

    uint32_t*                                       r_msk_pti;
    uint32_t*                                       r_msk_wti;
    uint32_t*                                       r_msk_hwi;
    uint32_t                                        r_pti_pending;
    uint32_t                                        r_wti_pending;
    uint32_t                                        r_hwi_pending;
    uint32_t                                        *r_pti_per;
    uint32_t                                        *r_pti_val;
    uint32_t                                        *r_wti_reg;

    uint32_t                                        m_clock_cycles;


    bool on_write( int                        seg, 
                   typename vci_param::addr_t addr, 
                   typename vci_param::data_t data, 
                   int                        be );
    bool on_read(  int                        seg, 
                   typename vci_param::addr_t addr, 
                   typename vci_param::data_t &data );

    void transition();
    void genMoore();


protected:
    SC_HAS_PROCESS(VciXicu);

public:
    sc_core::sc_in<bool>                           p_clk;
    sc_core::sc_in<bool>                           p_resetn;
    soclib::caba::VciTarget<vci_param>             p_vci;
    sc_core::sc_out<bool>*                         p_irq;
    sc_core::sc_in<bool>*                          p_hwi;

	~VciXicu();

	VciXicu(
		sc_core::sc_module_name name,
		const soclib::common::MappingTable &mt,
		const soclib::common::IntTab &index,
        size_t pti_count,
        size_t hwi_count,
        size_t wti_count,
        size_t irq_count);

    soclib_static_assert(vci_param::B == 4);
};

}}

#endif /* SOCLIB_VCI_XICU_H */

// Local Variables:
// tab-width: 4
// c-basic-offset: 4
// c-file-offsets:((innamespace . 0)(inline-open . 0))
// indent-tabs-mode: nil
// End:

// vim: filetype=cpp:expandtab:shiftwidth=4:tabstop=4:softtabstop=4

