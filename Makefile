.PHONY: run run-ui all clean

OUTPUT = YTPPlus.jar
PACKAGE = zone/arctic/ytpplus
LOC = src/$(PACKAGE)
_CLASS = TimeStamp Utilities EffectsFactory YTPGenerator MainApp
CLASS = $(patsubst %,$(LOC)/%.class,$(_CLASS))
SOURCES = $(patsubst %,$(LOC)/%.java,$(_CLASS))
COMPAT = 8

$(OUTPUT): $(SOURCES)
	javac -source $(COMPAT) -target $(COMPAT) $(LOC)/*.java
	cd src && jar cmfv ../manifest.mf ../$(OUTPUT) $(PACKAGE)/*.class

clean:
	rm $(OUTPUT) $(LOC)/*.class || true

clean-ui: clean
	cd ../YTPPlusUI && make clean || true

jar: $(OUTPUT)

run: jar
	exec java -classpath $(OUTPUT) zone.arctic.ytpplus.MainApp

run-ui: jar
	cd ../YTPPlusUI && make
	exec make -f ../YTPPlusUI/Makefile run-nocheck

all: jar
