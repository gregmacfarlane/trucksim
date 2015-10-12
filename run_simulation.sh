#!/bin/bash

# This script runs the MATSim scenario and post-processing scripts.
scenario="5day_5pct_2011_config"

# 1 -------------------------------------
# This command runs the MATSim scenario based on the configuration file
# specified.
echo "Running simulation"
java -classpath "bin:libs/*"\
 -Xmx60G\
 trucksim.Controller ./${scenario}.xml


# 2 ------------------------------------
# Create run report
echo "Creating report"
Rscript Rmd/parameters.R  ${scenario} ${scenario}
Rscript -e "rmarkdown::render('Rmd/report.Rmd', output_file = '../${scenario}.html')"


# 3 ------------------------------------
#echo "Getting link daily volumes."
#java -classpath "bin:libs/*"\
# trucksim.VolumeCalculator ./${scenario}_config.xml \
# ./outputs/${scenario}/ITERS/it.10/${scenario}.10.events.xml.gz \
# ./outputs/${scenario}/daily_link_volumes.csv
