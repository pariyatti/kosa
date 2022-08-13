#!/usr/bin/env bash
# This script is run by terraform, if you need to run it manually, run it from the ops/scripts/aws_lightsail directory.

if python3 -c "import boto3" &>/dev/null; then
    echo 'boto3 is installed, proceeding..'
else
    echo 'Cannot find boto3 on local machine, please install it and try again'
fi

export AWS_PROFILE=pariyatti
export AWS_DEFAULT_REGION=us-east-1

aws sts get-caller-identity

python3 ./lightsail_enable_auto_snapshots.py
python3 ./lightsail_enable_alarms.py
