library(methods)

args <- commandArgs(trailingOnly = TRUE)

params <- list()
params$run_id <- args[1]
params$out_folder <- args[2]

saveRDS(params, "./Rmd/params.rds")
