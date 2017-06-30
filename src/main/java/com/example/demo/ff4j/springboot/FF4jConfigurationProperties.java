package com.example.demo.ff4j.springboot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Declaration of FF4j Spring Boot configuration properties
 *
 * @author Pierre Besson
 */
@ConfigurationProperties(prefix="ff4j")
public class FF4jConfigurationProperties {
	private Map<String, Feature> features = new HashMap<>();

	public Map<String, Feature> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, Feature> features) {
		this.features = features;
	}

	public static class Feature {
		private String uid;
		private Boolean enable = false;
		private String description = "";
		private String group = "";
		private List<String> permissions = new ArrayList<>();
		private FlipStrategy flipStrategy;

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public List<String> getPermissions() {
			return permissions;
		}

		public void setPermissions(List<String> permissions) {
			this.permissions = permissions;
		}

		public FlipStrategy getFlipStrategy() {
			return flipStrategy;
		}
		public void setFlipStrategy(FlipStrategy flipStrategy) {
			this.flipStrategy = flipStrategy;
		}

		public static class FlipStrategy {
			private String clazz;
			private Map<String,String> params = new HashMap<>();

			public String getClazz() {
				return clazz;
			}

			public void setClazz(String clazz) {
				this.clazz = clazz;
			}

			public Map<String,String> getParams() {
				return params;
			}

			public void setParams(Map<String,String> params) {
				this.params = params;
			}
		}
	}

	private Map<String, Property> properties = new HashMap<>();

	public Map<String, Property> getProperties() {
		return properties;
	}

	public static class Property {
		private String type;
		private String value;
		private String description;
		private List<String> fixedValues;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public List<String> getFixedValues() {
			return fixedValues;
		}

		public void setFixedValues(List<String> fixedValues) {
			this.fixedValues = fixedValues;
		}
	}

	public void setProperties(Map<String, Property> properties) {
		this.properties = properties;
	}
}