package hydraheadhunter.cmdstats.command.argument;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.Item;

public class ItemArgument implements ArgumentType<Item> {
    private final Item item;
    
    public ItemArgument(Item item) {
        this.item = item;
    }
    
    public Item getItem() {
        return this.item;
    }
    
    @Override
    public Item parse( StringReader reader ) throws CommandSyntaxException {
        return null;
    }
}
