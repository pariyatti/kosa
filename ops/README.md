# Kosa Ops

This folder contains all the necessary code to deploy the Kosa app. Please
read the [How It Works](https://github.com/pariyatti/kosa/tree/master/ops#how-it-works)
explanation at the bottom of this README before attempting any of these
commands.

## 1. One-time setup: prereqs

### 1.a Install Terraform

Instructions at https://learn.hashicorp.com/tutorials/terraform/install-cli

Prefer `brew` on MacOS and `apt-get` on Linux.

### 1.b Install the latest Ansible

If you aren't sure which method to use to install Ansible, it is safest to
install it through `pip`. As always, Python _itself_ is best installed with [`pyenv`](https://github.com/pyenv/pyenv#installation).

``` sh
sudo easy_install pip
sudo pip install ansible --quiet
```

### 1.c Install Ansible dependencies

``` sh
ansible-galaxy collection install ansible.posix
```

### 1.d Install Deploy Key

To seed the database with "Looped" feeds, the server will require access
to the private https://github.com/pariyatti/Daily_emails_RSS/ repo.

A deploy key for this repo is provided in the `vault` under `Deployment` =>
`~/.kosa/kosa_key` and `~/.kosa/kosa_key.pub`. You can use keepassxc-cli to accomplish this, by running the following in the terminal:

```sh
keepassxc-cli show -a Notes pariyatti-devops.kdbx Deployment/~/.kosa/kosa_key.pub > ~/.kosa/kosa_key.pub

keepassxc-cli show -a Notes pariyatti-devops.kdbx Deployment/~/.kosa/kosa_key > ~/.kosa/kosa_key
```

OR

you can copy the contents of those secrets to files with those same names on your _local computer._ The Ansible scripts will use those local files to push the keys to the server.

### 1.e Configure AWS Access/Secret Keys

```sh
aws configure --profile pariyatti # or 'default', if pariyatti is the only AWS org you will access

```

### 1.f Install LightSail Key

TODO: replace with automated process?

#### Via the terminal with AWS cli

Please ensure that `jq` is available on your machine.

```sh
aws lightsail --output=json download-default-key-pair | jq -r '.privateKeyBase64' > ~/.kosa/LightsailDefaultKey.pem && chmod 400 ~/.kosa/LightsailDefaultKey.pem
```

#### Via the AWS console
1. Go to https://lightsail.aws.amazon.com/ls/webapp/home/instances
2. Click on the instance you provisioned (or the instance previously provisioned)
3. Under `Connect`, click "Download default key".
Rename this key to `~/.kosa/LightsailDefaultKey.pem` on your local machine

### 1.g Enable automatic snapshots for lightsail

Currently the terraform AWS provider lacks the ability to configure this and hence we are relying on a simple boto3 script that can be run once to enable/check automatic snapshots for all the lightsail instances in us-east-1 region.

Refer to [scripts/aws_lightsail/README.md](scripts/aws_lightsail/README.md) for more details

## 2. Provisioning servers (DIY)

Add your public SSH key to the DigitalOcean or Lightsail team **before**
creating a box.

Stand up a box with the following profile. You can use a $10/mo server
in staging instead, if you like. Pricing is similar between DigitalOcean
and Lightsail:

```
* $20/mo
* Ubuntu 20.04 LTS x64 (or newer LTS)
* 4GB RAM
* 2 CPU/cores
* 80GB disk
* 4 TB transfer
```

### 2.a Provision pre-requisites:

``` sh
ansible-playbook --become --limit "kosa-sandbox.pariyatti.app" -i hosts provision.yml
```

Replace the `--limit` parameter with your target host. It is possible to provision
all the boxes at once by eliding the `--limit` parameter but you probably never
want to do that.

### 2.b Deploy Kosa

``` sh
ansible-playbook --become --limit "kosa-sandbox.pariyatti.app" -i hosts deploy.yml
```

**NOTE:** This step can be run each time you would like to update the running code on the remote machine.

### 2.c Seed Data (Looped TXTs)

After Kosa is deployed the first time, we use this command to add seed data.
It adds Looped `Pali Word`, `Words of Buddha`, and `Daily Doha` cards to the db:

``` sh
ansible-playbook --become --limit "kosa-sandbox.pariyatti.app" -i hosts seed_looped_txt.yml
```

## 3. Provisioning servers using terraform

Terraform server setup files can be used to provision the server boxes and run the ansible playbooks locally on the machine running `terraform apply` as well.

It covers:

* Creating the appropriate Ubuntu 20.04 LTS Lightsail VM
* Updating the DNS records in AWS Route 53 to the newly provisioned resource's IP address
* Running ansible configuration management playbooks to configure and deploy the kosa app


Please ensure that you have the correct credentials configured in terminal for lightsail deployment. To verify run:

```sh
aws sts get-caller-identity
```

Currently the terraform statefile is stored within a S3 bucket names `pariyatti-tf-state-bucket` that is accessible via the pariyatti AWS account credentials.

Following three commands that are usually run in directories containing the terraform files:

```sh
terraform init # run once in each directory to fetch the terraform modules and configure the backend
terraform plan -out=terraform.plan
terraform apply "terraform.plan"
```

If you need to destro the lightsail instance and re-create it, depending on the environment, you command could be one of the following:
```
terraform destroy -target module.kosa-production

or

terraform destroy -target module.kosa-sandbox
```

**Note:** Running terraform plan and apply for a new server will create a DNS entry that does not resolve by the time Ansible runs, which will cause it to fail. As a workaround, temporarily set your local DNS resolution to '8.8.8.8'.

**Note:** Running terraform plan and apply on existing server will result in execution of ansible playbook `deploy.yml`. This is managed via dynamically updating the `build_number` to timestamp() in the triggers.

## Troubleshooting

You can view logs with:

```sh
journalctl -u kosa-app --since="30 min ago"
journalctl -u kosa-app > this-is-a-real-txt-file.log
```

View Caddy logs similarly:

```sh
sudo journalctl -u caddy
```

## Developing

To add a new secret/password to Ansible, decide what you want the variable to
be called when referenced in Ansible scripts (`YOUR_DESIRED_VARIABLE_NAME`) and
choose a password (`YOUR_NEW_PASSWORD`). The contents of `~/.kosa/ansible-password`
are in the vault, if you have not already created this file.

```sh
cp ~/.kosa/ansible-password > a_password_file
ansible-vault encrypt_string --vault-password-file a_password_file 'YOUR_NEW_PASSWORD' --name 'YOUR_DESIRED_VARIABLE_NAME'
```

Copy the results into `kosa/ops/ansible/secrets.yml`


## Monitoring

We have an `#alerts` channel on Discord which receives alerts via Webhook.
To add a new webhook, a Discord user must have the Admin role. Currently,
Steven Deobald, Tanmay Balwa, and Brihas Sarathy have the Admin role.

## How it works?

We'll be using ansible to setup our machines, and do incremental updates.

The main steps for deploying will be in `deploy.yml` which we will call with
ansible. For the time being someone will have to manually call this ansible
script on their machines. In the future we might be able to hook this up
automatically with Github Pipelines.

Currently we have 2 digital ocean droplets

1. kosa-staging
2. kosa-production

We will store information about these in our `hosts` file.

The deployment procedure performed by ansible will be as simple as a git pull,
and a systemd restart. Ansible will also first check if all required
dependencies are installed.

## Contributing

1. For terraform files please ensure that you run `terraform fmt` before checking in the files.
1. If possible, please update the visual representation of configuration by running `terraform graph | dot -Tsvg > graph.svg` in the same directory.
