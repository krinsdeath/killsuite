DeathCounter
---
Welcome to DeathCounter v3.0! After many revisions and some deliberation, I've decided to refactor this plugin yet again. As a result, anyone that was utilizing the API (for whatever inane reason) will likely be annoyed. I'm sorry for that.

Configuration
---
There's several new monsters and MySQL support is finally here!

Each entry in the economy section is a list, denoting minimum and maximum. Players can now be 'looted' by setting 'realism' to true, and you can set a static value range or a percentage range (determined by the 'percentage' key) for the looting process.

save-interval will change how often updates are rolled out to your database. The default setting is 300 (5 minutes). This is recommended for large servers to prevent overloading your DB (especially if you're running plugins like LogBlock or Hawkeye).