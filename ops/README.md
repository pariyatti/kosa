# Kosa Ops

This folder contains all the necessary code to deploy the Kosa app.

## Prereqs

Please install the latest ansible and then install the following dependencies:

``` sh
ansible-galaxy collection install ansible.posix
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

## First time setup

To provision the server initially, run the following command

``` sh
ansible-playbook server-setup.yml -i hosts
```

## Deployment

After the server has been provisioned once, we use this code for our app deployment

``` sh
ansible-playbook deploy.yml -i hosts
```
