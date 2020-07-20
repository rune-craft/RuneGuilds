package org.runecraft.runeguilds.service.impl;

import com.flowpowered.math.vector.Vector2i;
import org.runecraft.runecore.db.Atribute;
import org.runecraft.runecore.db.DataBase;
import org.runecraft.runecore.db.enums.Table;
import org.runecraft.runeguilds.Guild;
import org.runecraft.runeguilds.RuneGuilds;
import org.runecraft.runeguilds.service.ChunksService;
import org.spongepowered.api.scheduler.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChunksServiceImpl implements ChunksService {

    private final Map<Vector2i, Guild> chunksMap = new HashMap<>();
    private final Set<Guild> cooldownSet = new HashSet<>();

    public Map<Vector2i, Guild> getChunksMap() { return chunksMap; }

    public void downloadChunks(){
        try{
            Statement st = DataBase.getConnection().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + Table.GUILD_CHUNKS.getName());
            chunksMap.clear();
            while(rs.next()){
                Guild owner = Guild.by(rs.getString(Atribute.GuildChunksAtributes.GUILD.getName())).get();
                int x = rs.getInt(Atribute.GuildChunksAtributes.X.getName());
                int y = rs.getInt(Atribute.GuildChunksAtributes.X.getName());
                chunksMap.put(new Vector2i(x,y), owner);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addClaimCooldown(Guild claimer){
        cooldownSet.add(claimer);
        Task.builder().async().delay(10, TimeUnit.SECONDS).execute(() -> {
            cooldownSet.remove(claimer);
        }).submit(RuneGuilds.get().getPlugin());
    }

    public boolean isInCooldown(Guild guild){
        return cooldownSet.contains(guild);
    }

    public boolean isClaimed(Vector2i coords){
        return getOwner(coords).isPresent();
    }

    public Optional<Guild> getOwner(Vector2i coords){
        for (Map.Entry<Vector2i, Guild> entry : chunksMap.entrySet()) {
            if(entry.getKey().compareTo(coords) == 0){
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public void removeChunk(Vector2i coords, boolean fromDb) {
        for (Map.Entry<Vector2i, Guild> entry : chunksMap.entrySet()) {
            if (entry.getKey().compareTo(coords) == 0) {
                chunksMap.remove(entry.getKey());
            }
        }
        if(fromDb){
            /*
            Task task = Task.builder().async().execute(() -> {
                try{
                    PreparedStatement st = DataBase.getConnection().prepareStatement(
                            "DELETE FROM " + Table.GUILD_CHUNKS.getName() + " WHERE x=? AND z=?");
                    st.setInt(1, coords.getX());
                    st.setInt(2, coords.getY());

                    st.executeUpdate();
                }catch (SQLException ex){
                    ex.printStackTrace();
                }
            }).submit(RuneGuilds.get().getPlugin());*/
        }
    }

    public void addChunk(Vector2i coords, Guild owner, boolean onDb){
        chunksMap.put(coords, owner);
        if(onDb){
            /*
            Task task = Task.builder().async().execute(() -> {
                try{
                    PreparedStatement st = DataBase.getConnection().prepareStatement(Table.GUILD_CHUNKS.getBuildString(DatabaseOperation.INSERT));
                    st.setString(1, owner.getTag().toPlain());
                    st.setInt(2, coords.getX());
                    st.setInt(3, coords.getY());

                    st.executeUpdate();
                }catch (SQLException ex){
                    ex.printStackTrace();
                }
            }).submit(RuneGuilds.get().getPlugin());*/
        }

    }

    public Optional<Guild>[][] getGuildsMap(Vector2i center){
        Optional[][] map = new Optional[15][15];
        for(int x = -7; x<7; x++){
            for(int y = -7; y<7; y++){
                map[x+7][y+7]=getOwner(center.add(x,y));
            }
        }
        return map;
    }
}
