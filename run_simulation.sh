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
echo "Run report"
Rscript Rmd/parameters.R  nc_test nc_outputs
Rscript -e "rmarkdown::render('Rmd/report.Rmd', output_file = '../nc_test.html')"
