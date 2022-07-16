#!/usr/bin/env bash

# Populate variables that are needed for script execution
source /root/.bashrc
cd /srv/kosa-user/kosa/ops/scripts/backups
pipenv install
pipenv run python3 /srv/kosa-user/kosa/ops/scripts/backups/data_backup.py
