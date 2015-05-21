#!/bin/bash

# This script runs the MATSim scenario and post-processing scripts.

# 1 -------------------------------------
# This command runs the MATSim scenario based on the configuration file
# specified.
echo "Running simulation"
java -classpath "bin:libs/*"\
 trucksim.Controller ./nc_config.xml


# 2 ------------------------------------
# Create run report
echo "Creating report"
Rscript Rmd/parameters.R  nc_test nc_outputs
Rscript -e "rmarkdown::render('Rmd/report.Rmd', output_file = '../nc_test.html')"


# 3 ------------------------------------
echo "Getting link daily volumes."
java -classpath "bin:libs/*"\
 trucksim.VolumeCalculator ./nc_config.xml \
 ./outputs/nc_outputs/ITERS/it.50/nc_test.50.events.xml.gz \
 ./outputs/nc_outputs/daily_link_volumes.csv
