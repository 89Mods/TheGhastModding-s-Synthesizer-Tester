package theGhastModding.synthTester.main;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import theGhastModding.synthTester.gui.SynthTesterPanel;
import theGhastModding.synthesizer.main.TGMSynthesizer;

public class SynthTesterMain {
	
	public static JFrame frame;
	
	public static final String NAME = "TheGhastModding's Synthesizer tester";
	public static final String VERSION = "2.3_3";
	
	public static void main(String[] args){
		boolean update = Updater.checkForUpdates();
		boolean laf = false;
		for(String s:args){
			if(s.equals("-lookAndFeelTest")){
				laf = true;
			}
		}
		if(laf){
			try {
		        UIManager.setLookAndFeel(
		                UIManager.getSystemLookAndFeelClassName());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		try {
			TGMSynthesizer.startSynth(44100, false);
		} catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error starting TGM's synthesizer", "Error", JOptionPane.ERROR_MESSAGE);
		}
		frame = new JFrame(NAME + " V" + VERSION + " with TheGhastModding's Synthesizer V" + TGMSynthesizer.getVersion());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setContentPane(new SynthTesterPanel(laf));
		frame.pack();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		frame.setLocation((int)(d.getWidth() / 2 - 341), (int)(d.getHeight() / 2 -  182));
		frame.setVisible(true);
		frame.requestFocus();
		if(update){
			int option = JOptionPane.showConfirmDialog(frame, "An new version is available to download. Would you like to download it now?", "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(option == 0){
				try {
					Desktop.getDesktop().browse(new URL("https://www.dropbox.com/s/u1vc58x21vk79bv/MIDISequencer.zip?dl=0").toURI());
				} catch(Exception e2){
		        	JOptionPane.showMessageDialog(frame, "Error opening download page", "Error", JOptionPane.ERROR_MESSAGE);
		        	e2.printStackTrace();
		        	return;
				}
			}
		}
	}
	
}