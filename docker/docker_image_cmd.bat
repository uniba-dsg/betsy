call environment.bat %1
cd %2
docker build --tag="%3" .