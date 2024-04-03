package hydraheadhunter.cmdstats.command.argument;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BlockNoStateArgumentParser {
    public static final SimpleCommandExceptionType DISALLOWED_TAG_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.block.tag.disallowed"));
    public static final DynamicCommandExceptionType INVALID_BLOCK_ID_EXCEPTION = new DynamicCommandExceptionType(block -> Text.stringifiedTranslatable("argument.block.id.invalid", block));
    public static final DynamicCommandExceptionType UNKNOWN_BLOCK_TAG_EXCEPTION = new DynamicCommandExceptionType(tag -> Text.stringifiedTranslatable("arguments.block.tag.unknown", tag));
    private static final char TAG_PREFIX = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_DEFAULT = SuggestionsBuilder::buildFuture;
    private final RegistryWrapper<Block> registryWrapper;
    private final StringReader reader;
    private final boolean allowTag;
    private Identifier blockId = new Identifier("");
    @Nullable private Block block;
    @Nullable private RegistryEntryList<Block> tagId;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_DEFAULT;

    private BlockNoStateArgumentParser(RegistryWrapper<Block> registryWrapper, StringReader reader, boolean allowTag) {
        this.registryWrapper = registryWrapper;
        this.reader = reader;
        this.allowTag = allowTag;
    }

    public static BlockResult block(RegistryWrapper<Block> registryWrapper, String string) throws CommandSyntaxException {
        return BlockNoStateArgumentParser.block(registryWrapper, new StringReader(string));
    }

    public static BlockResult block(RegistryWrapper<Block> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            BlockNoStateArgumentParser blockArgumentParser = new BlockNoStateArgumentParser(registryWrapper, reader, false);
            blockArgumentParser.parse();
            return new BlockResult(blockArgumentParser.block);
        } catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw commandSyntaxException;
        }
    }

    public static Either<BlockResult, TagResult> blockOrTag(RegistryWrapper<Block> registryWrapper, String string) throws CommandSyntaxException {
        return BlockNoStateArgumentParser.blockOrTag(registryWrapper, new StringReader(string));
    }

    public static Either<BlockResult, TagResult> blockOrTag(RegistryWrapper<Block> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            BlockNoStateArgumentParser blockArgumentParser = new BlockNoStateArgumentParser(registryWrapper, reader, true);
            blockArgumentParser.parse();
            if (blockArgumentParser.tagId != null) {
                return Either.right(new TagResult(blockArgumentParser.tagId));
            }
            return Either.left(new BlockResult(blockArgumentParser.block));
        } catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw commandSyntaxException;
        }
    }

    public static CompletableFuture<Suggestions> getSuggestions(RegistryWrapper<Block> registryWrapper, SuggestionsBuilder builder, boolean allowTag) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        BlockNoStateArgumentParser blockArgumentParser = new BlockNoStateArgumentParser(registryWrapper, stringReader, allowTag);
        try {
            blockArgumentParser.parse();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return blockArgumentParser.suggestions.apply(builder.createOffset(stringReader.getCursor()));
    }

    private void parse() throws CommandSyntaxException {
        this.suggestions = this.allowTag ? this::suggestBlockOrTagId : this::suggestBlockId;
        if (this.reader.canRead() && this.reader.peek() == TAG_PREFIX) {
            this.parseTagId();
        } else {
            this.parseBlockId();
        }
        this.suggestions = SuggestionsBuilder::buildFuture;
    }

    private CompletableFuture<Suggestions> suggestIdentifiers(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamTagKeys().map(TagKey::id), builder, String.valueOf(TAG_PREFIX));
    }

    private CompletableFuture<Suggestions> suggestBlockId(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
    }

    private CompletableFuture<Suggestions> suggestBlockOrTagId(SuggestionsBuilder builder) {
        this.suggestIdentifiers(builder);
        this.suggestBlockId(builder);
        return builder.buildFuture();
    }

    private void parseBlockId() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        this.blockId = Identifier.fromCommandInput(this.reader);
        this.block = this.registryWrapper.getOptional(RegistryKey.of(RegistryKeys.BLOCK, this.blockId)).orElseThrow(() -> {
            this.reader.setCursor(i);
            return INVALID_BLOCK_ID_EXCEPTION.createWithContext(this.reader, this.blockId.toString());
        }).value();
    }

    private void parseTagId() throws CommandSyntaxException {
        if (!this.allowTag) {
            throw DISALLOWED_TAG_EXCEPTION.createWithContext(this.reader);
        }
        int i = this.reader.getCursor();
        this.reader.expect(TAG_PREFIX);
        this.suggestions = this::suggestIdentifiers;
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        this.tagId = this.registryWrapper.getOptional(TagKey.of(RegistryKeys.BLOCK, identifier)).orElseThrow(() -> {
            this.reader.setCursor(i);
            return UNKNOWN_BLOCK_TAG_EXCEPTION.createWithContext(this.reader, identifier.toString());
        });
    }

    public record BlockResult(Block block) {
    }

    public record TagResult(RegistryEntryList<Block> tag) {
    }
}
