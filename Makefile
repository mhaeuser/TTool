export JAVAC  		= javac
export JAR    		= jar
JAVADOC			= javadoc
MAKE			= make -s
TAR			= tar
GZIP			= gzip
PREFIX			= [BASE]             

export TTOOL_PATH 	:= $(shell /bin/pwd)

define HELP_message
How to compile TTool:
---------------------
make all                        builds TTool and produces the jar files in bin/
make ttool			builds TTool (but do not produce the jar of companion software)

Usual targets:
---------------
make (help)                     prints this help
make documentation              generates the documentation of java classes using javadoc
make release                    to prepare a new release for the website. It produces the release.tgz and releaseWithSrc.tgz files in releases/
make test                       tests on TTool.
make publish_jar                places ttool.jar in perso.telecom-paristech.fr/docs/ttool.jar. Must have the right ssh key installed for this
make clean                      removes the .class
make ultraclean                 runs clean and removes the jar files in bin/ and the releases

Other targets:
--------------
make preinstall			generates a preinstall version of TTool for Linux, Windows and MacOS and publish them on perso.telecom-paristech.fr


Please report bugs or suggestions of improvements to:
  Ludovic Apvrille <ludovic.apvrille@telecom-paristech.fr>
endef
export HELP_message

.PHONY: ttool clean launcher graphminimize graphshow tiftranslator tmltranslator rundse remotesimulator webcrawler documentation help ultraclean publish_jar preinstall test

help:
	@echo "$$HELP_message"

FORCE:

