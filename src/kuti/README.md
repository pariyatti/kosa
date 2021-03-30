# kuṭi [f.]: any single-roomed abode; a hut, cabin, cot, shed

## Clojure in Kuti (originally "Clojure in Cages")

This mini-project / micro-framework is the extraction of any reusable components from `kosa` which
need not be a part of the core codebase. This framework is loosely modelled on _Ruby on Rails_.
Rails does a good job of creating simple, repeatable, **inflexible** internal APIs. We have the
same aim here: Don't make things too fancy or too customizable. Resources generate 7 CRUD routes.
Database operations are synchronous. Etc. If you require something custom, put that code in `kosa`,
not in `kuti`.
