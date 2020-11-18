package com.wonderfulenchantments;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

import java.util.UUID;

public class AttributeHelper {
	private final UUID uuid;
	private final String name;
	private final IAttribute attribute;
	private final AttributeModifier.Operation operation;
	private double value = 1.0D;

	public AttributeHelper( String uuid, String name, IAttribute attribute, AttributeModifier.Operation operation ) {
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
		IAttributeInstance attributeInstance = livingEntity.getAttribute( this.attribute );

		attributeInstance.removeModifier( this.uuid );
		AttributeModifier modifier = new AttributeModifier( this.uuid, this.name, this.value, this.operation );
		attributeInstance.applyModifier( modifier );

		return this;
	}
}
