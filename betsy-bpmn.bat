@echo off
echo "Starting with the following parameters: %*"
gradlew run -Pargs="bpmn %*"