# ======================================== 
# ========== SUB-PROJECTS BUILD ========== 
# ======================================== 
export TTOOL_SRC 		= $(TTOOL_PATH)/src/main/java
export GLOBAL_JAVA		= $(shell cd $(TTOOL_SRC); find . -name "*.java")
export TTOOL_RESOURCES		= $(TTOOL_PATH)/src/main/resources
export TTOOL_WEBCRAWLER_SRC 	= $(TTOOL_PATH)/src/main/java/web/crawler
export TTOOL_BIN 		= $(TTOOL_PATH)/bin
export TTOOL_LIBS		= $(TTOOL_PATH)/libs
export TTOOL_LIBRARIES		= $(wildcard $(TTOOL_LIBS)/*.jar)
export TTOOL_CLASSPATH		= $(subst $(eval) ,:,$(TTOOL_LIBRARIES))

export GLOBAL_CFLAGS		= -encoding "UTF8"

export TTOOL_DIR		= $(TTOOL_PATH)/ttool
export TTOOL_BINARY 		= $(TTOOL_BIN)/ttool.jar

export LAUNCHER_DIR		= $(TTOOL_PATH)/launcher
export LAUNCHER_BINARY 		= $(TTOOL_BIN)/launcher.jar

export GRAPHMINIMIZE_DIR	= $(TTOOL_PATH)/graphminimize
export GRAPHMINIMIZE_BINARY 	= $(TTOOL_BIN)/graphminimize.jar

export GRAPHSHOW_DIR		= $(TTOOL_PATH)/graphshow
export GRAPHSHOW_BINARY 	= $(TTOOL_BIN)/graphshow.jar

export TIFTRANSLATOR_DIR	= $(TTOOL_PATH)/tiftranslator
export TIFTRANSLATOR_BINARY 	= $(TTOOL_BIN)/tiftranslator.jar

export TMLTRANSLATOR_DIR	= $(TTOOL_PATH)/tmltranslator
export TMLTRANSLATOR_BINARY 	= $(TTOOL_BIN)/tmltranslator.jar

export RUNDSE_DIR		= $(TTOOL_PATH)/rundse
export RUNDSE_BINARY 		= $(TTOOL_BIN)/rundse.jar

export REMOTESIMULATOR_DIR	= $(TTOOL_PATH)/simulationcontrol
export REMOTESIMULATOR_BINARY 	= $(TTOOL_BIN)/simulationcontrol.jar

export WEBCRAWLER_CLIENT_DIR	= $(TTOOL_PATH)/webcrawler/client
export WEBCRAWLER_CLIENT_BINARY	= $(TTOOL_BIN)/webcrawler-client.jar

export WEBCRAWLER_SERVER_DIR	= $(TTOOL_PATH)/webcrawler/server
export WEBCRAWLER_SERVER_BINARY	= $(TTOOL_BIN)/webcrawler-server.jar

export JTTOOL_DIR		= $(TTOOL_PATH)/jttool
export JTTOOL_BINARY		= $(TTOOL_BIN)/jttool.jar

all: ttool launcher graphminimize graphshow tiftranslator tmltranslator rundse remotesimulator webcrawler

ttool: $(TTOOL_BINARY)

$(TTOOL_BINARY): FORCE
	@$(MAKE) -C $(TTOOL_DIR) -e $@

launcher: $(LAUNCHER_BINARY)

$(LAUNCHER_BINARY): FORCE
	@$(MAKE) -C $(LAUNCHER_DIR) -e $@

graphminimize: $(GRAPHMINIMIZE_BINARY)

$(GRAPHMINIMIZE_BINARY): FORCE
	@$(MAKE) -C $(GRAPHMINIMIZE_DIR) -e $@

graphshow: $(GRAPHSHOW_BINARY)

$(GRAPHSHOW_BINARY): FORCE
	@$(MAKE) -C $(GRAPHSHOW_DIR) -e $@

tiftranslator: $(TIFTRANSLATOR_BINARY)

$(TIFTRANSLATOR_BINARY): FORCE
	@$(MAKE) -C $(TIFTRANSLATOR_DIR) -e $@

tmltranslator: $(TMLTRANSLATOR_BINARY)

$(TMLTRANSLATOR_BINARY): FORCE
	@$(MAKE) -C $(TMLTRANSLATOR_DIR) -e $@

rundse: $(RUNDSE_BINARY)

$(RUNDSE_BINARY): FORCE
	@$(MAKE) -C $(RUNDSE_DIR) -e $@

remotesimulator: $(REMOTESIMULATOR_BINARY)

$(REMOTESIMULATOR_BINARY): FORCE
	@$(MAKE) -C $(REMOTESIMULATOR_DIR) -e $@

webcrawler: $(WEBCRAWLER_CLIENT_BINARY) $(WEBCRAWLER_SERVER_BINARY)

$(WEBCRAWLER_CLIENT_BINARY): FORCE
	@$(MAKE) -C $(WEBCRAWLER_CLIENT_DIR) -e $@

$(WEBCRAWLER_SERVER_BINARY): FORCE
	@$(MAKE) -C $(WEBCRAWLER_SERVER_DIR) -e $@

$(JTTOOL_BINARY): FORCE
	@$(MAKE) -C $(JTTOOL_DIR) -e $@

# ======================================== 
# ==========    DOCUMENTATION   ========== 
# ======================================== 
TTOOL_DOC			= $(TTOOL_PATH)/doc
export TTOOL_DOC_HTML 		= $(TTOOL_DOC)/html

DOCFLAGS			= $(GLOBAL_CFLAGS) -quiet -J-Xmx256m -classpath $(TTOOL_CLASSPATH) -d $(TTOOL_DOC_HTML)

documentation: $(patsubst %,$(TTOOL_SRC)/%,$(GLOBAL_JAVA))
	@echo "$(PREFIX) Generating Javadoc"
	@$(JAVADOC) $(DOCFLAGS) $^

# ======================================== 
# ==========      RELEASES      ========== 
# ======================================== 
TTOOL_PRIVATE 			?= $(TTOOL_PATH)/../TTool-Private

PROD_USERNAME			= apvrille
PROD_ADDRESS			= ssh.enst.fr
PROD_PATH			= public_html/docs

TTOOL_DOC_SOCLIB_USERGUIDE_DIR 	= $(TTOOL_DOC)/documents_soclib/USER_GUIDE
TTOOL_DOC_SOCLIB_USERGUIDE_CMD 	= make user_guide
TTOOL_DOC_SOCLIB_INSTALLATIONGUIDE_DIR 	= $(TTOOL_DOC)/documents_soclib/INSTALLATION_GUIDE
TTOOL_DOC_SOCLIB_INSTALLATIONGUIDE_CMD 	= make installation_guide
TTOOL_MODELING			= $(TTOOL_PATH)/modeling
TTOOL_SIMULATORS 		= $(TTOOL_PATH)/simulators
TTOOL_FIGURES 			= $(TTOOL_PATH)/figures
TTOOL_EXECUTABLECODE 		= $(TTOOL_PATH)/executablecode
TTOOL_MPSOC 			= $(TTOOL_PATH)/MPSoC
TTOOL_STD_RELEASE 		= $(TTOOL_PATH)/release
TTOOL_TARGET_RELEASE 		= $(TTOOL_PATH)/TTool_install
TTOOL_TARGET 			= $(TTOOL_TARGET_RELEASE)/TTool
TTOOL_TARGET_WINDOWS		= $(TTOOL_TARGET_RELEASE)/Windows
TTOOL_TARGET_MACOS		= $(TTOOL_TARGET_RELEASE)/MacOS
TTOOL_TARGET_LINUX		= $(TTOOL_TARGET_RELEASE)/Linux

BASERELEASE			= $(TTOOL_STD_RELEASE)/baseRelease.tar
STDRELEASE			= $(TTOOL_STD_RELEASE)/release.tgz
ADVANCED_RELEASE		= $(TTOOL_STD_RELEASE)/releaseWithSrc.tgz
TTOOL_PREINSTALL_LINUX 		= $(TTOOL_STD_RELEASE)/ttoollinux.tgz
TTOOL_PREINSTALL_WINDOWS 	= $(TTOOL_STD_RELEASE)/ttoolwindows.tgz
TTOOL_PREINSTALL_MACOS 		= $(TTOOL_STD_RELEASE)/ttoolmacos.tgz

TTOOL_LOTOS_H		= $(patsubst $(TTOOL_DIR)/runtime/%,$(TTOOL_BIN)/%,$(wildcard $(TTOOL_DIR)/runtime/spec*))

RELEASE_STD_FILES_XML 	= $(patsubst %,$(TTOOL_MODELING)/%,\
			  TURTLE/manual-HW.xml \
			  TURTLE/WebV01.xml \
			  TURTLE/Protocol_example1.xml \
			  TURTLE/BasicExchange.xml \
			  DIPLODOCUS/SmartCardProtocol.xml \
			  TURTLE/ProtocolPatterns.xml \
			  CTTool/COCOME_V50.xml \
			  AVATAR/CoffeeMachine_Avatar.xml \
			  AVATAR/Network_Avatar.xml \
			  AVATAR/MicroWaveOven_SafetySecurity_fullMethodo.xml)
RELEASE_STD_FILES_LIB 	= $(patsubst %,$(TTOOL_MODELING)/%,\
			  TURTLE/TClock1.lib \
			  TURTLE/TTimerv01.lib)
RELEASE_STD_FILES_LICENSES 	= $(patsubst %,$(TTOOL_DOC)/%,\
			     	  LICENSE \
				  LICENSE_CECILL_ENG \
				  LICENSE_CECILL_FR)
TTOOL_EXE 		= $(patsubst %,$(TTOOL_DOC)/%,\
			  ttool_linux.exe \
			  ttool_macosx.exe \
			  ttool_windows.bat)
TTOOL_CONFIG_SRC 	= $(patsubst %,$(TTOOL_DOC)/%,\
			  config_linux.xml \
			  config_macosx.xml \
			  config_windows.xml)

release: $(STDRELEASE) $(ADVANCED_RELEASE)

$(TTOOL_STD_RELEASE)/%.tgz: $(TTOOL_STD_RELEASE)/%.tar
	@$(GZIP) -c $< > $@

$(STDRELEASE:.tgz=.tar): $(BASERELEASE:.tgz=.tar)
	@echo "$(PREFIX) Generating standard release"
	@cp $< $@
# LOTOS
	@mkdir -p $(TTOOL_TARGET)/lotos
	@cp $(TTOOL_DOC)/README_lotos $(TTOOL_TARGET)/lotos
#NC
	@mkdir -p $(TTOOL_TARGET)/nc
	@cp $(TTOOL_DOC)/README_nc $(TTOOL_TARGET)/nc

	@cd $(TTOOL_DOC_SOCLIB_USERGUIDE_DIR)/&&$(TTOOL_DOC_SOCLIB_USERGUIDE_CMD)
	@cp $(TTOOL_DOC_SOCLIB_USERGUIDE_DIR)/build/user_guide.pdf  $(TTOOL_TARGET)/doc/prototyping_with_soclib_user_guide.pdf
	@cd $(TTOOL_DOC_SOCLIB_INSTALLATIONGUIDE_DIR)/&&$(TTOOL_DOC_SOCLIB_INSTALLATIONGUIDE_CMD)	
	@cp $(TTOOL_DOC_SOCLIB_INSTALLATIONGUIDE_DIR)/build/installation_guide.pdf  $(TTOOL_TARGET)/doc/prototyping_with_soclib_installation_guide.pdf

# Figures
	@cp $(TTOOL_FIGURES)/Makefile $(TTOOL_TARGET)/figures
	@cp $(TTOOL_FIGURES)/mli.mk $(TTOOL_TARGET)/figures
# JTTool
	@mkdir -p $(TTOOL_TARGET)/java
	@cp $(JTTOOL_BINARY) $(TTOOL_TARGET)/java
	@cp $(TTOOL_DOC)/README_java $(TTOOL_TARGET)/java
# Basic bin
	@cp $(TTOOL_EXE) $(TTOOL_TARGET)/
	@cp $(TTOOL_LOTOS_H) $(TTOOL_TARGET)/bin
	@$(TAR) uf $@ -C $(TTOOL_TARGET_RELEASE) TTool/lotos TTool/nc TTool/bin TTool/java TTool/figures TTool/nc TTool/lotos TTool/doc/prototyping_with_soclib_installation_guide.pdf TTool/doc/prototyping_with_soclib_user_guide.pdf  $(patsubst $(TTOOL_DOC)/%,TTool/%,$(TTOOL_EXE))

$(ADVANCED_RELEASE:.tgz=.tar): $(STDRELEASE:.tgz=.tar) documentation
	@echo "$(PREFIX) Generating advanced release"
	@cp $< $@
	@cp -r $(TTOOL_DOC_HTML) $(TTOOL_TARGET)/doc/srcdoc
	@mkdir -p $(TTOOL_TARGET)/src
	@cp -R $(TTOOL_SRC)/* $(TTOOL_TARGET)/src
	@cp -r $(TTOOL_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(LAUNCHER_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(GRAPHMINIMIZE_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(GRAPHSHOW_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(TIFTRANSLATOR_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(TMLTRANSLATOR_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(RUNDSE_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(REMOTESIMULATOR_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(WEBCRAWLER_CLIENT_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@cp -r $(WEBCRAWLER_SERVER_DIR)/src/main/java/* $(TTOOL_TARGET)/src
	@find $(TTOOL_TARGET)/src -type f -not \( -name '*.java' -o -name '*.gif' -o -name '*.jjt' -o -name '*.txt' \) -a -exec rm -f {} \;
	@cp -R $(TTOOL_DOC)/README_src $(TTOOL_TARGET)/src
	@$(TAR) uf $@ -C $(TTOOL_TARGET_RELEASE) TTool/doc/srcdoc TTool/src

$(TTOOL_PREINSTALL_WINDOWS:.tgz=.tar): $(BASERELEASE:.tgz=.tar)
	@echo "$(PREFIX) Generating preinstall for Windows"
	@cp $< $@
	@mkdir -p $(TTOOL_TARGET_WINDOWS)/TTool/bin
	@$(TAR) xzvf $(TTOOL_PRIVATE)/stocks/proverif_windows.tar.gz -C $(TTOOL_TARGET_WINDOWS)
	@$(TAR) xzvf $(TTOOL_PRIVATE)/stocks/uppaal.tar.gz -C $(TTOOL_TARGET_WINDOWS)
	@cp $(TTOOL_DOC)/config_windows.xml $(TTOOL_TARGET_WINDOWS)/TTool/bin/config.xml
	@cp $(TTOOL_DOC)/ttool_windows.bat $(TTOOL_TARGET_WINDOWS)/ttool.bat
	@$(TAR) uf $@ -C $(TTOOL_TARGET_WINDOWS) proverif uppaal TTool/bin/config.xml ttool.bat

$(TTOOL_PREINSTALL_MACOS:.tgz=.tar): $(BASERELEASE:.tgz=.tar)
	@echo "$(PREFIX) Generating preinstall for MacOS"
	@cp $< $@
	@mkdir -p $(TTOOL_TARGET_MACOS)/TTool/bin
	@$(TAR) xzf $(TTOOL_PRIVATE)/stocks/proverif_macos.tar.gz -C $(TTOOL_TARGET_MACOS)
	@$(TAR) xzf $(TTOOL_PRIVATE)/stocks/uppaal_macos.tar.gz -C $(TTOOL_TARGET_MACOS)
	@mv $(TTOOL_TARGET_MACOS)/uppaal* $(TTOOL_TARGET_MACOS)/uppaal
	@cp $(TTOOL_DOC)/config_macosx.xml $(TTOOL_TARGET_MACOS)/TTool/bin/config.xml
	@cp $(TTOOL_DOC)/ttool4preinstalllinux.exe $(TTOOL_TARGET_MACOS)/ttool.exe
	@$(TAR) uf $@ -C $(TTOOL_TARGET_MACOS) proverif uppaal TTool/bin/config.xml ttool.exe

$(TTOOL_PREINSTALL_LINUX:.tgz=.tar): $(BASERELEASE:.tgz=.tar)
	@echo "$(PREFIX) Generating preinstall for Linux"
	@cp $< $@
	@mkdir -p $(TTOOL_TARGET_LINUX)/TTool/bin
	@$(TAR) xzvf $(TTOOL_PRIVATE)/stocks/proverif_linux.tar.gz -C $(TTOOL_TARGET_LINUX)
	@$(TAR) xzvf $(TTOOL_PRIVATE)/stocks/uppaal.tar.gz -C $(TTOOL_TARGET_LINUX)
	@cp $(TTOOL_DOC)/config_linux.xml $(TTOOL_TARGET_LINUX)/TTool/bin/config.xml
	@cp $(TTOOL_DOC)/ttool4preinstalllinux.exe $(TTOOL_TARGET_LINUX)/ttool.exe
	@$(TAR) uf $@ -C $(TTOOL_TARGET_LINUX) proverif uppaal TTool/bin/config.xml ttool.exe

$(BASERELEASE:.tgz=.tar): $(JTTOOL_BINARY) $(TTOOL_BINARY) $(LAUNCHER_BINARY) $(TIFTRANSLATOR_BINARY) $(TMLTRANSLATOR_BINARY) $(RUNDSE_BINARY)
	@echo "$(PREFIX) Preparing base release"
	@rm -rf $(TTOOL_TARGET)
	@mkdir -p $(TTOOL_TARGET)
# modeling
	@mkdir -p $(TTOOL_TARGET)/modeling
	@cp $(RELEASE_STD_FILES_XML) $(TTOOL_TARGET)/modeling
	@cp $(TTOOL_DOC)/README_modeling $(TTOOL_TARGET)/modeling
# lib
	@mkdir -p $(TTOOL_TARGET)/lib
	@cp $(RELEASE_STD_FILES_LIB) $(TTOOL_TARGET)/lib
	@cp $(TTOOL_DOC)/README_lib $(TTOOL_TARGET)/lib
# DIPLODOCUS simulators
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/src_simulator/TEPE
	@mkdir -p $(TTOOL_TARGET)/simulators/c++2/lib
	@cp  $(TTOOL_SIMULATORS)/c++2/lib/README $(TTOOL_TARGET)/simulators/c++2/lib/
	@cp  $(TTOOL_SIMULATORS)/c++2/Makefile $(TTOOL_TARGET)/simulators/c++2
	@cp  $(TTOOL_SIMULATORS)/c++2/Makefile.defs $(TTOOL_TARGET)/simulators/c++2
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/app/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/app
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/arch/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/arch
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/ebrdd/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/ebrdd
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/evt/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/evt
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/sim/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/sim
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.cpp $(TTOOL_TARGET)/simulators/c++2/src_simulator/TEPE
	@cp  $(TTOOL_SIMULATORS)/c++2/src_simulator/TEPE/*.h $(TTOOL_TARGET)/simulators/c++2/src_simulator/TEPE
# Licenses
	@cp $(RELEASE_STD_FILES_LICENSES) $(TTOOL_TARGET)
# Main readme
	@cp $(TTOOL_DOC)/README $(TTOOL_TARGET)
#TML
	@mkdir -p $(TTOOL_TARGET)/tmlcode
	@cp $(TTOOL_DOC)/README_tml $(TTOOL_TARGET)/tmlcode
#UPPAAL
	@mkdir -p $(TTOOL_TARGET)/uppaal
	@cp $(TTOOL_DOC)/README_uppaal $(TTOOL_TARGET)/uppaal
# Proverif
	@mkdir -p $(TTOOL_TARGET)/proverif
	@cp $(TTOOL_DOC)/README_proverif $(TTOOL_TARGET)/proverif
# Graphs
	@mkdir -p $(TTOOL_TARGET)/graphs
	@cp $(TTOOL_DOC)/README_graph $(TTOOL_TARGET)/graphs/
# Figure
	@mkdir -p $(TTOOL_TARGET)/figures
	@cp $(TTOOL_DOC)/README_figure $(TTOOL_TARGET)/figures
# VCD
	@mkdir -p $(TTOOL_TARGET)/vcd
	@cp $(TTOOL_DOC)/README_vcd $(TTOOL_TARGET)/vcd
# Basic doc
	@mkdir -p $(TTOOL_TARGET)/doc
	@cp $(TTOOL_DOC)/README_doc $(TTOOL_TARGET)/doc
# AVATAR executable code
	@mkdir -p $(TTOOL_TARGET)/TToolexecutablecode
	@mkdir -p $(TTOOL_TARGET)/executablecode/src
	@mkdir -p $(TTOOL_TARGET)/executablecode/generated_src
	@cp $(TTOOL_EXECUTABLECODE)/Makefile $(TTOOL_TARGET)/executablecode/
	@cp $(TTOOL_EXECUTABLECODE)/Makefile.defs $(TTOOL_TARGET)/executablecode/
	@cp $(TTOOL_EXECUTABLECODE)/Makefile.forsoclib $(TTOOL_TARGET)/executablecode/
	@cp $(TTOOL_EXECUTABLECODE)/src/*.c $(TTOOL_TARGET)/executablecode/src/
	@cp $(TTOOL_EXECUTABLECODE)/src/*.h $(TTOOL_TARGET)/executablecode/src/
	@cp $(TTOOL_EXECUTABLECODE)/generated_src/README $(TTOOL_TARGET)/executablecode/generated_src/
# MPSOC
	@mkdir -p $(TTOOL_TARGET)/MPSoC
	@mkdir -p $(TTOOL_TARGET)/MPSoC/generated_topcell
	@mkdir -p $(TTOOL_TARGET)/MPSoC/generated_src
	@mkdir -p $(TTOOL_TARGET)/MPSoC/src
	@cp $(TTOOL_MPSOC)/Makefile $(TTOOL_TARGET)/MPSoC/
	@cp $(TTOOL_MPSOC)/Makefile.defs $(TTOOL_TARGET)/MPSoC/
	@cp $(TTOOL_MPSOC)/Makefile.forsoclib $(TTOOL_TARGET)/MPSoC/
	@cp $(TTOOL_MPSOC)/src/*.c $(TTOOL_TARGET)/MPSoC/src/
	@cp $(TTOOL_MPSOC)/src/*.h $(TTOOL_TARGET)/MPSoC/src/
	@cp $(TTOOL_MPSOC)/generated_src/README $(TTOOL_TARGET)/MPSoC/generated_src/
	@cp $(TTOOL_MPSOC)/generated_topcell/nbproc $(TTOOL_TARGET)/MPSoC/generated_topcell/
	@cp $(TTOOL_MPSOC)/generated_topcell/config_noproc $(TTOOL_TARGET)/MPSoC/generated_topcell/
# Basic bin
	@mkdir -p $(TTOOL_TARGET)/bin
	@cp $(TTOOL_DOC)/README_bin $(TTOOL_TARGET)/bin
	@cp $(TTOOL_BIN)/*.jar $(TTOOL_TARGET)/bin
	@mkdir -p $(TTOOL_STD_RELEASE)
	@$(TAR) cf $(BASERELEASE) -C $(TTOOL_TARGET_RELEASE) .

publish_jar: $(TTOOL_BINARY)
	@echo "$(PREFIX) Publishing standard and advanced releases"
	scp $< $(PROD_USERNAME)@$(PROD_ADDRESS):$(PROD_PATH)/
	ssh $(PROD_USERNAME)@$(PROD_ADDRESS) "chmod a+r $(PROD_PATH)/$(notdir $<)"

preinstall: $(TTOOL_PREINSTALL_WINDOWS) $(TTOOL_PREINSTALL_LINUX) $(TTOOL_PREINSTALL_MACOS)
	@echo "$(PREFIX) Publishing preinstall versions"
	scp $^ $(PROD_USERNAME)@$(PROD_ADDRESS):$(PROD_PATH)/

# ======================================== 
# ==========       TESTS        ========== 
# ======================================== 
test:
	@./gradlew test

# ======================================== 
# ==========       CLEAN        ========== 
# ======================================== 
clean:
	@$(MAKE) -C $(TTOOL_DIR) -e clean
	@$(MAKE) -C $(LAUNCHER_DIR) -e clean
	@$(MAKE) -C $(GRAPHMINIMIZE_DIR) -e clean
	@$(MAKE) -C $(GRAPHSHOW_DIR) -e clean
	@$(MAKE) -C $(TIFTRANSLATOR_DIR) -e clean
	@$(MAKE) -C $(TMLTRANSLATOR_DIR) -e clean
	@$(MAKE) -C $(RUNDSE_DIR) -e clean
	@$(MAKE) -C $(REMOTESIMULATOR_DIR) -e clean
	@$(MAKE) -C $(WEBCRAWLER_CLIENT_DIR) -e clean
	@$(MAKE) -C $(WEBCRAWLER_SERVER_DIR) -e clean
	@$(MAKE) -C $(JTTOOL_DIR) -e clean
	@rm -rf $(TTOOL_TARGET_RELEASE)
	@rm -f $(TTOOL_STD_RELEASE)/*.tar

ultraclean: clean
	@rm -rf $(TTOOL_BIN)
	@rm -rf $(TTOOL_DOC_HTML)
	@rm -rf $(TTOOL_STD_RELEASE)
