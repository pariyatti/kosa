# Architecture Decision Record: Graph Data & Neo4j

## Context

We have spent an inordinate amount of time debating Ruby vs. Python (vs. Java) and Postgres vs. Neo4j. The Pariyatti data model will ultimately require very complex relationships not well-suited to a relational database. In anticipation of that point in time, we spent the early portion of the project exploring graph databases.

## Decision

If we anticipated stopping this project at the collection of artefacts (books, audio, videos, etc.) and tagging them with metadata, a graph database may not be necessary. It is possible to manipulate the metadata we want to apply to artefacts into a relational model... but it is awkward. However, we expect to begin collecting more and more literature (_"pariyatti"_) which crosses more and more lines: languages, scripts, cultures, countries, centuries.

We have stuck with Neo4j until now and it has been sufficient. Ruby/Rails support for Neo4j is not as strong as it is for ActiveRecord and Postgres, but our problems are primarily data-modeling and Information Architecture problems, not technical problems. Conversations with experts have suggested that our decision to use a graph database is a sensible one.

We will continue to use Neo4j.

Our commitment is strictly to graph data modeling, not Neo4j. If in the future we have the capacity to switch to WikiBase or if a graph database like `dgraph` becomes more popular, there is no harm in switching.

## Status

Accepted.

## Consequences

1. Future developers will need to familiarize themselves with Neo4j and ActiveGraph (previously `Neo4j.rb`).
2. Developers need to understand and appreciate that Neo4j does not have a static schema and to take great care in manipulating model schemas in the Rails application.
3. We must be aware that, ultimately, WikiBase is probably the best back-end tool for this job. For now, it is too heavy and complicated to serve as the data store for `kosa`.
4. If we continue to run into complications with Rubygems, such that we feel developer time is being wasted, it is not unreasonable to switch to Postgres as long as we have a serious discussion about the shortcomings of such a decision. Switching databases is a huge task.
5. As we have committed to graph-shaped data, the team designers have been encouraged to exploit that capability in their designs.
