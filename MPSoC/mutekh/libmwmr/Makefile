
DIR_CFLAGS = -D_HEXO_INTTYPES_DEPRECATED

ifeq ($(CONFIG_MWMR_PTHREAD),defined)
objs += mwmr_pthread.o
endif

ifeq ($(CONFIG_MWMR_SOCLIB),defined)
ifeq ($(CONFIG_MWMR_LOCKFREE),defined)
objs += mwmr_soclib_lockfree.o
else
objs += mwmr_soclib.o
endif
endif

doc_headers = mwmr/mwmr.h
