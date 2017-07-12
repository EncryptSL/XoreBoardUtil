package eu.haelexuis.utils.xoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static eu.haelexuis.utils.xoreboard.XoreBoardPackets.getDisplayNamePacket;
import static eu.haelexuis.utils.xoreboard.XoreBoardPackets.getLinePacket;
import static eu.haelexuis.utils.xoreboard.XoreBoardPackets.getSidebarPacket;

public class XoreBoardGlobalSidebar {
    private HashMap<String, Integer> lines = new HashMap<>();
    private XoreBoard xoreBoard;
    private String displayName;

    XoreBoardGlobalSidebar(XoreBoard xoreBoard) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', xoreBoard.getName());
        this.xoreBoard = xoreBoard;
        this.xoreBoard.getPlayers().forEach(this::showSidebar);
    }

    public void setDisplayName(String displayName) {
        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        if(displayName.length() > 32) {
            System.out.println("[XoreBoardUtil] Error! DisplayName is longer than 32 characters (" + displayName + ")");
            return;
        }
        if(!this.displayName.equals(displayName)) {
            for(Player p : xoreBoard.getPlayers())
                XoreBoardPackets.sendPacket(p, getSidebarPacket(xoreBoard.getId() + "." + p.getEntityId(), displayName, 2));
            this.displayName = displayName;
        }
    }

    public void putLine(String key, int value) {
        for(Player p : xoreBoard.getPlayers())
            XoreBoardPackets.sendPacket(p, getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), value, XoreBoardPackets.EnumScoreboardAction.CHANGE));
        lines.put(key, value);
    }

    public void putLines(HashMap<String, Integer> lines) {
        this.lines.forEach((key, value) -> {
            if(lines.get(key) != null && lines.get(key).equals(value))
                lines.remove(key);
        });
        for(Player p : xoreBoard.getPlayers())
            lines.forEach((key, value) -> XoreBoardPackets.sendPacket(p, getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), value, XoreBoardPackets.EnumScoreboardAction.CHANGE)));
        this.lines.putAll(lines);
    }

    public void rewriteLines(HashMap<String, Integer> lines) {
        HashMap<String, Integer> tmpLines = new HashMap<>(this.lines);
        tmpLines.forEach((key, value) -> {
            if(lines.get(key) == null)
                clearLine(key);
        });
        lines.forEach((key, value) -> {
            if(this.lines.get(key) == null || !this.lines.get(key).equals(value))
                putLine(key, value);
        });
    }

    public void clearLine(String key) {
        if(!this.lines.containsKey(key))
            return;
        for(Player p : xoreBoard.getPlayers())
            XoreBoardPackets.sendPacket(p, getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), 0, XoreBoardPackets.EnumScoreboardAction.REMOVE));
        this.lines.remove(key);
    }

    public void clearLines() {
        for(Player p : xoreBoard.getPlayers()) {
            this.lines.forEach((key, value) -> {
                XoreBoardPackets.sendPacket(p, getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), 0, XoreBoardPackets.EnumScoreboardAction.REMOVE));
            });
        }
        this.lines.clear();
    }

    public void hideSidebar() {
        for(Player p : xoreBoard.getPlayers()) {
            XoreBoardPackets.sendPacket(p, getSidebarPacket(xoreBoard.getId() + "." + p.getEntityId(), displayName, 1));
            xoreBoard.getSidebar(p).setShowedGlobalSidebar(false);
        }
    }

    public void hideForPlayer(Player p) {
        XoreBoardPackets.sendPacket(p, getSidebarPacket(xoreBoard.getId() + "." + p.getEntityId(), displayName, 1));
        xoreBoard.getSidebar(p).setShowedGlobalSidebar(false);
    }

    public void showSidebar(Player p) {
        xoreBoard.getEntries().get(p).setShowedGlobalSidebar(true);
        XoreBoardPackets.sendPacket(p, getSidebarPacket(xoreBoard.getId() + "." + p.getEntityId(), displayName, 0));
        XoreBoardPackets.sendPacket(p, getDisplayNamePacket(xoreBoard.getId() + "." + p.getEntityId()));
        lines.forEach((key, value) -> {
            XoreBoardPackets.sendPacket(p, getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), value, XoreBoardPackets.EnumScoreboardAction.CHANGE));
        });
    }
}
