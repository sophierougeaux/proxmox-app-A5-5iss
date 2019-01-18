package org.ctlv.proxmox.generator;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.manager.Controller;
import org.json.JSONException;

public class GeneratorMain {
	
	static Random rndTime = new Random(new Date().getTime());
	public static int getNextEventPeriodic(int period) {
		return period;
	}
	public static int getNextEventUniform(int max) {
		return rndTime.nextInt(max);
	}
	public static int getNextEventExponential(int inv_lambda) {
		float next = (float) (- Math.log(rndTime.nextFloat()) * inv_lambda);
		return (int)next;
	}
	
	public static void main(String[] args) throws InterruptedException, LoginException, JSONException, IOException {
	
		long baseID = Constants.CT_BASE_ID;
		int lambda = 30;
		
		
		Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();

		ProxmoxAPI api = new ProxmoxAPI();
		Controller controller = new Controller(api);
		Random rndServer = new Random(new Date().getTime());
		Random rndRAM = new Random(new Date().getTime()); 
		
		float memAllowedOnServer1 = (float) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);
		float memAllowedOnServer2 = (float) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);
		
		float memMaxMigrationCT1 = (float) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MIGRATION_THRESHOLD);
		float memMaxMigrationCT2 = (float) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MIGRATION_THRESHOLD);
		
		float memLoadManager1 = (float) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.DROPPING_THRESHOLD);
		float memLoadManager2 = (float) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.DROPPING_THRESHOLD);

		
		long CTid = Constants.CT_BASE_ID + 9;
		while (CTid < (Constants.CT_BASE_ID + 100)) {
			
			// Calculer la quantité de RAM utilisée par mes CTs sur chaque serveur
			float memOnServer1 = 0;
			System.out.println("There are (is) " + api.getCTs(Constants.SERVER1).size() + " container(s) on " + Constants.SERVER1);
			
			for (int i = 0; i < api.getCTs(Constants.SERVER1).size(); i++) {
				memOnServer1 = memOnServer1 + api.getCTs(Constants.SERVER1).get(i).getMem();
			}
			
			float memOnServer2 = 0;
			System.out.println("There are (is) " + api.getCTs(Constants.SERVER2).size() + " container(s) on " + Constants.SERVER2);
			for (int i = 0; i < api.getCTs(Constants.SERVER2).size(); i++) {
				memOnServer2 = memOnServer2 + api.getCTs(Constants.SERVER2).get(i).getMem();
			}
			
			// CLUSTER MANAGER
			// migration
			if (memOnServer1 > memMaxMigrationCT1) {
				controller.migrateFromTo(Constants.SERVER1, Constants.SERVER2);
			}
			if (memOnServer2 > memMaxMigrationCT2) {
				controller.migrateFromTo(Constants.SERVER2, Constants.SERVER1);
			}
			
			// delete
			if (memOnServer1 > memLoadManager1) {
				controller.offLoad(Constants.SERVER1);
			}
			if (memOnServer2 > memLoadManager2) {
				controller.offLoad(Constants.SERVER2);
			}			
			
			// CT GENERATOR
			// Mémoire autorisée sur chaque serveur
			/*System.out.println(api.getNode(Constants.SERVER1).getMemory_total());
			System.out.println(memOnServer1);
			System.out.println(memAllowedOnServer1);*/

			float memRatioOnServer1 = memOnServer1*100f/memAllowedOnServer1;
			float memRatioOnServer2 = memOnServer2*100f/memAllowedOnServer2;
			
			System.out.println("The RAM used by the containers on " + Constants.SERVER1 + " reaches " + memRatioOnServer1 + " % of the allowed RAM usage");
			System.out.println("The RAM used by the containers on " + Constants.SERVER2 + " reaches " + memRatioOnServer2 + " % of the allowed RAM usage");

			
			if ((memOnServer1 < memAllowedOnServer1) && (memOnServer2 < memAllowedOnServer2)) {  // Exemple de condition de l'arrêt de la génération de CTs
				
				// choisir un serveur aléatoirement avec les ratios spécifiés 66% vs 33%
				String serverName;
				if (rndServer.nextFloat() < Constants.CT_CREATION_RATIO_ON_SERVER1)
					serverName = Constants.SERVER1;
				else
					serverName = Constants.SERVER2;
				
				// créer un contenaire sur ce serveur
				api.createCT(serverName, String.valueOf(CTid), Constants.CT_BASE_NAME + String.valueOf(CTid - Constants.CT_BASE_ID), 768);
				System.out.println("Container " + CTid + " created");
								
				// planifier la prochaine création
				int timeToWait = getNextEventExponential(lambda); // par exemple une loi expo d'une moyenne de 30sec
				
				// attendre jusqu'au prochain évènement
				Thread.sleep(1000 * timeToWait);
				
				// starter le server
				/*api.startCT(serverName, String.valueOf(CTid));
				System.out.println("Container " + CTid + " started");*/
				
				CTid++;
			}
			else {
				System.out.println("Servers are loaded, waiting ...");
				Thread.sleep(Constants.GENERATION_WAIT_TIME* 1000);
			}
			

		}
		
	}

}
