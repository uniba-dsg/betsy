call environment.bat %1
shift
docker create --name %1 --cpu-shares %2 --device-read-bps=/dev/sda:%3mb  --device-write-bps=/dev/sda:%3mb --memory=%4mb %5 sh betsy %6 %7 %8 %9