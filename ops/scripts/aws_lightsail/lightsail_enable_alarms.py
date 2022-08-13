import boto3

client = boto3.client("lightsail", region_name="us-east-1")

response = client.get_instances()

instances = response["instances"]


def enable_alarm(instance_name, alarm_name, metric_name, threshold):
    response = client.put_alarm(
        alarmName=alarm_name,
        metricName=metric_name,
        monitoredResourceName=instance_name,
        comparisonOperator="GreaterThanThreshold",
        threshold=threshold,
        evaluationPeriods=1,
        datapointsToAlarm=1,
        treatMissingData="notBreaching",
        contactProtocols=["Email"],
        notificationTriggers=["OK", "ALARM"],
        notificationEnabled=True,
    )
    print(
        response["operations"][0]["resourceName"]
        + " for "
        + instance_name
        + " status: "
        + response["operations"][0]["status"]
    )


def enable_instance_status_alarm(instance_name):
    enable_alarm(
        instance_name,
        "{0}-instance-status-check".format(instance_name),
        "StatusCheckFailed_Instance",
        1.0,
    )


def enable_cpu_utilization_alarm(instance_name):
    enable_alarm(
        instance_name,
        "{0}-instance-cpu-check".format(instance_name),
        "CPUUtilization",
        60.0,
    )


def enable_metrics(instance_name, force_update=False):
    existing_alarms = client.get_alarms(monitoredResourceName=instance_name)
    if existing_alarms["alarms"] and not force_update:
        print("Metrics already enabled for %s" % instance_name)
        enable_instance_status_alarm(instance_name)
        enable_cpu_utilization_alarm(instance_name)
    else:
        enable_instance_status_alarm(instance_name)
        enable_cpu_utilization_alarm(instance_name)


for instance in instances:
    instance_name = instance["name"]
    try:
        enable_metrics(instance_name)
    except Exception as e:
        print("Metrics not enabled for %s" % instance_name)
        print(e)
