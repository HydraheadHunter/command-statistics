package hydraheadhunter.cmdstats.command.argument.custom_stat;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CustomStatArgumentType implements ArgumentType<CustomStatArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("animals_bred", "minecraft:clean_armor");
    private final RegistryWrapper<Identifier> registryWrapper;
    
    public CustomStatArgumentType(CommandRegistryAccess commandRegistryAccess) {
        this.registryWrapper = commandRegistryAccess.getWrapperOrThrow(RegistryKeys.CUSTOM_STAT);
    }
    
    public static CustomStatArgumentType stat(CommandRegistryAccess commandRegistryAccess) {
        return new CustomStatArgumentType(commandRegistryAccess);
    }
    
    public CustomStatArgument parse(StringReader stringReader) throws CommandSyntaxException {
        CustomStatStringReader.StatResult statResult = CustomStatStringReader.stat(this.registryWrapper, stringReader);
        return new CustomStatArgument( statResult.stat() );
    }
    
    public static <S> CustomStatArgument getStat(CommandContext<S> context, String name) {
        return (CustomStatArgument)context.getArgument(name, CustomStatArgument.class);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CustomStatStringReader.getSuggestions(this.registryWrapper, builder, false);
    }
    
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
