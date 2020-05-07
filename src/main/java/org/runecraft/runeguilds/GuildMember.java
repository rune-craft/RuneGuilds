package org.runecraft.runeguilds;

import org.runecraft.runecore.User;
import org.runecraft.runeguilds.enums.Office;

import java.util.Optional;

public class GuildMember {
    private User owner;
    private Guild guild;
    private Office office;

    public GuildMember(User owner, Guild guild, Office office) {
        this.owner = owner;
        this.guild = guild;
        this.office = office;
    }

    public User getOwner() {
        return owner;
    }

    public Guild getGuild() {
        return guild;
    }

    public Office getOffice() {
        return office;
    }
}
