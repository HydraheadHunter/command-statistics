package hydraheadhunter.cmdstats.command.argument;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

public class ItemArgumentType implements ArgumentType<ItemArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
    private final RegistryWrapper<Item> registryWrapper;
    
    public ItemArgumentType(CommandRegistryAccess commandRegistryAccess) {
        this.registryWrapper = commandRegistryAccess.createWrapper(RegistryKeys.ITEM);
    }
    
    public static ItemArgumentType item(CommandRegistryAccess commandRegistryAccess) {
        return new ItemArgumentType(commandRegistryAccess);
    }
    
    public ItemArgument parse(StringReader stringReader) throws CommandSyntaxException {
        ItemNoStackStringReader.ItemResult itemResult = ItemNoStackStringReader.item(this.registryWrapper, stringReader);
        return new ItemArgument( itemResult.item().value() );
    }
    
    public static <S> ItemArgument getItem(CommandContext<S> context, String name) {
        return (ItemArgument)context.getArgument(name, ItemArgument.class);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ItemNoStackStringReader.getSuggestions(this.registryWrapper, builder, false);
    }
    
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
