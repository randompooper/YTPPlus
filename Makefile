.PHONY: run run-ui all clean

OUTPUT = YTPPlus.jar
PACKAGE = zone/arctic/ytpplus
LOC = src/$(PACKAGE)
_CLASS = TimeStamp Utilities EffectsFactory YTPGenerator MainApp
CLASS = $(patsubst %,$(LOC)/%.class,$(_CLASS))
SOURCES = $(patsubst %,$(LOC)/%.java,$(_CLASS))
COMPAT = -source 8 -target 8

$(OUTPUT): $(SOURCES)
	javac $(COMPAT) -classpath lib/commons-exec-1.3.jar $(LOC)/*.java
	cd src && jar cmfv ../manifest.mf ../$(OUTPUT) $(PACKAGE)/*.class

clean:
	rm $(OUTPUT) $(LOC)/*.class || true

jar: $(OUTPUT)

run: jar
	exec java -classpath lib/commons-exec-1.3.jar:$(OUTPUT) zone.arctic.ytpplus.MainApp

run-ui: jar
	cd ../YTPPlusUI && make
	exec java -classpath $(OUTPUT):YTPPlusUI.jar:lib/commons-exec-1.3.jar --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml,javafx.media ytpplusui.MainApp

all: jar
