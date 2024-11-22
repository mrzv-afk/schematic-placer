package com.example.schematicplacer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.io.File;

public class LoadSchematicCommand implements CommandExecutor {
    private final SchematicPlacer plugin;

    public LoadSchematicCommand(SchematicPlacer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда может быть использована только игроком!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("schematicplacer.load")) {
            player.sendMessage(ChatColor.RED + "У вас нет прав для использования этой команды!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Использование: /loadschematic <имя_файла> [поворот]");
            return true;
        }

        String fileName = args[0];
        if (!fileName.endsWith(".schematic")) {
            fileName += ".schematic";
        }

        File schematicFile = new File(plugin.getDataFolder() + "/schematics", fileName);
        if (!schematicFile.exists()) {
            player.sendMessage(ChatColor.RED + "Схематика '" + fileName + "' не найдена!");
            return true;
        }

        float yaw = 0f;
        if (args.length > 1) {
            try {
                yaw = Float.parseFloat(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Неверный формат угла поворота! Используйте число.");
                return true;
            }
        }

        player.sendMessage(ChatColor.GREEN + "Размещаем схематику '" + fileName + "'...");
        plugin.placeSchematic(player.getLocation(), schematicFile, yaw);
        return true;
    }
}
