package eu.haelexuis.utils.xoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static eu.haelexuis.utils.xoreboard.XoreBoardPackets.getDisplayNamePacket;
import static eu.haelexuis.utils.xoreboard.XoreBoardPackets.getLinePacket;
import static eu.haelexuis.utils.xoreboard.XoreBoardPackets.getSidebarPacket;

public class XoreBoardPlayerSidebar {
    private final XoreBoard xoreBoard;
    private final Player player;
    private ConcurrentHashMap<String, Integer> lines = new ConcurrentHashMap<>();
    private String displayName;
    private boolean showedGlobalSidebar = false;
    private boolean showedPrivateSidebar = false;

    XoreBoardPlayerSidebar(XoreBoard xoreBoard, Player player) {
        this.xoreBoard = xoreBoard;
        this.player = player;
        this.displayName = ChatColor.translateAlternateColorCodes('&', xoreBoard.getName());
        showSidebar();
    }

    public void setDisplayName(String displayName) {
        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        if(displayName.length() > 32) {
            System.out.println("[XoreBoardUtil] Error! DisplayName is longer than 32 characters (" + displayName + ")");
            return;
        }
        if(!this.displayName.equals(displayName)) {
            XoreBoardPackets.sendPacket(player, getSidebarPacket(xoreBoard.getId() + ":" + player.getEntityId(), displayName, 2));
            this.displayName = displayName;
        }
    }

    public void putLine(String key, int value) {
        XoreBoardPackets.sendPacket(player, getLinePacket(xoreBoard.getId() + ":" + player.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), value, XoreBoardPackets.EnumScoreboardAction.CHANGE));
        this.lines.put(key, value);
    }

    public void putLines(HashMap<String, Integer> lines) {
        this.lines.forEach((key, value) -> {
            if(lines.containsKey(key) && lines.get(key).equals(value))
                lines.remove(key);
        });
        lines.forEach((key, value) -> XoreBoardPackets.sendPacket(player, getLinePacket(xoreBoard.getId() + ":" + player.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), value, XoreBoardPackets.EnumScoreboardAction.CHANGE)));
        this.lines.putAll(lines);
    }

    public void rewriteLines(HashMap<String, Integer> lines) {
        this.lines.forEach((key, value) -> {
            if(!lines.containsKey(key))
                clearLine(key);
        });
        lines.forEach((key, value) -> {
            if(!this.lines.containsKey(key) || !this.lines.get(key).equals(value))
                putLine(key, value);
        });
    }

    public void clearLine(String key) {
        XoreBoardPackets.sendPacket(player, getLinePacket(xoreBoard.getId() + ":" + player.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), 0, XoreBoardPackets.EnumScoreboardAction.REMOVE));
        this.lines.remove(key);

    }

    public void clearLines() {
        this.lines.forEach((key, value) -> XoreBoardPackets.sendPacket(player, getLinePacket(xoreBoard.getId() + ":" + player.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), 0, XoreBoardPackets.EnumScoreboardAction.REMOVE)));
        this.lines.clear();
    }

    public void hideSidebar() {
        if(showedPrivateSidebar) {
            showedPrivateSidebar = false;
            XoreBoardPackets.sendPacket(player, getSidebarPacket(xoreBoard.getId() + ":" + player.getEntityId(), displayName, 1));
            return;
        }
        showSidebar();
        hideSidebar();
    }

    public void showSidebar() {
        if(!showedPrivateSidebar) {
            showedPrivateSidebar = true;
            XoreBoardPackets.sendPacket(player, getSidebarPacket(xoreBoard.getId() + ":" + player.getEntityId(), displayName, 0));
            XoreBoardPackets.sendPacket(player, getDisplayNamePacket(xoreBoard.getId() + ":" + player.getEntityId()));
            this.lines.forEach((key, value) -> XoreBoardPackets.sendPacket(player, getLinePacket(xoreBoard.getId() + ":" + player.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), value, XoreBoardPackets.EnumScoreboardAction.CHANGE)));
            return;
        }
        hideSidebar();
        showSidebar();
    }

    public HashMap<String, Integer> getLines() {
        return new HashMap<>(lines);
    }

    public boolean isShowedGlobalSidebar() {
        return showedGlobalSidebar;
    }

    public void setShowedGlobalSidebar(boolean bool) {
        this.showedGlobalSidebar = bool;
    }
}
