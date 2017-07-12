package eu.haelexuis.utils.xoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class XoreBoard {
    private HashMap<Player, XoreBoardPlayerSidebar> players = new HashMap<>();
    private XoreBoardGlobalSidebar globalSidebar;
    private Scoreboard bukkitScoreboard;
    private String name;
    private String id;

    XoreBoard(Scoreboard bukkitScoreboard, String id, String name) {
        this.bukkitScoreboard = bukkitScoreboard;
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setDefaultTitle(String name) {
        this.name = name;
    }

    public XoreBoardGlobalSidebar getSidebar() {
        if(globalSidebar == null)
            globalSidebar = new XoreBoardGlobalSidebar(this);
        players.forEach((player, sidebar) -> {
            if(!sidebar.isShowedGlobalSidebar())
                globalSidebar.showSidebar(player);
        });
        return globalSidebar;
    }

    public XoreBoardPlayerSidebar getSidebar(Player p) {
        if(!players.containsKey(p))
            addPlayer(p);
        return players.get(p);
    }

    public Scoreboard getBukkitScoreboard() {
        return bukkitScoreboard;
    }

    public void addPlayer(Player p) {
        p.setScoreboard(bukkitScoreboard);
        if(!players.containsKey(p))
            players.put(p, new XoreBoardPlayerSidebar(this, p));
    }

    public void removePlayer(Player p) {
        getSidebar(p).clearLines();
        getSidebar(p).hideSidebar();
        if(players.containsKey(p))
            players.remove(p);
    }

    public Collection<Player> getPlayers() {
        return players.keySet();
    }

    public HashMap<Player, XoreBoardPlayerSidebar> getEntries() {
        return players;
    }

    public void destroy() {
        getSidebar().clearLines();
        getSidebar().hideSidebar();
        List<Player> tmp = new ArrayList<>(getPlayers());
        tmp.forEach(this::removePlayer);
    }
}
