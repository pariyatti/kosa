# Architecture Decision Record: Rails

## Context

Initially, `kosa` had a chicken-egg problem between choosing a database (graph or relational?) and choosing a web framework. We explored a number of options for both, looking for something that would be very stable, long-lasting, and capable of supporting the Pariyatti library.

## Decision

We did not start with Rails. We started with Python: first Django, then Flask. However, the developers available for the initial work were more comfortable with Rails and also felt that the Rails ecosystem is sufficiently stable at this point. We have chosen Rails.

## Status

Accepted.

## Consequences

1. Future developers will need to be comfortable with Ruby.
2. Unfortunately, some libraries have required us to update them due to our Neo4j dependency (such as `carrierwave-neo4j` and `clearance`). This is a problem of the Neo4j / Ruby intersection, not Neo4j itself. But it is a problem nonetheless.
