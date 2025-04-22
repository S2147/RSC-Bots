#!/bin/bash

# Wait until Google is reachable
until ping -c1 google.com &>/dev/null; do
	sleep2
done

nohup /home/ubuntu/plutonium/bot > /home/ubuntu/plutonium/output.log 2>&1 &

echo "Internet is up, running script!"