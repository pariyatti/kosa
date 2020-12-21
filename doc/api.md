# Kosa's API for the Pariyatti Mobile App #

The mobile app prototype will need to consume Kosa APIs. Get the development version running, below. 
We will list API endpoints here as they become available. The goal will be to make the API endpoints very 
simple and reflective of the Pariyatti mobile app wireframes.

## Open Questions ##

- Cards: should we structure dohas as cards or not?

## Answered Questions ##

- Q: "Resources" is overloaded with web terminology. So is "Asset".
  - A: Let's go with `Artefacts`... yes, with the weird spelling. This way we're pretty much guaranteed to avoid naming collisions.
- Q: DB+API: UUID or Integer ids?
  - A: UUIDs. Neo4j prefers them and they're just saner anyway.
- Q: Diacritics in URLs?
  - A: no.
  - note the "Pali Word of the Day" example
  - substitute standard ASCII representations in URLs, please
  - diacritics in json is fine
- Q: Cards - is a "generic" card a supertype of all other Cards?
  - no, many fields are ignored so this isn't a real supertype
- Q: `to_json` in Ruby defaults to labeling types (such as `{"card": CardObj}`)... is this annoying in Dart deserialization?
  - A: yes. don't nest unnecessarily.
- Q: Cards - "CTA" (Call to Action) or just "button"?
  - A: just 'button'
- Q: URLs - do we want semantic URLs over documents?
  - A: yes.
  - the documents might have very hetergenous structures
  - there shouldn't be so many artefact types that routes become overwhelming
  - ex. `/api/artefacts/audiobooks`
  - https://jsonapi.org/ - we might want to implement some or all of this

## Stubbed ##

```
None - we'll try to implement real APIs over neo4j now but we might stub APIs if it helps the mobile team.
```

## Implemented ##

```
http://localhost:3000/api/today.json
```

## Working (Examples) ##

### High Level APIs ###

```
/api/today.json

=> {"today_cards":
    [{"card": Card}, {"card": Card}, {"card": Card}, ... ]}

# `Card` is heterogeneous based on its sub-type.
# It's possible we should consider implementing something like https://jsonfeed.org
```

### User Objects ###

```
Card (superclass):
=> {"id": "3386076e-566c-4acc-9816-3514e192852f", 
    "type": "", 
    "published_at": "2020-04-25 18:26:30.774998000 UTC",
    "bookmarkable": true,
    "shareable": false,
    "header": "USUALLY ALL CAPS" }

Card (type = 'stacked_inspiration')
=> {"id": "3386076e-566c-4acc-9816-3514e192852f",
    "type": "stacked_inspiration", 
    "published_at": "2020-04-25 18:26:30.774998000 UTC",
    "bookmarkable": true,
    "shareable": false,
    "header": "Inspiration of the Day",
    "image": {"url": "https://pariyatti.org/webu.jpg"},
    "text": "Ven. Webu Sayadaw was one of the most highly respected monks of the last century in Burma."}

Card (type = 'overlay_inspiration')
=> {"id": "3386076e-566c-4acc-9816-3514e192852f", 
    "type": "overlay_inspiration", 
    "published_at": "2020-04-25 18:26:30.774998000 UTC",
    "bookmarkable": true,
    "shareable": false,
    "header": "Inspiration of the Day",
    "image": {"url": "https://pariyatti.org/buddha.jpg"}, 
    "text": "Ataapi Sampajaanno Satima",
    "text_color": "white"}

Card (type = 'pali_word')
=> {"id": 123,
    "type": "pali_word",
    "audio_url": "http://download.pariyatti.org/pali/vipassisu.m.mp3",
    "pali": "vipassisuṃ",
    "translations":
        [{"language": "en", "translation": "insight"},
         {"language": "hi", "translation": " विशेष प्रकार से देखना"}]}

```

## Planned (Examples) ##

### High Level APIs ###

TODO

### Artefacts ###

```
/api/artefacts/audiobook/1

=> {"id": 123, "title": "content-type": "audio/mpeg", "language": "en", 
    "Curbing Anger, Spreading Love", "author": "Bhikkhu Visuddhacara", "url": "https://pariyatti.org/anger.mp3"}

/api/artefacts/book/1

=> {"id": 123, "language": "en", 
    "title": "The Elimination of Anger", "author": "Ven. K. Piyatissa Thera", "url": "https://store.pariyatti.org/Anger"}
```

### User Objects ###

```
/api/cards/1

=> {"id": 123, "type": "generic",  
    "title": "Stay Updated", 
    "header": "Sign up for the Pariyatti newsletter", 
    "image": "https://pariyatti.org/newsletter.png",
    "text": "We'll keep you updated with the latest news and updates."}

=> {"id": 123, "type": "words_of_the_buddha",  
    "sutta_reference": "Dhammapada 17.223", 
    "pali": "Akkodhena jine kodhaṃ;\nasādhuṃ sādhunā jine;\njine kadariyaṃ dānena;\nsaccenālikavādinaṃ.", 
    "translation_language": "en",
    "translation": "Overcome the angry by non-anger;\novercome the wicked by goodness;\novercome the miser by generosity;\novercome the liar by truth."
    "audio_url": "http://download.pariyatti.org/dwob/dhammapada_17_223.mp3"}

=> {"id": 123, "type": "doha",
    "TODO": "TODO"}

=> {"id": 123, "type": "topic_of_the_week", "alignment": "list", 
    "header": "When anger puts you down", "rows": [Audiobook, Book, ... ]}

    # See Artefacts for `Audiobook` and `Book` examples

=> {"id": 123, "type": "donation", 
    "icon": "https://pariyatti.org/heart-money-icon.png", 
    "header": "We are running a donation camp", "text": "We are generating funds for the upcoming pilgrimage to Nepal and India."}

=> {"id": 123, "type": "pilgrimage", 
    "header": "Along the Path - India &amp; Nepal", "text": "&quot;There are four places which should be (visited and) seen by a person of devotion,&quot; the Buddha said."}

=> {"id": 123, "type": "recommended_read", 
    "header": "Letters From the Dhamma Brothers",
    "image": "https://pariyatti.org/dhamma-brothers-cover.png", 
    "author": "Joey Phillips",
    "category": "Softcover Book",
    "page_count": 112
    "text": "As we know all too well, anger causes unhappiness to ourselves and to others. If we could only live with less anger and more love..."}

```

### Enums ###

These are just easier to list with a schema snippet than an example.

```
# Card type: { "type": { "enum": [ "generic",
                                    "words_of_the_buddha",
                                    "pali_word_of_the_day", 
                                    "doha", 
                                    "inspriation", 
                                    "topic_of_the_week", 
                                    "donation", 
                                    "pilgrimage", 
                                    "recommended_read" ] } }
# Inspiration alignment: { "alignment": {"enum": [ "stacked", "overlay" ]}}
# Topic of the Week alignment: { "alignment": {"enum": [ "list", "blurb" ]}}
```
