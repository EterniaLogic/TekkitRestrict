NOTE: This is a beta release, not everything is working fully yet.\\
If you want to use this version, you have to let tekkitrestrict write new config files, as they have changed. You can keep your database file.

[TekkitRestrict v1.15 Beta Changes]\\
Global:
* Major code cleanup.
* Majorly improved efficiency.
* Small bugfixes
* Fixed typos
* Added more color to messages from tekkitrestrict.
* Better explanations for commands.

Dupes and hacks:
* Removed hackban option, as Anti-hack is currently not very trustworty.
* Added option to kick people when they dupe.
* Dupe and hack broadcasts now support colors (&0-&f, &k-&o and &r)
* Dupe and hack broadcasts now require a permission to see them:
	** tekkitrestrict.notify.dupe and tekkitrestrict.notify.hack
* Option to choose kick method; Kick from console or default.
	** Useful if you have a plugin that keeps track of kicks and you would like to use its system.

Listeners:
* TekkitRestrict now does smart checks and only enables listeners for things that are enabled.

Threads:
* Alot of preformance increase in all threads.
* Threads now get stopped properly.
* SaveThread will trigger one more time on shutdown.

EMC:
* Changed the way EMC is set.
	** An EMC value of 0 now actually removes the EMC value of that item.
	** Improvement to the way the strings in the config are handled.
	** "<id>[:<data>] <EMC>" where data can consist of multiple values separated by comma's. Ranges can be specified with <begin>-<end>
	** "10:2-4,12 100" Will set the EMC values of 10:2, 10:3, 10:4 and 10:12 to 100.
* Improved EMC tempset and lookup.

SafeZones:
* Improved GriefPrevention support.
	** Added option to only allow adminclaims to be safezones
	** You can now set if a claim should be a safezone by using /tr admin safezone addgp <name> while you are standing in it.
	** Bug: if you remove the claim, tekkitrestrict still thinks there is a safezone

Commands:
* Added alot of permissions for all the sections of the /tr command.
* OpenAlc now uses a more improved system.
* TPIC now supports a thorough setting (checks for all entities, not
only items)
* TPIC now searches more efficient
* All commands have better error messages and more help.
* Moved /tr admin emc to /tr emc

Logging:
* Completely changed the way things are logged. It should be more efficient now.
* The logfilter now does better checks.
* Include Essentials nickname checking.
* Added an option in the config to completely disable or enable the LogFilter. (Splitter and Replacer)


Permissions:
* Fixed groupmanager errors.
* Added more permissions for bypasses.
	** tekkitrestrict.bypass.hack.fly, tekkitrestrict.bypass.dupe.rmfurnace, etc.
* Changed all tekkitrestrict.*.bypass permissions to tekkitrestrict.bypass.*

Developers:
* Started work on an API.
* API currently has 2 classes, EMC and SafeZones.