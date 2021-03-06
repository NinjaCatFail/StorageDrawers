package com.jaquadro.minecraft.storagedrawers.block;

import com.jaquadro.minecraft.chameleon.block.properties.UnlistedTileEntity;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.core.handlers.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockFramingTable extends BlockContainer
{
    public static final int[][] leftOffset = new int[][] {{0, 0}, {0, 0}, {1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    public static final int[][] rightOffset = new int[][] {{0, 0}, {0, 0}, {-1, 0}, {1, 0}, {0, 1}, {0, -1}};

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool RIGHT_SIDE = PropertyBool.create("right");

    public static final IUnlistedProperty<TileEntityFramingTable> TILE = UnlistedTileEntity.create(TileEntityFramingTable.class);

    public BlockFramingTable (String blockName) {
        super(Material.wood);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(2.5f);
        setStepSound(soundTypeWood);
        setUnlocalizedName(blockName);

        setDefaultState(blockState.getBaseState().withProperty(RIGHT_SIDE, true));
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        return new TileEntityFramingTable();
    }

    @Override
    public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float vx, float vy, float vz) {
        int priX = pos.getX() + getXOff(state);
        int priZ = pos.getZ() + getZOff(state);

        IBlockState targetState = world.getBlockState(new BlockPos(priX, pos.getY(), priZ));
        if (targetState.getBlock() != this || !isRightBlock(targetState))
            return false;

        player.openGui(StorageDrawers.instance, GuiHandler.framingGuiID, world, priX, pos.getY(), priZ);
        return true;
    }

    private int getXOff (IBlockState state) {
        if (isRightBlock(state))
            return 0;

        return rightOffset[getDirection(state).getIndex()][0];
    }

    private int getZOff (IBlockState state) {
        if (isRightBlock(state))
            return 0;

        return rightOffset[getDirection(state).getIndex()][1];
    }

    @Override
    public boolean isOpaqueCube () {
        return false;
    }

    @Override
    public boolean isFullCube () {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int getRenderType () {
        return 3;
    }

    @Override
    public boolean canRenderInLayer (EnumWorldBlockLayer layer) {
        return layer == EnumWorldBlockLayer.SOLID || layer == EnumWorldBlockLayer.TRANSLUCENT;
    }

    @Override
    public void onNeighborBlockChange (World world, BlockPos pos, IBlockState state, Block block) {
        EnumFacing side = getDirection(state);
        if (isRightBlock(state)) {
            BlockPos otherPos = pos.add(leftOffset[side.getIndex()][0], 0, leftOffset[side.getIndex()][1]);
            if (world.getBlockState(otherPos).getBlock() != this) {
                world.setBlockToAir(pos);
                if (!world.isRemote)
                    dropBlockAsItem(world, pos, state, 0);
            }
        }
        else {
            BlockPos otherPos = pos.add(rightOffset[side.getIndex()][0], 0, rightOffset[side.getIndex()][1]);
            if (world.getBlockState(otherPos).getBlock() != this)
                world.setBlockToAir(pos);
        }
    }

    @Override
    public Item getItemDropped (IBlockState state, Random rand, int fortune) {
        return isPrimaryBlock(state) ? Item.getItemFromBlock(ModBlocks.framingTable) : Item.getItemById(0);
    }

    @Override
    public void dropBlockAsItemWithChance (World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (isPrimaryBlock(state))
            super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
    }

    @Override
    public int getMobilityFlag () {
        return 1;
    }

    @Override
    public void onBlockHarvested (World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.capabilities.isCreativeMode && !isPrimaryBlock(state)) {
            EnumFacing side = getDirection(state);
            pos = pos.add(rightOffset[side.getIndex()][0], 0, rightOffset[side.getIndex()][1]);

            if (world.getBlockState(pos).getBlock() == this)
                world.setBlockToAir(pos);
        }
    }

    @Override
    public void breakBlock (World world, BlockPos pos, IBlockState state) {
        TileEntityFramingTable tile = (TileEntityFramingTable)world.getTileEntity(pos);
        if (tile != null && isPrimaryBlock(state))
            InventoryHelper.dropInventoryItems(world, pos, tile);

        super.breakBlock(world, pos, state);
    }

    public static EnumFacing getDirection (IBlockState state) {
        return state.getValue(FACING);
    }

    public static boolean isRightBlock (IBlockState state) {
        return state.getValue(RIGHT_SIDE);
    }

    public static boolean isPrimaryBlock (IBlockState state) {
        return isRightBlock(state);
    }

    @Override
    public IBlockState getStateFromMeta (int meta) {
        EnumFacing side = EnumFacing.getFront(meta & 0x7);
        if (side.getAxis() == EnumFacing.Axis.Y)
            side = EnumFacing.NORTH;

        return getDefaultState().withProperty(RIGHT_SIDE, (meta & 0x8) == 0).withProperty(FACING, side);
    }

    @Override
    public int getMetaFromState (IBlockState state) {
        return (isRightBlock(state) ? 0x8 : 0) | getDirection(state).getIndex();
    }

    @Override
    protected BlockState createBlockState () {
        return new ExtendedBlockState(this, new IProperty[] { RIGHT_SIDE, FACING }, new IUnlistedProperty[] { TILE });
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {
        state = getActualState(state, world, pos);
        if (!(state instanceof IExtendedBlockState))
            return state;

        TileEntityFramingTable tile = (TileEntityFramingTable)world.getTileEntity(pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState)state).withProperty(TILE, tile);
    }
}
