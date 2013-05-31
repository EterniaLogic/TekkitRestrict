Global:
- Major code cleanup
- Small bugfixes
- Fixes in typos
- Working on adding more colour to the output of tekkitrestrict.

Dupes and hacks:
- Removed hackban option, as Anti-hack is currently not very trustworty.
- Added option to kick people when they dupe.
- Dupe and hack broadcasts now support colors (&0-&f, &k-&o and &r)
- Dupe and hack broadcasts now require a permission to see them:
	- tekkitrestrict.notify.dupe and tekkitrestrict.notify.hack
- Option to choose kick method; Kick from console or default.
	- Useful if you have a plugin that keeps track of kicks and you would like to use its system.

EMC:
- Changed the way EMC is set.
	- An EMC value of 0 now actually removes the EMC value of that item.
	- Improvement to the way the strings in the config are handled.
	- "<id>[:<data>] <EMC>" where data can consist of multiple values separated by comma's. Ranges can be specified with <begin>-<end>
	- "10:2-4,12 100" Will set the EMC values of 10:2, 10:3, 10:4 and 10:12 to 100.

SafeZones:
- Improved GriefPrevention support.
	- Added option to only allow adminclaims to be safezones
	- You can now set if a claim should be a safezone by using /tr admin safezone addgp <name> while you are standing in it.
	* Bug: if you remove the claim, tekkitrestrict still thinks there is a safezone

Commands:
- Added alot of permissions for all the sections of the /tr command.

Developers:
- Started work on an API.
	- API currently has 2 classes, EMC and SafeZones.
	
Known bugs:
- I highly recommend you do NOT use the logfilter. There seems to be a bug with it which can cause the
whole server to freeze (players get disconnected, all plugins continue doing their stuff as they should, 
but the server is unreachable)