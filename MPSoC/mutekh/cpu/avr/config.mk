CPUTOOLS=avr-unknown-elf-

CPUCFLAGS=-Dmain=main_ -mmcu=$(CONFIG_CPU_AVR_GCCMCU)
CPUASFLAGS=-mmcu=$(CONFIG_CPU_AVR_GCCMCU)
CPULDFLAGS=--no-check-sections
TARGET_EXT=hex
TARGET_NO_LD_RELOC=1
