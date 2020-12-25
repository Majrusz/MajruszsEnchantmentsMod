package com.wonderfulenchantments;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EntityEquipmentSlots {
	public static final EntityEquipmentSlot[]
		ARMOR			= new EntityEquipmentSlot[]{
			EntityEquipmentSlot.HEAD,
			EntityEquipmentSlot.CHEST,
			EntityEquipmentSlot.LEGS,
			EntityEquipmentSlot.FEET
		},
		BOTH_HANDS 		= new EntityEquipmentSlot[]{
			EntityEquipmentSlot.MAINHAND,
			EntityEquipmentSlot.OFFHAND
		},
		ARMOR_AND_HANDS	= new EntityEquipmentSlot[]{
			EntityEquipmentSlot.MAINHAND,
			EntityEquipmentSlot.OFFHAND,
			EntityEquipmentSlot.HEAD,
			EntityEquipmentSlot.CHEST,
			EntityEquipmentSlot.LEGS,
			EntityEquipmentSlot.FEET
		};
}
