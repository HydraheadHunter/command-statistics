package hydraheadhunter.cmdstats.command.argument.block;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;


import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlockArgumentType implements ArgumentType<BlockArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone");
    private final RegistryWrapper<Block> registryWrapper;

    public BlockArgumentType(CommandRegistryAccess commandRegistryAccess) {
        this.registryWrapper = commandRegistryAccess.getWrapperOrThrow(RegistryKeys.BLOCK);
    }

    public static BlockArgumentType block(CommandRegistryAccess commandRegistryAccess) {
        return new BlockArgumentType(commandRegistryAccess);
    }

    @Override
    public BlockArgument parse(StringReader stringReader) throws CommandSyntaxException {
        BlockNoStateArgumentParser.BlockResult blockResult = BlockNoStateArgumentParser.block(this.registryWrapper, stringReader);
        return new BlockArgument(blockResult.block() );
    }

    public static BlockArgument getBlock(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, BlockArgument.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return BlockNoStateArgumentParser.getSuggestions(this.registryWrapper, builder, false);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}


