package hydraheadhunter.cmdstats.command.argument.entity_type;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public class EntityTypeArgument implements ArgumentType<EntityType<?>> {
    private final EntityType<?> entityType;
    
    public <T extends Entity> EntityTypeArgument(EntityType<T> entityType) {
        this.entityType = entityType;
    }
    
    public EntityType<?> getEntityType() {
        return this.entityType;
    }
    
    @Override
    public EntityType<?> parse( StringReader reader ) throws CommandSyntaxException {
        return null;
    }
}
