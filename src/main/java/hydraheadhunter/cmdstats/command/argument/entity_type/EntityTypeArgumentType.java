package hydraheadhunter.cmdstats.command.argument.entity_type;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class EntityTypeArgumentType implements ArgumentType<EntityTypeArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
    private final RegistryWrapper<EntityType<?>> registryWrapper;
    
    public EntityTypeArgumentType(CommandRegistryAccess commandRegistryAccess) {
        this.registryWrapper = commandRegistryAccess.getOrThrow(RegistryKeys.ENTITY_TYPE);
    }
    
    public static EntityTypeArgumentType type(CommandRegistryAccess commandRegistryAccess) {
        return new EntityTypeArgumentType(commandRegistryAccess);
    }
    
    public EntityTypeArgument parse(StringReader stringReader) throws CommandSyntaxException {
        EntityTypeNoBrainStringReader.TypeResult typeResult = EntityTypeNoBrainStringReader.type(this.registryWrapper, stringReader);
        return new EntityTypeArgument( typeResult.entityType() );
    }
    
    public static <S> EntityTypeArgument getEntityType(CommandContext<S> context, String name) {
        return (EntityTypeArgument)context.getArgument(name, EntityTypeArgument.class);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return EntityTypeNoBrainStringReader.getSuggestions(this.registryWrapper, builder, false);
    }
    
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
