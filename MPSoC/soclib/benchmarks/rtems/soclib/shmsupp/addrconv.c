/*  Shm_Convert_address
 *
 *  This MVME136 has a "normal" view of the VME address space.
 *  No address range conversion is required.
 *
 *  Input parameters:
 *    address - address to convert
 *
 *  Output parameters:
 *    returns - converted address
 *
 *  COPYRIGHT (c) 1989-1999.
 *  On-Line Applications Research Corporation (OAR).
 *
 *  The license and distribution terms for this file may be
 *  found in the file LICENSE in this distribution or at
 *  http://www.rtems.com/license/LICENSE.
 *
 *  $Id: addrconv.c,v 1.9 2003/09/04 18:51:59 joel Exp $
 */

#include <rtems.h>
#include <bsp.h>
#include <shm_driver.h>

void *Shm_Convert_address(
  void *address
)
{
  return ( address );
}
