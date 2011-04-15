package redecouverte.npcspawner;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;


public class NpcEntityTargetEvent extends EntityTargetEvent {

	private static final long serialVersionUID = 1802343027839971850L;

	public static enum NpcTargetReason {
        CLOSEST_PLAYER, NPC_RIGHTCLICKED, NPC_BOUNCED
    }
    private NpcTargetReason reason;

    public NpcEntityTargetEvent(Entity entity, Entity target, NpcTargetReason reason) {
        super(entity, target, TargetReason.CUSTOM);
        this.reason = reason;
    }


    public NpcTargetReason getNpcReason()
    {
        return this.reason;
    }

}
