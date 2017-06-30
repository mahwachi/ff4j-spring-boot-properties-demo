package com.example.demo.ff4j.springboot;

import javax.el.PropertyNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.ff4j.exception.PropertyAccessException;
import org.ff4j.exception.PropertyAlreadyExistException;
import org.ff4j.property.Property;
import org.ff4j.property.store.AbstractPropertyStore;
import org.ff4j.property.store.PropertyStore;
import org.ff4j.property.util.PropertyFactory;
import org.ff4j.utils.MappingUtil;
import org.ff4j.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Bridge between FF4j {@link PropertyStore} and Spring Boot properties
 *
 * @author Pierre Besson
 */
@Configuration
@EnableConfigurationProperties(FF4jConfigurationProperties.class)
public class SpringBootConfigurationPropertyStore extends AbstractPropertyStore {
	@Autowired
	FF4jConfigurationProperties ff4jPropertyConfiguration;

	public SpringBootConfigurationPropertyStore() {
	}

	@Override
	public <T> void updateProperty(Property<T> prop) {
		super.updateProperty(prop);
	}

	@Override
	public boolean existProperty(String name) {
		Util.assertHasLength(name);
		return ff4jPropertyConfiguration.getProperties().get(name) != null;
	}

	@Override
	public <T> void createProperty(Property<T> value) {
		Util.assertNotNull(value);
		Util.hasLength(value.getName());
		if (existProperty(value.getName())) {
			throw new PropertyAlreadyExistException(value.getName());
		}
		String propertyName = value.getName();
		FF4jConfigurationProperties.Property configurationProperty = new FF4jConfigurationProperties.Property();
		configurationProperty.setType(MappingUtil.mapPropertyType(value.getType()));
		configurationProperty.setValue(value.getValue().toString());
		configurationProperty.setDescription(value.getDescription());
		List<String> configurationFixedValues = new ArrayList<>();
		for (T fixedValue : value.getFixedValues()) {
			configurationFixedValues.add(String.valueOf(fixedValue));
		}
		configurationProperty.setFixedValues(configurationFixedValues);
		ff4jPropertyConfiguration.getProperties().put(propertyName, configurationProperty);
	}

	@Override
	public Property<?> readProperty(String name) {
		if (this.existProperty(name)) {
			FF4jConfigurationProperties.Property configurationProperty = ff4jPropertyConfiguration.getProperties().get(name);
			List<String> configurationFixedValues = configurationProperty.getFixedValues();
			Set propertyFixedValues = null;
			if (configurationFixedValues != null && !configurationFixedValues.isEmpty()) {
				propertyFixedValues = new HashSet();
				for (String fixedValue : configurationFixedValues) {
					Object instance = null;
					try {
						//FIXME(hack) find another way to get instanciate a value of the same type as PropertyX from a String
						Property<?> sampleProperty = PropertyFactory.createProperty("sample", MappingUtil.mapPropertyType(configurationProperty.getType()), fixedValue);
						propertyFixedValues.add(sampleProperty.getValue());
					} catch (Exception e) {
						throw new PropertyAccessException("Cannot instantiate property fixedValues field", e);
					}
				}
			}
			return PropertyFactory
				.createProperty(name, MappingUtil.mapPropertyType(configurationProperty.getType()), configurationProperty.getValue(), configurationProperty.getDescription(),
					propertyFixedValues);
		} else {
			throw new PropertyNotFoundException();
		}
	}

	@Override
	public void deleteProperty(String name) {
		if (!existProperty(name)) {
			throw new org.ff4j.exception.PropertyNotFoundException(name);
		}
		ff4jPropertyConfiguration.getProperties().remove(name);
	}

	@Override
	public Map<String, Property<?>> readAllProperties() {
		Map<String, Property<?>> props = new HashMap<String, Property<?>>();
		return this.listPropertyNames().stream().collect(Collectors.toMap(Function.identity(), this::readProperty));
	}

	@Override
	public Set<String> listPropertyNames() {
		return ff4jPropertyConfiguration.getProperties().keySet();
	}

	@Override
	public void clear() {
		ff4jPropertyConfiguration.setProperties(new HashMap<>());
	}
}
