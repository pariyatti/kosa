# Kosa Ops

This folder contains all the necessary code to deploy the Kosa app. Please
read the [How It Works](https://github.com/pariyatti/kosa/tree/master/ops#how-it-works)
explanation at the bottom of this README before attempting any of these
commands.

## 1. One-time setup: prereqs

### Install the latest Ansible

If you aren't sure which method to use to install Ansible, it is safest to
install it through `pip`:

``` sh
sudo easy_install pip
sudo pip install ansible --quiet
```

### Install Ansible dependencies

``` sh
ansible-galaxy collection install ansible.posix
```

### Install Deploy Key

To seed the database with "Looped" feeds, the server will require access
to the private https://github.com/pariyatti/Daily_emails_RSS/ repo. A
deploy key for this repo is provided in the `vault` under `Deployment` =>
`~/.kosa/kosa_key` and `~/.kosa/kosa_key.pub`. Copy the contents of those
secrets to files with those same names on your _local computer._ The
Ansible scripts will use those local files to push the keys to the server.

## 2. One-time setup: provisioning servers

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

Then provision the box:

``` sh
ansible-playbook --limit "kosa-staging.pariyatti.app" -i hosts provision.yml
```

Replace the `--limit` parameter with your target host. It is possible to provision
all the boxes at once by eliding the `--limit` parameter but you probably never
want to do that.

## 3. Deployment

After the server has been provisioned, we use this command to deploy Kosa:

``` sh
ansible-playbook --limit "kosa-staging.pariyatti.app" -i hosts deploy.yml
```

## 4. Seed Data (Looped TXTs)

After Kosa is deployed the first time, we use this command to add seed data.
It adds Looped `Pali Word`, `Words of Buddha`, and `Daily Doha` cards to the db:

``` sh
ansible-playbook --limit "kosa-staging.pariyatti.app" -i hosts seed_looped_txt.yml
```

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
