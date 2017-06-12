PREFIX			= [$(COLOR)TTOOL$(RESET)]            

MANIFEST		= $(TTOOL_DIR)/ttool.txt

DEPEND			= $(patsubst %,$(TTOOL_BIN)/%,$(shell cat $(MANIFEST) | grep Class-Path | cut -d: -f2))

LOCAL_SRC_DIR		= $(TTOOL_DIR)/src/main/java
LOCAL_RESOURCES_DIR 	= $(TTOOL_RESOURCES)
LOCAL_BUILD_DIR		= $(TTOOL_DIR)/build/classes/main
LOCAL_RUNTIME_DIR	= $(TTOOL_DIR)/runtime

LOCAL_JAVA	= $(shell cd $(LOCAL_SRC_DIR); find . -name "*.java")
LOCAL_CLASS	= $(patsubst %.java,%.class,$(LOCAL_JAVA))
LOCAL_RT_DEP	= $(patsubst $(LOCAL_RUNTIME_DIR)/%,$(TTOOL_BIN)/%,$(wildcard $(LOCAL_RUNTIME_DIR)/*))

CFLAGS		= $(GLOBAL_CFLAGS) -sourcepath $(TTOOL_SRC):$(LOCAL_SRC_DIR) -classpath $(TTOOL_CLASSPATH) -d $(LOCAL_BUILD_DIR)
JARFLAGS	= cmf
JARRSRCFLAGS	= uf

.PHONY: compile clean

$(TTOOL_BINARY): compile $(LOCAL_RT_DEP) $(DEPEND)
	@echo "$(PREFIX) Archiving"
	@mkdir -p $(TTOOL_BIN)
	@$(JAR) $(JARFLAGS) $(MANIFEST) $@ -C $(LOCAL_BUILD_DIR) .
	@$(JAR) $(JARRSRCFLAGS) $@ -C $(LOCAL_RESOURCES_DIR) . 2>/dev/null

$(TTOOL_BIN)/%.jar: $(TTOOL_LIBS)/%.jar
	@mkdir -p $(TTOOL_BIN)
	@cp $< $@

$(TTOOL_BIN)/%: $(LOCAL_RUNTIME_DIR)/%
	@mkdir -p $(TTOOL_BIN)
	@cp -r $< $@

compile: $(patsubst %,$(LOCAL_SRC_DIR)/%,$(LOCAL_JAVA)) $(patsubst %,$(TTOOL_SRC)/%,$(GLOBAL_JAVA))
	@echo "$(PREFIX) Compiling"
	@mkdir -p $(LOCAL_BUILD_DIR)
	@$(JAVAC) $(CFLAGS) $(patsubst %,$(LOCAL_SRC_DIR)/%,$(LOCAL_JAVA))

clean:
	@rm -rf $(LOCAL_BUILD_DIR)