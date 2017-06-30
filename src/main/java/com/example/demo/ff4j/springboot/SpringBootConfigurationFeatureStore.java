package com.example.demo.ff4j.springboot;

import java.util.*;

import org.ff4j.core.Feature;
import org.ff4j.core.FeatureStore;
import org.ff4j.exception.GroupNotFoundException;
import org.ff4j.store.AbstractFeatureStore;
import org.ff4j.utils.MappingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Bridge between FF4j {@link FeatureStore} and Spring Boot properties
 *
 * @author Pierre Besson
 */
@Configuration
@EnableConfigurationProperties(FF4jConfigurationProperties.class)
public class SpringBootConfigurationFeatureStore extends AbstractFeatureStore {
	@Autowired
	private FF4jConfigurationProperties ff4jConfigurationProperties;

	/**
	 * Default constructor.
	 */
	public SpringBootConfigurationFeatureStore() {
	}

	private Feature convertToFF4jFeature(String featureUid, FF4jConfigurationProperties.Feature featureFromConfiguration) {
		Feature feature = new Feature(featureUid);
		feature.setDescription(featureFromConfiguration.getDescription());
		feature.setEnable(featureFromConfiguration.getEnable());
		feature.setGroup(featureFromConfiguration.getGroup());
		Set<String> permissions = new HashSet<>();
		permissions.addAll(featureFromConfiguration.getPermissions());
		feature.setPermissions(permissions);
		FF4jConfigurationProperties.Feature.FlipStrategy featureEntryFlipStrategy = featureFromConfiguration.getFlipStrategy();
		if (featureEntryFlipStrategy != null) {
			feature.setFlippingStrategy(MappingUtil.instanceFlippingStrategy(featureUid, featureEntryFlipStrategy.getClazz(), featureEntryFlipStrategy.getParams()));
		}
		return feature;
	}

	private FF4jConfigurationProperties.Feature convertToFeatureConfigurationProperty(Feature feature) {
		FF4jConfigurationProperties.Feature featureConfiguration = new FF4jConfigurationProperties.Feature();
		featureConfiguration.setUid(feature.getUid());
		featureConfiguration.setEnable(feature.isEnable());
		featureConfiguration.setGroup(feature.getGroup());
		featureConfiguration.setDescription(feature.getDescription());
		List<String> permissionsAsList = new ArrayList<>();
		permissionsAsList.addAll(feature.getPermissions());
		featureConfiguration.setPermissions(permissionsAsList);
		if (feature.getFlippingStrategy() != null) {
			FF4jConfigurationProperties.Feature.FlipStrategy featureConfigurationFlipStrategy = new FF4jConfigurationProperties.Feature.FlipStrategy();
			featureConfigurationFlipStrategy.setClazz(feature.getFlippingStrategy().getClass().getName());
			featureConfigurationFlipStrategy.setParams(feature.getFlippingStrategy().getInitParams());
			featureConfiguration.setFlipStrategy(featureConfigurationFlipStrategy);
		}
		return featureConfiguration;
	}

	@Override
	public void enable(String featureId) {
		ff4jConfigurationProperties.getFeatures().get(featureId).setEnable(true);
	}

	@Override
	public void disable(String featureId) {
		ff4jConfigurationProperties.getFeatures().get(featureId).setEnable(false);
	}

	@Override
	public boolean exist(String featId) {
		return ff4jConfigurationProperties.getFeatures().get(featId) != null;
	}

	@Override
	public void create(Feature feature) {
		Map<String, FF4jConfigurationProperties.Feature> features = ff4jConfigurationProperties.getFeatures();
		features.put(feature.getUid(), convertToFeatureConfigurationProperty(feature));
	}

	@Override
	public Feature read(String featureUid) {
		return convertToFF4jFeature(featureUid, ff4jConfigurationProperties.getFeatures().get(featureUid));
	}

