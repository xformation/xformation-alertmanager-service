package com.synectiks.process.server.xformation.service;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synectiks.process.server.xformation.domain.Alert;

public interface AlertService {
	
	public List<Alert> getAllAlerts();
	public Alert getAlert(Long alertId);
	public Alert getAlert(String guid);
	public Alert updateAlert(ObjectNode obj);
	public void deleteAlert(Long alertId);
	public void deleteAlert(String guid);
}