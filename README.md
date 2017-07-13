### XoreBoardUtil is better Scoreboard API for Spigot/Craftbukkit platform.

Supported minecraft versions: 1.8, 1.9, 1.10, 1.11, 1.12 (and maybe below, because I used reflection)

[Example usage](https://github.com/haelexuis/XoreBoardUtil/wiki/Examples)

**Features:**
* Easy sidebar manipulation.
* Ability to use 1 scoreboard and set different sidebars to each player, or shared (global) sidebar.
* Player collisions disabled by default.
* Always visible team prefixes and below names for everyone, no matter in which Scoreboard (XoreBoard) player is.
* You can set Below Names, such as "&c❤", and set health value to specific player.
* All server players are in 1 real Bukkit Scoreboard, where you can create a teams (ranks, etc.).
* Ability to hide/show sidebar for each player in XoreBoard.
* Player can be in multiple Scoreboards (XoreBoards) at the same time, depends which one are you updating.
* Whole code can be used in async threads.
* Color codes, such as &b, supported by default.
* Based on packets, and util can be used by multiple plugins at the same time.
* Prevent client kicking/crashing (if you set some text really long)

**Known bugs:**
* When you disable global collisions, mob collisions will be disabled as well, so if you have Survival/SkyBlock server, it can cause problem with mob pushing, so I recommend use XoreBoardUtil.setGlobalCollisions(Team.OptionStatus.FOR_OWN_TEAM) instead of XoreBoardUtil.setGlobalCollisions(Team.OptionStatus.NEVER) after server start, if you need mob pushing. Then will be collisions only between Teams, such as ADMIN -> VIP.. of course, if you don't have all players in 1 team.
