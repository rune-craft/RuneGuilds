package org.runecraft.runeguilds;

import org.runecraft.runecore.User;
import org.runecraft.runecore.db.DataBase;
import org.runecraft.runecore.db.enums.DatabaseOperation;
import org.runecraft.runecore.db.enums.Table;
import org.runecraft.runeguilds.enums.Office;
import org.runecraft.runeguilds.service.GuildsService;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Guild {

    private final Map<User, Office> members = new HashMap<>();
    private final Set<Guild> enemyGuilds = new HashSet<>();
    private final Set<Guild> allyGuilds = new HashSet<>();
    private final int points;
    private double power;
    private final Text name;
    private final Text tag;

    public Guild(User owner, double power, int points, Text name, Text tag) {
        members.put(owner, Office.OWNER);
        this.name = name;
        this.tag = tag;
        this.points = points;
        this.power = power;
    }

    public static Optional<Guild> by(String tag){
        return RuneGuilds.provide(GuildsService.class).getGuild(tag);
    }

    public int getPoints() {
        return points;
    }

    public double getPower(){
        return power;
    }

    public void addMember(User member, Office office){
        members.put(member, office);
    }

    public Set<Guild> getEnemys(){
        return enemyGuilds;
    }

    public Set<Guild> getAllies() {
        return allyGuilds;
    }

    public void addEnemy(Guild guild, boolean db){
        enemyGuilds.add(guild);
        if(db){
            /*
            Task.Builder task = Task.builder().async().execute(() ->{
                try{
                    PreparedStatement st = DataBase.getConnection().prepareStatement(Table.GUILD_ENEMY.getBuildString(DatabaseOperation.INSERT));
                    st.setString(1, getTag().toPlain());
                    st.setString(2, guild.getTag().toPlain());

                    st.executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            */
        }
    }

    public void removeEnemy(Guild enemy, boolean db){
        enemyGuilds.remove(enemy);
    }

    public void addAlly(Guild guild, boolean db){
        allyGuilds.add(guild);
        if(db){
            /*
            Task.Builder task = Task.builder().async().execute(() ->{
                try{
                    PreparedStatement st = DataBase.getConnection().prepareStatement(Table.GUILD_ALLY.getBuildString(DatabaseOperation.INSERT));
                    st.setString(1, getTag().toPlain());
                    st.setString(2, guild.getTag().toPlain());

                    st.executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            */
        }
    }

    public void removeAlly(Guild ally, boolean db){
        allyGuilds.remove(ally);
        
    }

    public void registerMember(User user, Office office){
        addMember(user,office);
        /*
        Task.Builder task = Task.builder().async().execute(() ->{
           try{
               PreparedStatement st = DataBase.getConnection().prepareStatement(Table.GUILD_MEMBERS.getBuildString(DatabaseOperation.INSERT));
               st.setString(1, user.getUUID().toString());
               st.setString(2, tag.toPlain());
               st.setInt(3, office.getId());

               st.executeUpdate();
           } catch (SQLException throwables) {
               throwables.printStackTrace();
           }
        });
        */
    }

    public void removeMember(User user){
        members.remove(user);

        /*
        Task.Builder task = Task.builder().async().execute(() -> {
           try{
               PreparedStatement st = DataBase.getConnection().prepareStatement(
                       "DELETE FROM " + Table.GUILD_MEMBERS.getName() + " WHERE user=?");
               st.setString(1, user.getUUID().toString());
               st.executeUpdate();
           } catch (SQLException throwables) {
               throwables.printStackTrace();
           }
        });*/
    }

    public void increasePower(double quantity){
        if(power + quantity >= 5){
            this.power = 5;
        }else{
            power+=quantity;
        }
        /*
        Task task = Task.builder().async().execute(() ->{
           try{
               PreparedStatement st = DataBase.getConnection().prepareStatement(
                       "UPDATE " + Table.GUILD_CHUNKS.getName() + " SET power=power+? WHERE "
                       + Atribute.GuildsAtributes.TAG.getName() + "=?"
               );

               st.setDouble(1, quantity);
               st.setString(2, tag.toPlain());
               st.executeUpdate();
           } catch (SQLException throwables) {
               throwables.printStackTrace();
           }
        }).submit(RuneGuilds.get().getPlugin());*/
    }

    public void decreasePower(double quantity) {
        if(power - quantity <= 0){
            this.power = 0;
        }else{
            power-=quantity;
        }
        /*
        Task task = Task.builder().async().execute(() ->{
           try{
               PreparedStatement st = DataBase.getConnection().prepareStatement(
                       "UPDATE " + Table.GUILD_CHUNKS.getName() + " SET power=power-? WHERE "
                       + Atribute.GuildsAtributes.TAG.getName() + "=?"
               );

               st.setDouble(1, quantity);
               st.setString(2, tag.toPlain());
               st.executeUpdate();
           } catch (SQLException throwables) {
               throwables.printStackTrace();
           }
        }).submit(RuneGuilds.get().getPlugin());*/
    }

    public User getOwner() {
        for(Map.Entry<User,Office> entry : members.entrySet()){
            if(entry.getValue() == Office.OWNER){
                return entry.getKey();
            }
        }
        return null;
    }

    public Office getMemberOffice(User member){
        return members.get(member);
    }

    public List<Map.Entry<User, Office>> getOnlineMembers() {
        return members.entrySet().stream().filter(e -> e.getKey().isOnline()).collect(Collectors.toList());
    }

    public Map<User, Office> getMembers() {
        return members;
    }

    public Text getName() {
        return name;
    }

    public Text getTag() {
        return tag;
    }
}
