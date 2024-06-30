package hydraheadhunter.cmdstats.command.argument.block;
//Code in this file thanks to 7410.

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;


public class BlockArgument implements ArgumentType<Block> {
    private final Block block;
    
    public BlockArgument(Block block) {
        this.block = block;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    @Override
    public Block parse( StringReader reader ) throws CommandSyntaxException {
        return null;
    }
}

