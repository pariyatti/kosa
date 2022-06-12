#!/usr/bin/env bash

cd /srv/kosa-user/kosa/ops/scripts/backups
pipenv install
pipenv run python3 /srv/kosa-user/kosa/ops/scripts/backups/data_backup.py
