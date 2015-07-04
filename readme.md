#SimpleInventoryBackup
###Inventory backups made easy!
SimpleInventoryBackup allows server administrators to backup specific or all players inventories and restore them as they wish. The source can be seen at the [Github page.](https://github.com/Cammy-the-block/SimpleInventoryBackup) Feel free to download the plugin at the [Bukkit page](http://dev.bukkit.org/bukkit-plugins/simpleinventorybackup/).

##Features
* Server Admins can backup all players' inventories or a chosen player's inventory in one command
* Server Admins can also restore all all players' inventories or a chosen player's inventory in one command.
* Players can request to have their inventory restored, which sends a message to admin allowing them to choose the proper choice of action.
* Allows Admins to backup the inventories of players that are offline, using the same command.
* Multiworld support allowing for configuration of which worlds inventory backups and restores occur.
* Ender chests can be backed up along with inventory backups.

##Commands
**/backupinventory <player>** - Backs up a given player's inventory and ender chest if enabled. To back up all players' inventories use all in place of a player name.

**/restoreInventory <player>** - Restores a given player's inventory to the backup. It will also restore the player's ender chest if enabled in the config. To restore ever players' inventories use all in place of a player name.

**/requestinventoryrestore** - Request that an admin restore their inventory.

##Permissions
**simpleinventorybackup.backup** - Allows a player to backup inventories.

**simpleinventorybackup.restore** - Allows a player to restore inventories.

**simpleinventorybackup.request** - Allows a player to request to have their inventories

**simpleinventorybackup.seerequest** - If this permission is granted, the requests of players to have their inventories backed up are shown to the player with this permission. 

##Configuration
**individualplayeroverride** - Boolean value. True causes an inventory backup or restore of a specific player to occur regardless of the excluded and included worlds. False causes inventory backups and restores to occur as dictated by the config.

**backupenderchests** - Boolean value. True causes ender chests to be restore and backed up with inventories. False prevents ender chests from being restored and backed up. Ender chest's are backed up according to the world settings for inventory backups.

**includedworlds** - Comma seperated worlds or all. Backups and restores will only occur on included worlds. Obviously all includes all worlds :P If a world is excluded that shall override including all.

**excludedworlds** - Comma seperated worlds. Worlds listed here will not have inventory backups or restores occur on them.

##Changelog
###V1.3
* Ender chests can now be backed up. Enabled by default.
* Fixed bug that prevented armor from being properly restored.

###V1.2
* Added support for backing up offline players.

###V1.1
* Multiworld support added.
