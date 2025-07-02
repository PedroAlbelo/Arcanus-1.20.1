package net.PaiPain.arcanus.item.custom;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item;

public class MaskItem extends ArmorItem {
    public MaskItem() {
        super(ArmorMaterials.IRON, Type.HELMET, new Item.Properties().stacksTo(1));
    }
}
