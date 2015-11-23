

 
JFLAGS = -g
JC = javac
CP = -cp libs/*:java/bin/

srcdir = java/src
bindir = java/bin
sources = $(wildcard $(srcdir)/*.java)
classes = $(sources:$(srcdir)/%.java=$(bindir)/%.class)

j: $(classes)
	@echo "Classes compiled."

$(classes): $(bindir)/%.class:$(srcdir)/%.java
	@mkdir -p $(bindir)
	$(JC) -d $(@D) $(CP) $<

jclean:
	rm -rf java/bin/
