@echo off
echo  [35m###############################################
echo  #                                             #
echo  #      [95mLaunching IdleRSC Botting Client[35m       #
echo  #                                             #
echo  ############################################### [0m

java -jar -Djava.net.preferIPv4Stack=true IdleRSC.jar

echo [91m CLIENT ERROR, CLOSING IN 5 MINUTES! [0m
TIMEOUT /t 300000
