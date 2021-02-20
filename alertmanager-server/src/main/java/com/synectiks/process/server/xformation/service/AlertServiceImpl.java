package com.synectiks.process.server.xformation.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.persist.Transactional;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.xformation.domain.Alert;

public class AlertServiceImpl implements AlertService {

	private static final Logger LOG = LoggerFactory.getLogger(AlertServiceImpl.class);

	
	private EntityManager entityManager = null;

	@Inject
    public AlertServiceImpl() {
		this.entityManager = GuiceInjectorHolder.getInjector().getInstance(EntityManager.class);
    }
	
	@Override
	public List<Alert> getAllAlerts() {
		LOG.info("Start service getAllAlert");
		List<Alert> list = Collections.emptyList();
		try {
			String query = "select a from Alert a";
			list = entityManager.createQuery(query, Alert.class).getResultList();
			for (Alert o : list) {
				LOG.debug("Alert: " + o.toString());
			}
		}catch(Exception e) {
			LOG.error("Exception: ",e);
		}
		LOG.info("End service getAllAlert");
		return list;
	}

	@Override
	public Alert getAlert(Long alertId) {
		LOG.info("Start service getAlert(Long alertId)");
		String query = "select a from Alert a where a.id = :alertId";
		Alert alert = entityManager.createQuery(query, Alert.class).setParameter("alertId", alertId).getSingleResult();
		LOG.debug("Alert: " + alert.toString());
		LOG.info("Start service getAlert(Long alertId)");
		return alert;
	}
	
	@Override
	public Alert getAlert(String guid) {
		LOG.info("Start service getAlert(String guid)");
		String query = "select a from Alert a where a.guid = :guid";
		Alert alert = entityManager.createQuery(query, Alert.class).setParameter("guid", guid).getSingleResult();
		LOG.debug("Alert: " + alert.toString());
		LOG.info("Start service getAlert(String guid)");
		return alert;
	}
	
	@Override
	@Transactional
	public Alert updateAlert(ObjectNode obj) {
		LOG.info("Start service updateAlert");
		String guid = obj.get("guid").asText();
		String alertState = obj.get("alertState").asText();
		
		Alert alert = getAlert(guid);
		if (alert != null) {
			alert.setAlertState(alertState);
			alert.setUpdatedOn(Instant.now());
			alert = entityManager.merge(alert);
			LOG.debug("Alert updated in database");
		} else {
			LOG.warn("No alert found in database for update");
		}
		LOG.info("End service updateAlert");
		return alert;
	}

	@Override
	@Transactional
	public void deleteAlert(Long alertId) {
		LOG.info("Start service deleteAlert. Alert id: "+alertId);
		Alert alert = getAlert(alertId);
		if (alert != null) {
			entityManager.remove(alert);
			LOG.debug("Alert deleted from database");
		} else {
			LOG.warn("No alert found in database for delete");
		}
		LOG.info("End service deleteAlert. Alert id: "+alertId);
	}
	
	@Override
	@Transactional
	public void deleteAlert(String guid) {
		LOG.info("Start service deleteAlert. Alert guid: "+guid);
		Alert alert = getAlert(guid);
		if (alert != null) {
			entityManager.remove(alert);
			LOG.debug("Alert deleted from database");
		} else {
			LOG.warn("No alert found in database for delete");
		}
		LOG.info("End service deleteAlert. Alert guid: "+guid);
	}
}