docker-machine env --shell cmd %1
FOR /f "tokens=*" %%i IN ('docker-machine env --shell cmd %1') DO %%i