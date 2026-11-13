package com.nexora.helpdesk.desktop;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.nexora.helpdesk.desktop.ui.LoginFrame;

public class Main {
	public static void main(String[] args) {

	        SwingUtilities.invokeLater(() -> {

	            try {
	                UIManager.setLookAndFeel(
	                    UIManager.getSystemLookAndFeelClassName()
	                );
	            } catch (Exception e) {
	                e.printStackTrace();
	            }

	          
	        });
	    }
	}

