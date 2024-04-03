package hydraheadhunter.cmdstats.command.argument;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ItemNoStackStringReader {
    private static final SimpleCommandExceptionType TAG_DISALLOWED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.item.tag.disallowed"));
    private static final DynamicCommandExceptionType ID_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.stringifiedTranslatable("argument.item.id.invalid", id));
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(tag -> Text.stringifiedTranslatable("arguments.item.tag.unknown", tag));
    private static final char HASH_SIGN = '#';
    private final RegistryWrapper<Item> registryWrapper;
    private final StringReader reader;
    private final boolean allowTag;
    private Either<RegistryEntry<Item>, RegistryEntryList<Item>> result;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SuggestionsBuilder::buildFuture;

    private ItemNoStackStringReader(RegistryWrapper<Item> registryWrapper, StringReader reader, boolean allowTag) {
        this.registryWrapper = registryWrapper;
        this.reader = reader;
        this.allowTag = allowTag;
    }

    public static ItemResult item(RegistryWrapper<Item> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            ItemNoStackStringReader itemStringReader = new ItemNoStackStringReader(registryWrapper, reader, false);
            itemStringReader.consume();
            RegistryEntry<Item> registryEntry = itemStringReader.result.left().orElseThrow(() -> new IllegalStateException("Parser returned unexpected tag name"));
            return new ItemResult(registryEntry);
        } catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw commandSyntaxException;
        }
    }

    public static Either<ItemResult, TagResult> itemOrTag(RegistryWrapper<Item> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            ItemNoStackStringReader itemStringReader = new ItemNoStackStringReader(registryWrapper, reader, true);
            itemStringReader.consume();
            return itemStringReader.result.mapBoth(ItemResult::new, TagResult::new);
        } catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw commandSyntaxException;
        }
    }

    public static CompletableFuture<Suggestions> getSuggestions(RegistryWrapper<Item> registryWrapper, SuggestionsBuilder builder, boolean allowTag) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        ItemNoStackStringReader itemStringReader = new ItemNoStackStringReader(registryWrapper, stringReader, allowTag);
        try {
            itemStringReader.consume();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return itemStringReader.suggestions.apply(builder.createOffset(stringReader.getCursor()));
    }

    private void readItem() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        Optional<RegistryEntry.Reference<Item>> optional = this.registryWrapper.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier));
        this.result = Either.left(optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return ID_INVALID_EXCEPTION.createWithContext(this.reader, identifier);
        }));
    }

    private void readTag() throws CommandSyntaxException {
        if (!this.allowTag) {
            throw TAG_DISALLOWED_EXCEPTION.createWithContext(this.reader);
        }
        int i = this.reader.getCursor();
        this.reader.expect(HASH_SIGN);
        this.suggestions = this::suggestTag;
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        Optional<RegistryEntryList.Named<Item>> optional = this.registryWrapper.getOptional(TagKey.of(RegistryKeys.ITEM, identifier));
        this.result = Either.right(optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return UNKNOWN_TAG_EXCEPTION.createWithContext(this.reader, identifier);
        }));
    }

    private void consume() throws CommandSyntaxException {
        this.suggestions = this.allowTag ? this::suggestItemOrTagId : this::suggestItemId;
        if (this.reader.canRead() && this.reader.peek() == HASH_SIGN) {
            this.readTag();
        } else {
            this.readItem();
        }
        this.suggestions = this::suggestItem;
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder builder) {
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamTagKeys().map(TagKey::id), builder, String.valueOf('#'));
    }

    private CompletableFuture<Suggestions> suggestItemId(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
    }

    private CompletableFuture<Suggestions> suggestItemOrTagId(SuggestionsBuilder builder) {
        this.suggestTag(builder);
        return this.suggestItemId(builder);
    }

    public record ItemResult(RegistryEntry<Item> item) {
    }

    public record TagResult(RegistryEntryList<Item> tag) {
    }
}
