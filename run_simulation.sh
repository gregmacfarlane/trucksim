#!/bin/bash

# This script runs the MATSim scenario and post-processing scripts.

# 1 -------------------------------------
# This command runs the MATSim scenario based on the configuration file
# specified.
echo "Running simulation"
java -classpath "bin:libs/*"\
 trucksim.Controller ./nc_config.xml
