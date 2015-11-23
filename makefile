
scenario = nc_faf4

simconfig = $(scenario)_config.xml
outdir = ouputs/$(scenario)
its = 20

 
JFLAGS = -g
JC = javac
CP = -cp java/libs/*:java/bin/
JM = -Xmx60g -Xms60g

srcdir = java/src
bindir = java/bin
sources = $(wildcard $(srcdir)/*.java)
classes = $(sources:$(srcdir)/%.java=$(bindir)/%.class)

# Run simulation based on config
sim: j $(simconfig)
	@echo "Running simulation from" $(simconfig)
	java $(CP) $(JM) Controller $(simconfig)

$(outdir)/daily_link_volumes.csv: $(outdir)/ITERS/it.$(its)/$(scenario).$(its).events.xml.gz
	java $(CP) VolumeCalculator $(simconfig) $< $@
	
# compile java classes
j: $(classes)
	@echo "Classes compiled."

$(classes): $(bindir)/%.class:$(srcdir)/%.java
	@mkdir -p $(bindir)
	$(JC) -d $(@D) $(CP) $<

jclean:
	rm -rf java/bin/
