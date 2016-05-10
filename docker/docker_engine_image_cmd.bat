call environment.bat %1
cd %2
docker build --tag="%3" --build-arg engine=%4 .