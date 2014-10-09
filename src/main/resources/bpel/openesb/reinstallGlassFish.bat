rem Instructions: just run this script - note: existing deployments, configurations etc. will be deleted

set __COMPAT_LAYER=RunAsInvoker
call %1 --silent --state %2

rem installation completed!


