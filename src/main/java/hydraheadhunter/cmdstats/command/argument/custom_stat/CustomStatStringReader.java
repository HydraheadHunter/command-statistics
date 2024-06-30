package hydraheadhunter.cmdstats.command.argument.custom_stat;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
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

public class CustomStatStringReader {
    public static final SimpleCommandExceptionType DISALLOWED_TAG_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.block.tag.disallowed"));
    public static final DynamicCommandExceptionType INVALID_BLOCK_ID_EXCEPTION = new DynamicCommandExceptionType(block -> Text.stringifiedTranslatable("argument.block.id.invalid", block));
    public static final DynamicCommandExceptionType UNKNOWN_BLOCK_TAG_EXCEPTION = new DynamicCommandExceptionType(tag -> Text.stringifiedTranslatable("arguments.block.tag.unknown", tag));
    private static final char TAG_PREFIX = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_DEFAULT = SuggestionsBuilder::buildFuture;
    private final RegistryWrapper<Identifier> registryWrapper;
    private final StringReader reader;
    private final boolean allowTag;
    private Identifier identifierID = Identifier.of("");
    
    @Nullable private Identifier identifier;
    @Nullable private RegistryEntryList<Identifier> tagId;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_DEFAULT;
    
    
    private CustomStatStringReader(RegistryWrapper<Identifier> registryWrapper, StringReader reader, boolean allowTag) {
        this.registryWrapper = registryWrapper;
        this.reader = reader;
        this.allowTag = false;
    }
    
    public static StatResult stat(RegistryWrapper<Identifier> registryWrapper, String string) throws CommandSyntaxException {
        return CustomStatStringReader.stat(registryWrapper, new StringReader(string));
    }
    
    public static StatResult stat(RegistryWrapper<Identifier> registryWrapper, StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        try {
            CustomStatStringReader customStatStringReader = new CustomStatStringReader(registryWrapper, reader, false);
            customStatStringReader.parse();
            return new StatResult(customStatStringReader.identifier);
        } catch (CommandSyntaxException commandSyntaxException) {
            reader.setCursor(i);
            throw commandSyntaxException;
        }
    }
    
    public static CompletableFuture<Suggestions> getSuggestions(RegistryWrapper<Identifier> registryWrapper, SuggestionsBuilder builder, boolean allowTag) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        CustomStatStringReader customStatStringReader = new CustomStatStringReader(registryWrapper, stringReader, allowTag);
        try {
            customStatStringReader.parse();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return customStatStringReader.suggestions.apply(builder.createOffset(stringReader.getCursor()));
    }
    
    private void parse() throws CommandSyntaxException {
        this.suggestions = this.allowTag ? this::suggestBlockOrTagId : this::suggestBlockId;
        if (this.reader.canRead() && this.reader.peek() == TAG_PREFIX) {
            // this.parseTagId();
        } else {
            this.parseEntityTypeId();
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
    
    private void parseEntityTypeId() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        this.identifierID = Identifier.fromCommandInput(this.reader);
        this.identifier = this.registryWrapper.getOptional(RegistryKey.of(RegistryKeys.CUSTOM_STAT, this.identifierID)).orElseThrow(() -> {
            this.reader.setCursor(i);
            return INVALID_BLOCK_ID_EXCEPTION.createWithContext(this.reader, this.identifierID.toString());
        }).value();
    }
    /*
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
    */
    public record StatResult(Identifier stat) {}
    
}
