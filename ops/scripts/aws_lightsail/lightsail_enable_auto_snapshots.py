import boto3

# There is no terraform implementation for turning on automatic snapshots for lighsail instances.
# Issue to track: https://github.com/hashicorp/terraform-provider-aws/issues/23688
# We want to enable snapshots for all lightsail VMs, which at this moment consists of only two
# This script will iterate through lightsail instances list and enable snapshot at 08:00 AM UTC

client = boto3.client("lightsail", region_name="us-east-1")

response = client.get_instances()

instances = response["instances"]


def enable_autosnapshot(instance_name):
    response = client.enable_add_on(
        resourceName=instance_name,
        addOnRequest={
            "addOnType": "AutoSnapshot",
            "autoSnapshotAddOnRequest": {"snapshotTimeOfDay": "08:00"},
        },
    )
    print("Automatic backups have been enabled for %s" % instance_name)
    return response


for instance in instances:
    instance_name = instance["name"]
    try:
        for addon in instance["addOns"]:
            if addon["name"] == "AutoSnapshot":
                if addon["status"] == "Enabled":
                    print("Automatic backups already enabled for %s" % instance_name)
                else:
                    response = enable_autosnapshot(instance_name)
    except:
        response = enable_autosnapshot(instance_name)