	@Override
	public Map<String, Feature> readAll() {
		Map<String, FF4jConfigurationProperties.Feature> features = ff4jConfigurationProperties.getFeatures();
		Map<String, Feature> resultMap = new HashMap<>();
		for (Map.Entry<String, FF4jConfigurationProperties.Feature> featureEntry : features.entrySet()) {
			Feature feature = convertToFF4jFeature(featureEntry.getKey(), featureEntry.getValue());
			resultMap.put(featureEntry.getKey(), feature);
		}
		return resultMap;
	}

	@Override
	public void delete(String fpId) {
		ff4jConfigurationProperties.getFeatures().remove(fpId);
	}

	@Override
	public void update(Feature fp) {
		ff4jConfigurationProperties.getFeatures().put(fp.getUid(), convertToFeatureConfigurationProperty(fp));
	}

	@Override
	public void grantRoleOnFeature(String flipId, String roleName) {
		ff4jConfigurationProperties.getFeatures().get(flipId).getPermissions().add(roleName);
	}

	@Override
	public void removeRoleFromFeature(String flipId, String roleName) {
		ff4jConfigurationProperties.getFeatures().get(flipId).getPermissions().remove(roleName);
	}

	@Override
	public void enableGroup(String groupName) {
		Map<String, FF4jConfigurationProperties.Feature> features = ff4jConfigurationProperties.getFeatures();
		for (FF4jConfigurationProperties.Feature feature : features.values()) {
			if (feature.getGroup().equals(groupName)) {
				feature.setEnable(true);
			}
		}
	}

	@Override
	public void disableGroup(String groupName) {
		Map<String, FF4jConfigurationProperties.Feature> features = ff4jConfigurationProperties.getFeatures();
		for (FF4jConfigurationProperties.Feature feature : features.values()) {
			if (feature.getGroup().equals(groupName)) {
				feature.setEnable(false);
			}
		}
	}

	@Override
	public boolean existGroup(String groupName) {
		Map<String, FF4jConfigurationProperties.Feature> features = ff4jConfigurationProperties.getFeatures();
		for (FF4jConfigurationProperties.Feature feature : features.values()) {
			if (feature.getGroup().equals(groupName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, Feature> readGroup(String groupName) {
		Map<String, Feature> groupFeaturesMap = new HashMap<>();
		Map<String, FF4jConfigurationProperties.Feature> features = ff4jConfigurationProperties.getFeatures();
		for (Map.Entry<String, FF4jConfigurationProperties.Feature> featureEntry : features.entrySet()) {
			if (featureEntry.getValue().getGroup().equals(groupName)) {
				String featureUid = featureEntry.getKey();
				groupFeaturesMap.put(featureUid, convertToFF4jFeature(featureUid, featureEntry.getValue()));
			}
		}
		if (groupFeaturesMap.isEmpty()) {
			throw new GroupNotFoundException("No features were found to belong to this group");
		}
		return groupFeaturesMap;
	}

	@Override
	public void addToGroup(String featureId, String groupName) {
		ff4jConfigurationProperties.getFeatures().get(featureId).setGroup(groupName);
	}

	@Override
	public void removeFromGroup(String featureId, String groupName) {
		if (!existGroup(groupName)) {
			throw new GroupNotFoundException("No features were found to belong to this group");
		}
		if (ff4jConfigurationProperties.getFeatures().get(featureId).getGroup().equals(groupName)) {
			ff4jConfigurationProperties.getFeatures().get(featureId).setGroup(null);
		}
	}

	@Override
	public Set<String> readAllGroups() {
		Set<String> allGroups = new HashSet<>();
		Map<String, FF4jConfigurationProperties.Feature> features = ff4jConfigurationProperties.getFeatures();
		for (FF4jConfigurationProperties.Feature feature : features.values()) {
			allGroups.add(feature.getGroup());
		}
		return allGroups;
	}

	@Override
	public void clear() {
		ff4jConfigurationProperties.setFeatures(new HashMap<>());
	}
}
