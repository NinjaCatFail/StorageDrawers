package com.jaquadro.minecraft.storagedrawers.item;

import com.google.common.collect.Multimap;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemPersonalKey extends Item
{
    public ItemPersonalKey (String name) {
        setUnlocalizedName(name);
        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack itemStack, EntityPlayer player, List<String> list, boolean par4) {
        String name = getUnlocalizedName(itemStack);
        list.add(StatCollector.translateToLocalFormatted(name + ".description"));
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers (ItemStack item) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(item);
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Weapon modifier", (double)2, 0));
        return multimap;
    }

    public String getSecurityProviderKey (int meta) {
        switch (meta) {
            case 0: return null;
            default: return null;
        }
    }
}
