package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSlave extends BlockContainer implements INetworked
{
    public BlockSlave (String blockName) {
        super(Material.rock);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setUnlocalizedName(blockName);
        setStepSound(Block.soundTypeStone);
    }

    @Override
    public int getRenderType () {
        return 3;
    }

    @Override
    public TileEntitySlave createNewTileEntity (World world, int meta) {
        return new TileEntitySlave();
    }

    public TileEntitySlave getTileEntity (IBlockAccess blockAccess, BlockPos pos) {
        TileEntity tile = blockAccess.getTileEntity(pos);
        return (tile instanceof TileEntitySlave) ? (TileEntitySlave) tile : null;
    }

    public TileEntitySlave getTileEntitySafe (World world, BlockPos pos) {
        TileEntitySlave tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = createNewTileEntity(world, 0);
            world.setTileEntity(pos, tile);
        }

        return tile;
    }
}
