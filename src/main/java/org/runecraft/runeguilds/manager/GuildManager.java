package org.runecraft.runeguilds.manager;

import org.runecraft.runecore.User;
import org.runecraft.runecore.db.Atribute;
import org.runecraft.runecore.db.DataBase;
import org.runecraft.runecore.db.enums.Table;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.GuildMember;
import org.runecraft.runeguilds.enums.Office;
import org.spongepowered.api.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class GuildManager {
    private Set<Guild> guilds = new HashSet<>();

    public void downloadGuilds(){
        try{
            Statement st = DataBase.getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM" + Table.GUILDS);

            guilds.clear();

            while(rs.next()){
                Text tag = Text.of(rs.getString(Atribute.GuildsAtributes.TAG.getName()));
                Text name = Text.of(rs.getString(Atribute.GuildsAtributes.NAME.getName()));
                int points = rs.getInt(Atribute.GuildsAtributes.POINTS.getName());
                int power = rs.getInt(Atribute.GuildsAtributes.POWER.getName());
                User owner = User.by(UUID.fromString(rs.getString(Atribute.GuildsAtributes.OWNER.getName()))).get();

                guilds.add(new Guild(owner,power,points,name,tag));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void downloadGuildMembers(){
        try{
            Statement st = DataBase.getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM" + Table.GUILD_MEMBERS);

            for(Guild guild : guilds){
                guild.getMembers().clear();
            }

            while(rs.next()){
                Guild guild = getGuild(rs.getString(Atribute.GuildMembersAtributes.GUILD.getName())).get();
                Office office = Office.by(rs.getInt(Atribute.GuildMembersAtributes.OFFICE.getName())).get();
                User user = User.by(rs.getString(Atribute.GuildMembersAtributes.USER.getName())).get();

                guild.addMember(new GuildMember(user,guild,office));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public Optional<Guild> getGuild(String tag){
        return guilds.stream().filter(g -> g.getTag().toString().equalsIgnoreCase(tag)).findFirst();
    }
}
