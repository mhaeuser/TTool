
DIR_CFLAGS = -D_HEXO_INTTYPES_DEPRECATED

objs =						\
cpu.o						\
init.o						\
context.o					\
interrupt.o

ifeq ($(CONFIG_HEXO_IPI), defined)
objs += ipi.o
endif

doc_headers = hexo/atomic.h hexo/cpu.h hexo/ipi.h hexo/mmu.h            \
	hexo/interrupt.h hexo/local.h hexo/error.h hexo/endian.h        \
	hexo/context.h hexo/iospace.h hexo/segment.h hexo/init.h        \
	hexo/lock.h hexo/types.h hexo/vmem.h hexo/ordering.h            \
	hexo/power.h
