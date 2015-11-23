#!/bin/bash

# This script runs the MATSim scenario and post-processing scripts.
scenario="3day_3pct_2012"

# 1 -------------------------------------
# This command runs the MATSim scenario based on the configuration file
# specified.
echo "Running simulation"
java -cp "java/bin:java/libs/*" -Xmx60g -Xms60g Controller ./${scenario}_config.xml


# 2 ------------------------------------
# Create run report
echo "Creating report"
Rscript Rmd/parameters.R  ${scenario} ${scenario}
Rscript -e "rmarkdown::render('Rmd/report.Rmd', output_file = '../${scenario}.html')"


# 3 ------------------------------------
#echo "Getting link daily volumes."
java -classpath "java/bin:java/libs/*"\
 trucksim.VolumeCalculator ./${scenario}_config.xml \
 ./outputs/${scenario}/ITERS/it.20/${scenario}.20.events.xml.gz \
 ./outputs/${scenario}/daily_link_volumes.csv
