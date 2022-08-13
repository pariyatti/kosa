# How to run:

Please ensure that your shell has the correct AWS profile set, e.g. `export AWS_PROFILE=pariyatti`
OR

```bash
export AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
export AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

Make sure `boto3` is installed:

```bash
pip install boto3
```

And then simply run the scripts:

```
> python3 lightsail_enable_auto_snapshots.py
Automatic backups already enabled for kosa
Automatic backups already enabled for kosa-sandbox
```

```
> python3 lightsail_enable_metrics.py
kosa-instance-status-check for kosa status: Succeeded
kosa-instance-cpu-check for kosa status: Succeeded
kosa-sandbox-instance-status-check for kosa-sandbox status: Succeeded
kosa-sandbox-instance-cpu-check for kosa-sandbox status: Succeeded
```
