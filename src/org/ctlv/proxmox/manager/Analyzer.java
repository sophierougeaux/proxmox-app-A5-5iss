package org.ctlv.proxmox.manager;

import java.util.List;
import java.util.Map;

import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	
	public void analyze(Map<String, List<LXC>> myCTsPerServer)  {

		// Calculer la quantit� de RAM utilis�e par mes CTs sur chaque serveur
		// ...

		
		// M�moire autoris�e sur chaque serveur
		// ...

		
		// Analyse et Actions
		// ...
		
	}

}
