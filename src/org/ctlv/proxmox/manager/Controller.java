package org.ctlv.proxmox.manager;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Controller {

	ProxmoxAPI api;
	public Controller(ProxmoxAPI api){
		this.api = api;
	}
	
	// migrer un conteneur du serveur "srcServer" vers le serveur "dstServer"
	public void migrateFromTo(String srcServer, String dstServer) throws LoginException, JSONException, IOException, InterruptedException  {
		System.out.println("Starting migration");
		String ctID = "0";
		for (int i = 0; i < api.getCTs(srcServer).size(); i++) {
			if ( (Integer.valueOf(api.getCTs(srcServer).get(i).getVmid()) < (Constants.CT_BASE_ID+100)) && (Integer.valueOf(api.getCTs(Constants.SERVER1).get(i).getVmid()) >= Constants.CT_BASE_ID)) {
				ctID = api.getCTs(srcServer).get(i).getVmid();
				if (api.getCTs(srcServer).get(i).getStatus().equals("running")) {
					api.stopCT(srcServer, ctID);
					Thread.sleep(10000); // to give time to the server to stop the container
				}
				api.migrateCT(srcServer, ctID, dstServer);
				System.out.println("Container " + ctID + " migrated");
				break;
			}
		}
		if (Integer.valueOf(ctID) == 0) {
			System.out.println("No container migrated");
		}

	}

	// arrêter le plus vieux conteneur sur le serveur "server"
	public void offLoad(String server) throws NumberFormatException, LoginException, JSONException, IOException {
		int min = Integer.valueOf(api.getCTs(server).get(0).getVmid());
		// chercher le plus vieux
			for (int i = 1; i < api.getCTs(server).size(); i++) {
				int current = Integer.valueOf(api.getCTs(server).get(0).getVmid());
				if ( (min > current) && api.getCTs(server).get(i).getStatus().equals("running") ){
					min = current;
				}
			}
		// supprimer le plus vieux
			
	 	api.stopCT(server, String.valueOf(min));
	 	
		System.out.println("Container " + String.valueOf(min) + " stopped");

		}

}
