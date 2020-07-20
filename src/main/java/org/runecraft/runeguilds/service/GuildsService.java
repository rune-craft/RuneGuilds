package org.runecraft.runeguilds.service;

import org.runecraft.runecore.User;
import org.runecraft.runecore.db.Atribute;
import org.runecraft.runecore.db.DataBase;
import org.runecraft.runecore.db.enums.Table;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.enums.Office;
import org.spongepowered.api.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public interface GuildsService {
    void downloadGuilds();

    void downloadGuildMembers();

    Optional<Guild> getGuild(String tag);

    Optional<Guild> getPlayerGuild(UUID uid);

    Optional<Guild> getPlayerGuild(User user);

    void createGuild(User owner, Text tag, Text name);
}
