# This is a Dockerfile to create an image with betsy and the engine #{engine.name} installed.
# Version 1.0
FROM betsy
MAINTAINER Simon Harrer (simon.harrer@uni-bamberg.de)

ARG engine

# install #{engine.real_name}
RUN ./betsy engine $engine install
