# Architecture Decision Record: API Modeling

## Context

As `kosa` is a volunteer-run project under Pariyatti, it requires that we simplify our designs wherever possible.

## Decision

Preference will always be given to the mobile app. Mobile app development times are longer, deployment cycles are more complicated, and developers are less commonly available.

In practice, this means we will tailor `kosa` to provide an API which is almost perfectly symmetrical with the mobile app's user interface.

## Status

Accepted.

## Consequences

1. `kosa` will effectively have a second data layer intended for the sole purpose of supporting the mobile app API.
2. Entities in the mobile app data layer, such as Today Cards, should have no relationship whatsoever to library data. All data used in such entities should be **copied** such that if we were to extract the mobile data API into a separate service with a completely different database (say, Postgres) it would be very easy to do.
