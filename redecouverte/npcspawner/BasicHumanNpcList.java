package redecouverte.npcspawner;

import java.util.HashMap;
import org.bukkit.entity.Entity;


public class BasicHumanNpcList extends HashMap<String, BasicHumanNpc> {

	private static final long serialVersionUID = 4325065770267149604L;

	public boolean containsBukkitEntity(Entity entity)
    {
        for(BasicHumanNpc bnpc : this.values())
        {
            if(bnpc.getBukkitEntity().getEntityId() == entity.getEntityId())
                return true;
        }

        return false;
    }

    public BasicHumanNpc getBasicHumanNpc(Entity entity)
    {
        for(BasicHumanNpc bnpc : this.values())
        {
            if(bnpc.getBukkitEntity().getEntityId() == entity.getEntityId())
                return bnpc;
        }

        return null;
    }

}
