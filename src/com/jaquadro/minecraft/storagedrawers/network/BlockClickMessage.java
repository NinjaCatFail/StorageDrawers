package com.jaquadro.minecraft.storagedrawers.network;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.IExtendedBlockClickHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

public class BlockClickMessage implements IMessage
{
    private int x;
    private int y;
    private int z;
    private int side;
    private float hitX;
    private float hitY;
    private float hitZ;
    private boolean invertShift;

    private boolean failed;

    public BlockClickMessage () { }

    public BlockClickMessage (int x, int y, int z, int side, float hitX, float hitY, float hitZ, boolean invertShift) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
        this.invertShift = invertShift;
    }

    @Override
    public void fromBytes (ByteBuf buf) {
        try {
            x = buf.readInt();
            y = buf.readShort();
            z = buf.readInt();
            side = buf.readByte();
            hitX = buf.readFloat();
            hitY = buf.readFloat();
            hitZ = buf.readFloat();
            invertShift = buf.readBoolean();
        }
        catch (IndexOutOfBoundsException e) {
            failed = true;
            FMLLog.log(StorageDrawers.MOD_ID, Level.ERROR, e, "BlockClickMessage: Unexpected end of packet.\nMessage: %s", ByteBufUtil.hexDump(buf, 0, buf.writerIndex()));
        }
    }

    @Override
    public void toBytes (ByteBuf buf) {
        buf.writeInt(x);
        buf.writeShort(y);
        buf.writeInt(z);
        buf.writeByte(side);
        buf.writeFloat(hitX);
        buf.writeFloat(hitY);
        buf.writeFloat(hitZ);
        buf.writeBoolean(invertShift);
    }

    public static class Handler implements IMessageHandler<BlockClickMessage, IMessage>
    {
        @Override
        public IMessage onMessage (BlockClickMessage message, MessageContext ctx) {
            if (!message.failed && ctx.side == Side.SERVER) {
                World world = ctx.getServerHandler().playerEntity.getEntityWorld();
                if (world != null) {
                    BlockPos pos = new BlockPos(message.x, message.y, message.z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (block instanceof IExtendedBlockClickHandler)
                        ((IExtendedBlockClickHandler) block).onBlockClicked(world, pos, ctx.getServerHandler().playerEntity, EnumFacing.getFront(message.side), message.hitX, message.hitY, message.hitZ, message.invertShift);
                }
            }

            return null;
        }
    }
}
