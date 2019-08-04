# Copyright 2019 randompooper
# This file is part of YTPPlus.
#
# YTPPlus is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# any later version.
#
# YTPPlus is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with YTPPlus.  If not, see <https://www.gnu.org/licenses/>.


.PHONY: run run-ui all clean

OUTPUT = YTPPlus.jar
PACKAGE = ytpplus
LOC = src/$(PACKAGE)
_CLASS = TimeStamp Utilities EffectsFactory YTPGenerator MainApp Pair
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
	exec java -classpath $(OUTPUT) ytpplus.MainApp

run-ui: jar
	cd ../YTPPlusUI && make
	exec make -f ../YTPPlusUI/Makefile run-nocheck

all: jar
