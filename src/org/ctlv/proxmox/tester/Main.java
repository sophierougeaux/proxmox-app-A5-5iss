package org.ctlv.proxmox.tester;

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;
import org.ctlv.proxmox.api.*;


public class Main {

	public static void main(String[] args) throws LoginException, JSONException, IOException {
		ProxmoxAPI api = new ProxmoxAPI();
		
		// GET INFORMATIONS FROM SERVER
		System.out.println("The cpu usage of the server "+ Constants.SERVER1 + " is " + api.getNode(Constants.SERVER1).getCpu()*100 + '%');
		System.out.println("The cpu usage of the server " + Constants.SERVER2 + " is " + api.getNode(Constants.SERVER2).getCpu()*100 + '%');
		
		System.out.println("The memory usage of the server " + Constants.SERVER1 + " is " + api.getNode(Constants.SERVER1).getMemory_used()*100/api.getNode(Constants.SERVER1).getMemory_total() + "%");
		System.out.println("The memory usage of the server " + Constants.SERVER2 + " is " + api.getNode(Constants.SERVER2).getMemory_used()*100/api.getNode(Constants.SERVER2).getMemory_total() + "%");

		System.out.println("The disk usage of the server " + Constants.SERVER1 + " is " + api.getNode(Constants.SERVER1).getRootfs_used()*100/api.getNode(Constants.SERVER1).getRootfs_total() + "%");
		System.out.println("The disk usage of the server " + Constants.SERVER2 + " is " + api.getNode(Constants.SERVER2).getRootfs_used()*100/api.getNode(Constants.SERVER2).getRootfs_total() + "%");
		
		// Listes les CTs par serveur
		/*for (int i=1; i<=10; i++) {
			String srv ="srv-px"+i;
			System.out.println("CTs sous "+srv);
			List<LXC> cts = api.getCTs(srv);
			
			for (LXC lxc : cts) {
				System.out.println("\t" + lxc.getName());
			}
		}*/
		
		
		// MANIPULATE A CONTAINER
		// Créer un CT - identifier between 1500 and 1599
		// api.createCT(Constants.SERVER1, "1501", Constants.CT_BASE_NAME + "00", 512);
		
		// Start CT
		// api.startCT(Constants.SERVER1, "1501");
		
		// Informations
		System.out.println();
		
		System.out.println("The status of the container is : " + api.getCT(Constants.SERVER1, "1501").getStatus());
		System.out.println("The cpu usage of the container is " + api.getCT(Constants.SERVER1, "1501").getCpu()*100 +" %");
		System.out.println("The disk usage of the container is " + api.getCT(Constants.SERVER1, "1501").getDisk()*100/api.getCT(Constants.SERVER1, "1501").getMaxdisk() + " %");
		System.out.println("The memory usage of the container is " + api.getCT(Constants.SERVER1, "1501").getMem()*100/api.getCT(Constants.SERVER1, "1501").getMaxmem() + " %");
		System.out.println("The name of the container is " + api.getCT(Constants.SERVER1, "1501").getVmid());
		
		
		// Stop all CTs in the server
		/*
		for (int i = 0; i < api.getCTs(Constants.SERVER1).size(); i++) {
			api.stopCT(Constants.SERVER1, api.getCTs(Constants.SERVER1).get(i).getVmid());
		}
		for (int i = 0; i < api.getCTs(Constants.SERVER2).size(); i++) {
			api.stopCT(Constants.SERVER2, api.getCTs(Constants.SERVER2).get(i).getVmid());
		}
		*/
		
		
		// Delete all CTs on the server
		/*
		 for (int i = 0; i < api.getCTs(Constants.SERVER1).size(); i++) {
			api.deleteCT(Constants.SERVER1, api.getCTs(Constants.SERVER1).get(i).getVmid());
		}
		
		for (int i = 0; i < api.getCTs(Constants.SERVER2).size(); i++) {
			api.deleteCT(Constants.SERVER2, api.getCTs(Constants.SERVER2).get(i).getVmid());
		}
		*/
	}

}
