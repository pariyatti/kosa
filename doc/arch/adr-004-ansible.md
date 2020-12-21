# Architecture Decision Record: Ansible

## Context

We were uncertain where, when, or how we would deploy the final version of `kosa` to the public. For the moment, we have deployed `kosa` to a very generic Ubuntu environment on DigitalOcean.

## Decision

Ansible offers largely platform-agnostic tooling for operations automation. If and when we move cloud providers, we can take our Ansible scripts with us.

We are using Ansible for deployments.

## Status

Accepted.

## Consequences

1. Deploying Rails on Ansible has been made easier through the use of a Capistrano port called (of course) Ansistrano.
2. Ansible almost definitely ties our server infrastructure to Linux.
3. Ansible has very thorough documentation but a steep learning curve.
