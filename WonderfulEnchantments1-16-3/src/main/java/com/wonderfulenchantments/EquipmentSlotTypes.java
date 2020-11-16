package com.wonderfulenchantments;

import net.minecraft.inventory.EquipmentSlotType;

public class EquipmentSlotTypes {
	public static final EquipmentSlotType[]
		ARMOR			= new EquipmentSlotType[]{
			EquipmentSlotType.HEAD,
			EquipmentSlotType.CHEST,
			EquipmentSlotType.LEGS,
			EquipmentSlotType.FEET
		},
		BOTH_HANDS 		= new EquipmentSlotType[]{
			EquipmentSlotType.MAINHAND,
			EquipmentSlotType.OFFHAND
		},
		ARMOR_AND_HANDS	= new EquipmentSlotType[]{
			EquipmentSlotType.MAINHAND,
			EquipmentSlotType.OFFHAND,
			EquipmentSlotType.HEAD,
			EquipmentSlotType.CHEST,
			EquipmentSlotType.LEGS,
			EquipmentSlotType.FEET
		};
}
