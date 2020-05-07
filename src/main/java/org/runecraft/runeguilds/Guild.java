package org.runecraft.runeguilds;

import org.runecraft.runecore.User;
import org.runecraft.runeguilds.enums.Office;
import org.spongepowered.api.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Guild {

    private GuildMember owner;
    private Set<GuildMember> members = new HashSet<>();
    private int points;
    private int power;
    private Text name;
    private Text tag;

    public Guild(User owner, int power, int points, Text name, Text tag) {
        this.owner = new GuildMember(owner, this, Office.OWNER);
        this.name = name;
        this.tag = tag;
        this.points = points;
        this.power = power;
    }

    public int getPoints() {
        return points;
    }

    public int getPower(){
        return power;
    }

    public void addMember(GuildMember member){
        members.add(member);
    }

    public GuildMember getOwner() {
        return owner;
    }

    public Set<GuildMember> getMembers() {
        return members;
    }

    public Text getName() {
        return name;
    }

    public Text getTag() {
        return tag;
    }
}
