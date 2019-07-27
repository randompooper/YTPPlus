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

jar: $(OUTPUT)

run: jar
	exec java -classpath $(OUTPUT) zone.arctic.ytpplus.MainApp

run-ui: jar
	cd ../YTPPlusUI && make
	exec java -classpath $(OUTPUT):YTPPlusUI.jar --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml ytpplusui.MainApp

all: jar
