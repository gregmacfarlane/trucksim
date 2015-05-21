# trucksim
A national simulation of long-distance freight movements.

MATSim takes two primary inputs
  - network: created with `trucksim_network`
  - plans: created from FAF flow data with `trucksim_disagg`

Running the shell script in `run_simulation.sh` will run the simulation
according to the configuration options, print a simulation report, and save
a table of daily link volumes to the outputs folder.

I highly recommend that any users complete the MATSim tutorials to learn about
configuration files, etc.

A set of the `matsim-0.6.0` Java libraries is included in this repository.


### Attribution
This code is open to the public. We request that all derivative work please cite

  > Macfarlane, G.S. and Donnelly, R. (2015). A national simulation of freight flows.
