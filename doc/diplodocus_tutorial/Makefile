SHELL=/bin/bash

# Top LaTeX document (without .tex extension)
TOP		= Tutorial
# Bibliography (if any)
#BIBLIO		= todaes2.bib

# Figures directory
FIGDIR		= figures/*

# Maximum number of pdflatex compilations
MAXPASSES	?= 5

# Main, common, Makefile
include ../mli.mk
#include Makefile.inc

# Main PDF depends on other PDFs generated from fig, dot, dia, gnuplot or
# inkscape formats. The GENERATED make variable is computed automatically by
# Makefile.inc, based on what is found in the FIGDIR directory.
$(TOP).pdf: $(GENERATED) $(wildcard $(FIGDIR)/*.JPG) $(wildcard $(FIGDIR)/*.jpg) $(wildcard $(FIGDIR)/*.png)

# Specific dependencies
$(TOP).pdf: $(wildcard *.tex)

