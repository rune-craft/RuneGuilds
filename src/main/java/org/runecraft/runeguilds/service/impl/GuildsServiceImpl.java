package org.runecraft.runeguilds.service.impl;

import org.runecraft.runecore.User;
import org.runecraft.runecore.db.Atribute;
import org.runecraft.runecore.db.DataBase;
import org.runecraft.runecore.db.enums.Table;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.enums.Office;
import org.runecraft.runeguilds.service.GuildsService;
import org.spongepowered.api.text.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class GuildsServiceImpl implements GuildsService {
    private final Set<Guild> guilds = new HashSet<>();

    public void downloadGuilds(){
        try{
            Statement st = DataBase.getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM" + Table.GUILDS);

            guilds.clear();

            while(rs.next()){
                Text tag = Text.of(rs.getString(Atribute.GuildsAtributes.TAG.getName()));
                Text name = Text.of(rs.getString(Atribute.GuildsAtributes.NAME.getName()));
                int points = rs.getInt(Atribute.GuildsAtributes.POINTS.getName());
                double power = rs.getInt(Atribute.GuildsAtributes.POWER.getName());
                double finalPower = 2.5;
                if(power <= 0) finalPower = 0;
                if(power >= 5) finalPower = 0;
                if(power >= 0 || power <= 5) finalPower = power;
                User owner = User.by(UUID.fromString(rs.getString(Atribute.GuildsAtributes.OWNER.getName()))).get();

                guilds.add(new Guild(owner,finalPower,points,name,tag));
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

                guild.addMember(user, office);

            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public Optional<Guild> getGuild(String tag){
        return guilds.stream().filter(g -> g.getTag().toString().equalsIgnoreCase(tag)).findFirst();
    }

    public Optional<Guild> getPlayerGuild(UUID uid){
        for(Guild g : guilds){
            if(g.getMembers().entrySet().stream().anyMatch(e -> e.getKey().getUUID().equals(uid))){
                return Optional.ofNullable(g);
            }
        }
        return Optional.empty();
    }

    public Optional<Guild> getPlayerGuild(User user){
        return getPlayerGuild(user.getUUID());
    }

    public void createGuild(User owner, Text tag, Text name){
        guilds.add(new Guild(owner,5D,0,name, tag));

        /*WAITING FOR SPONGE CONNECTOR UPDATE
        Task.Builder task = Task.builder().async().execute(() -> {
            try{
                PreparedStatement st = DataBase.getConnection().prepareStatement(Table.GUILDS.getBuildString(DatabaseOperation.INSERT));
                st.setString(1, tag.toPlain());
                st.setString(2, name.toPlain());
                st.setString(3, owner.getUUID().toString());
                st.setInt(4, 0);
                st.setInt(5, 0);

                st.executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        });*/
    }
}
