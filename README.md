Welcome to What's Reckless, an Android app to let you know what the driving laws are for your state. Useful for when you are traveling cross country, so you don't run amuck of the local laws. 

Disclaimer: All info in this app is pulled from open source content. The App authors recommend that you follow speedlimits.

== Open Source Content: ==
 * html parsing - MIT license - http://jsoup.org/
 * icons - FREE LICENSE(WITH ATTRIBUTION) - http://www.flaticon.com/authors/freepik
 * LRU cache - Apache License - https://github.com/JakeWharton/DiskLruCache
 
 
== TODO ==
 * Add About panel which includes License and open source bits
 * Implement checking for other nagivation apps running and start up(Waze, Google Maps, etc)
 * Improve look and feel of information. Add some CSS and make it look decent.
 * Improve filter for reading aloud so doesn't read source URL
 * Setup background service to monitor when activity isn't active
 * Create overlay / notification for state line crossing
 * Update disclaimer
 * Update google play description
 * Improve location accquision time by reducing restriction to require a "fine" location. "Course" should be good enough to determine what state you're in (although fine is good if waze or google maps already are using it).
 * Notifiy if law requires:
   - Lights while windshield wipers are running
   - Fines tied to amount over speed limit
   - Requires you to move over if slow in left lane long
   - reckless threshold
   - radar and laser detectors allowed
   - Move vechicles out of roadway after a fender bender
