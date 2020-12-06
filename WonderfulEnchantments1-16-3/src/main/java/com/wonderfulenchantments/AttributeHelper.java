package com.wonderfulenchantments;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

import java.util.UUID;

public class AttributeHelper {
	public static class Attributes {
		public static final Attribute MAX_HEALTH = net.minecraft.entity.ai.attributes.Attributes.field_233818_a_;
		public static final Attribute MINING_SPEED = net.minecraft.entity.ai.attributes.Attributes.field_233821_d_;
		public static final Attribute MOVEMENT_SPEED = net.minecraft.entity.ai.attributes.Attributes.field_233821_d_;
		public static final Attribute KNOCKBACK_RESISTANCE = net.minecraft.entity.ai.attributes.Attributes.field_233820_c_;
		public static final Attribute ARMOR = net.minecraft.entity.ai.attributes.Attributes.field_233826_i_;
	}
	private final UUID uuid;
	private final String name;
	private final Attribute attribute;
	private final AttributeModifier.Operation operation;
	private double value = 1.0D;

	public AttributeHelper( String uuid, String name, Attribute attribute, AttributeModifier.Operation operation ) {
		this.uuid = UUID.fromString( uuid );
		this.name = name;
		this.attribute = attribute;
		this.operation = operation;
	}

	public AttributeHelper setValue( double value ) {
		this.value = value;

		return this;
	}

	public AttributeHelper apply( LivingEntity livingEntity ) {
		ModifiableAttributeInstance attributeInstance = livingEntity.getAttribute( this.attribute );

		attributeInstance.removeModifier( this.uuid );
		AttributeModifier modifier = new AttributeModifier( this.uuid, this.name, this.value, this.operation );
		attributeInstance.func_233767_b_( modifier );

		return this;
	}
}
