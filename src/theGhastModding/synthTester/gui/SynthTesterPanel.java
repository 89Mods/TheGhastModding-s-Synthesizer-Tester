package theGhastModding.synthTester.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import theGhastModding.synthTester.midi.LyricsEvent;
import theGhastModding.synthTester.midi.MIDIEvent;
import theGhastModding.synthTester.midi.MIDILoader;
import theGhastModding.synthTester.midi.NoteOff;
import theGhastModding.synthTester.midi.NoteOn;
import theGhastModding.synthTester.midi.ProgramChangeEvent;
import theGhastModding.synthTester.midi.TempoEvent;
import theGhastModding.synthTester.midi.TextEvent;
import theGhastModding.synthTester.midi.Track;
import theGhastModding.synthesizer.main.TGMSynthesizer;
import theGhastModding.synthesizer.main.MidiEvent;
import java.awt.Color;

@SuppressWarnings("serial")
public class SynthTesterPanel extends JPanel implements WindowListener {
	
	private SynthTesterPanel instance;
	private boolean playing = false;
	private List<List<MIDIEvent>> midiEvents;
	private int currentPolyphony = 0;
	private JLabel label;
	private JSlider slider_4;
	private int TPB = 0;
	private JSlider slider;
	private JSlider slider_1;
	private long lengthInTicks;
	private JLabel lblLine;
	private JLabel lblLine_1;
	private JLabel lblLine_2;
	private PlaybackThread pt;
	private JButton btnPlay;
	private JCheckBox chckbxAlsoApplyVoice;
	private float currentRenderingTime;
	private JLabel lblCpuUsage;
	private JSlider slider_3;
	
	public SynthTesterPanel(boolean laf){
		super();
		instance = this;
		setPreferredSize(new Dimension(683, 380));
		setLayout(null);
		
		JFileChooser midiChooser = new JFileChooser();
		midiChooser.setDialogTitle("Select a MIDI");
		midiChooser.setFileFilter(new FileNameExtensionFilter("MIDI files", "midi", "MIDI", "mid", "MID"));
		
		JLabel lblTitle = new JLabel("Title:");
		lblTitle.setBounds(10, 36, 663, 14);
		add(lblTitle);
		
		JButton btnOpenFile = new JButton("Click here to open a file...");
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(playing){
					JOptionPane.showMessageDialog(instance, "Cant open file during playback", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(midiChooser.showOpenDialog(instance) == JFileChooser.APPROVE_OPTION){
					if(!midiChooser.getSelectedFile().exists()){
						JOptionPane.showMessageDialog(instance, "The selected file doesnt exist", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						MIDILoader loader = new MIDILoader(midiChooser.getSelectedFile());
						if(loader.getNoteCount() == 0){
							JOptionPane.showMessageDialog(instance, "Error loading MIDI", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						midiEvents = new ArrayList<List<MIDIEvent>>();
						for(Track t:loader.getTracks()){
							midiEvents.add(t.getEvents());
						}
						lblTitle.setText("Title:");
						lblLine.setText("");
						lblLine_1.setText("");
						lblLine_2.setText("");
						for(MIDIEvent event:midiEvents.get(0)){
							if(event instanceof TextEvent && event.getTick() == 0){
								lblTitle.setText("Title: " + ((TextEvent)event).getText());
								break;
							}
						}
						TPB = loader.getTPB();
						lengthInTicks = loader.getLengthInTicks();
						//loader.unload();
						btnOpenFile.setText(midiChooser.getSelectedFile().getPath());
					} catch(Exception e){
						e.printStackTrace();
						JOptionPane.showMessageDialog(instance, "Error loading MIDI", "Error", JOptionPane.ERROR_MESSAGE);
						midiEvents = null;
						return;
					}
				}
			}
		});
		btnOpenFile.setBounds(10, 11, 663, 23);
		add(btnOpenFile);
		
		btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(midiEvents == null || midiEvents.isEmpty()){
					JOptionPane.showMessageDialog(instance, "There is no MIDI loader", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(playing){
					pt.stop();
					btnPlay.setText("Play");
				}else{
					pt = new PlaybackThread();
					pt.start();
					btnPlay.setText("Stop");
				}
			}
		});
		btnPlay.setBounds(10, 61, 64, 23);
		add(btnPlay);
		
		slider = new JSlider();
		slider.setValue(0);
		slider.setMajorTickSpacing(100);
		slider.setPaintTicks(true);
		slider.setBounds(84, 60, 471, 26);
		add(slider);
		
		JCheckBox chckbxReverbChorus = new JCheckBox("Reverb & Chorus");
		chckbxReverbChorus.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				try {
					TGMSynthesizer.setUseFx(chckbxReverbChorus.isSelected());
				} catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(instance, "Error turning " + (chckbxReverbChorus.isSelected() ? "on" : "off") + " Reverb & Chorus");
					return;
				}
			}
		});
		chckbxReverbChorus.setSelected(true);
		chckbxReverbChorus.setBounds(laf ? 561 : 547, 41, laf ? 112 : 126, 23);
		add(chckbxReverbChorus);
		
		Font f = null;
		if(!laf){
			f = new Font("Dialog", Font.PLAIN, 9);
		}else{
			f = new Font("Tahoma", Font.PLAIN, 10);
		}
		
		JPanel tempoPanel = new JPanel();
		tempoPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Tempo", TitledBorder.CENTER, TitledBorder.TOP, f, null));
		tempoPanel.setBounds(561, 65, 51, 158);
		add(tempoPanel);
		tempoPanel.setLayout(null);
		
		JLabel lblTempoLabel = new JLabel("0");
		lblTempoLabel.setVisible(false);
		lblTempoLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblTempoLabel.setBounds(10, 15, 36, 14);
		tempoPanel.add(lblTempoLabel);
		
		slider_1 = new JSlider();
		slider_1.setMajorTickSpacing(100);
		slider_1.setMinimum(1);
		slider_1.setValue(101);
		slider_1.setMaximum(201);
		slider_1.setPaintTicks(true);
		slider_1.setBounds(10, 26, 31, 121);
		tempoPanel.add(slider_1);
		slider_1.setOrientation(SwingConstants.VERTICAL);
		
		JPanel volumePanel = new JPanel();
		volumePanel.setBorder(new TitledBorder(null, "Volume", TitledBorder.CENTER, TitledBorder.TOP, f, null));
		volumePanel.setBounds(622, 65, 51, 158);
		add(volumePanel);
		volumePanel.setLayout(null);
		
		JSlider slider_2 = new JSlider();
		slider_2.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				try {
					TGMSynthesizer.setVolume(slider_2.getValue());
				}catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(instance, "Error changing volume", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		slider_2.setMajorTickSpacing(99);
		slider_2.setValue(100);
		slider_2.setMinimum(1);
		slider_2.setPaintTicks(true);
		slider_2.setOrientation(SwingConstants.VERTICAL);
		slider_2.setBounds(10, 15, 31, 132);
		volumePanel.add(slider_2);
		
		if(!laf){
			f = new Font("Dialog", Font.PLAIN, 12);
		}else{
			f = new Font("Tahoma", Font.PLAIN, 12);
		}
		
		JPanel lyricsPanel = new JPanel();
		lyricsPanel.setBorder(new TitledBorder(null, "Lyrics", TitledBorder.LEADING, TitledBorder.TOP, f, null));
		lyricsPanel.setBounds(10, 95, 545, 73);
		add(lyricsPanel);
		lyricsPanel.setLayout(null);
		
		lblLine = new JLabel("");
		lblLine.setBounds(10, 15, 525, 14);
		lyricsPanel.add(lblLine);
		
		lblLine_1 = new JLabel("");
		lblLine_1.setBounds(10, 31, 525, 14);
		lyricsPanel.add(lblLine_1);
		
		lblLine_2 = new JLabel("");
		lblLine_2.setBounds(10, 47, 525, 14);
		lyricsPanel.add(lblLine_2);
		
		JPanel soundfontPanel = new JPanel();
		soundfontPanel.setBorder(new TitledBorder(null, "Soundfont", TitledBorder.LEADING, TitledBorder.TOP, f, null));
		soundfontPanel.setBounds(10, 170, 545, 53);
		add(soundfontPanel);
		soundfontPanel.setLayout(null);
		
		JFileChooser soundfontChooser = new JFileChooser();
		soundfontChooser.setDialogTitle("Select a soundfont");
		soundfontChooser.setFileFilter(new FileNameExtensionFilter("Soundfonts", "sf2", "SF2", "sfz", "SFZ"));
		
		JLabel lblLoadedsoundfont = new JLabel("no soundfont");
		lblLoadedsoundfont.setBounds(10, 15, laf ? 414 : 384, 14);
		soundfontPanel.add(lblLoadedsoundfont);
		
		JLabel lblSamples = new JLabel("");
		lblSamples.setBounds(10, 28, laf ? 414 : 384, 14);
		soundfontPanel.add(lblSamples);
		
		JButton btnOpenreplace = new JButton("Open/Replace");
		btnOpenreplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(soundfontChooser.showOpenDialog(instance) == JFileChooser.APPROVE_OPTION){
					if(!soundfontChooser.getSelectedFile().exists()){
						JOptionPane.showMessageDialog(instance, "The selected file doesnt exist", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						TGMSynthesizer.loadFont(soundfontChooser.getSelectedFile().getPath());
						lblLoadedsoundfont.setText(soundfontChooser.getSelectedFile().getName());
					}catch(Exception e){
						e.printStackTrace();
						JOptionPane.showMessageDialog(instance, "Error loading soundfont", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});
		btnOpenreplace.setBounds(laf ? 434 : 404, 19, laf ? 101 : 131, 23);
		soundfontPanel.add(btnOpenreplace);
		
		JPanel voiceControlPanel = new JPanel();
		voiceControlPanel.setBorder(new TitledBorder(null, "Voice Control (Use Left/Right Arrow keys for fine voice limit tunning)", TitledBorder.LEADING, TitledBorder.TOP, f, null));
		voiceControlPanel.setBounds(10, 224, 663, 75);
		add(voiceControlPanel);
		voiceControlPanel.setLayout(null);
		
		label = new JLabel("0/100");
		label.setBounds(507, 34, 133, 14);
		voiceControlPanel.add(label);
		
		slider_4 = new JSlider();
		slider_4.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				label.setText(currentPolyphony + "/" + slider_4.getValue());
				try {
					TGMSynthesizer.setMaxVoices(slider_4.getValue());
				} catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(instance, "Error setting max voices", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		slider_4.setMajorTickSpacing(40);
		slider_4.setValue(100);
		slider_4.setMinimum(1);
		slider_4.setMaximum(30000);
		slider_4.setPaintTicks(true);
		slider_4.setBounds(10, 22, 487, 26);
		voiceControlPanel.add(slider_4);
		
		JLabel lblActiveVoicescurrentVoice = new JLabel("Active voices/Current");
		lblActiveVoicescurrentVoice.setBounds(507, 11, 133, 14);
		voiceControlPanel.add(lblActiveVoicescurrentVoice);
		
		JLabel lblVoiceLimit = new JLabel("voice limit");
		lblVoiceLimit.setBounds(507, 22, 58, 14);
		voiceControlPanel.add(lblVoiceLimit);
		
		chckbxAlsoApplyVoice = new JCheckBox("Also apply voice limit to the maximum amount of notes send to the synth per tick (can mess up audio in extremely spammy parts)");
		chckbxAlsoApplyVoice.setBounds(10, 48, 647, 23);
		voiceControlPanel.add(chckbxAlsoApplyVoice);
		
		JPanel renderingLimitPanel = new JPanel();
		renderingLimitPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Rendering limit", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		renderingLimitPanel.setBounds(10, 310, 663, 59);
		add(renderingLimitPanel);
		renderingLimitPanel.setLayout(null);
		
		lblCpuUsage = new JLabel("CPU usage: 0.0% / unlimited%");
		lblCpuUsage.setBounds(419, 11, 234, 14);
		renderingLimitPanel.add(lblCpuUsage);
		
		slider_3 = new JSlider();
		slider_3.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				lblCpuUsage.setText("CPU usage: " + currentRenderingTime + "% / " + (slider_3.getValue() == 0 ? "unlimited" : slider_3.getValue()) + "%");
				try {
					TGMSynthesizer.setRenderingLimit(slider_3.getValue());
				} catch(Exception e2){
					e2.printStackTrace();
					JOptionPane.showMessageDialog(instance, "Error setting rendering limit", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		slider_3.setMinimum(0);
		slider_3.setValue(0);
		slider_3.setMajorTickSpacing(99);
		slider_3.setPaintTicks(true);
		slider_3.setBounds(10, 22, 399, 26);
		renderingLimitPanel.add(slider_3);
		
		if(!laf){
			for(Component c:this.getComponents()){
				c.setFont(new Font("Dialog", Font.PLAIN, 12));
			}
			for(Component c:soundfontPanel.getComponents()){
				c.setFont(new Font("Dialog", Font.PLAIN, 12));
			}
			for(Component c:renderingLimitPanel.getComponents()){
				c.setFont(new Font("Dialog", Font.PLAIN, 12));
			}
			for(Component c:voiceControlPanel.getComponents()){
				c.setFont(new Font("Dialog", Font.PLAIN, 12));
			}
		}
		try {
			TGMSynthesizer.setRenderingLimit(0);
		} catch(Exception e2){
			e2.printStackTrace();
			JOptionPane.showMessageDialog(instance, "Error setting rendering limit", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	private void addLyrics(String s){
		if(lblLine.getText().isEmpty()){
			lblLine.setText(s);
		}else if(lblLine_1.getText().isEmpty()){
			lblLine_1.setText(s);
		}else if(lblLine_2.getText().isEmpty()){
			lblLine_2.setText(s);
		}else{
			lblLine.setText(lblLine_1.getText());
			lblLine_1.setText(lblLine_2.getText());
			lblLine_2.setText(s);
		}
			
	}
	
	private synchronized List<List<MIDIEvent>> getMidiEvents(){
		return midiEvents;
	}
	
	private class PlaybackThread implements Runnable {
		
		private Thread t;
		private double TPS;
		private double tickPosition = 0;
		private double timerThen;
		private double timerNow;
		private int poly = 0;
		
		public PlaybackThread(){
			t = new Thread(this);
		}
		
		public void start(){
			if(getMidiEvents() == null){
				return;
			}
			if(getMidiEvents().isEmpty()){
				return;
			}
			TempoEvent firstTempo = null;
			for(List<MIDIEvent> eventList:getMidiEvents()){
				for(MIDIEvent event:eventList){
					if(event instanceof TempoEvent){
						if(event.getTick() == 0){
							firstTempo = (TempoEvent)event;
						}
					}
				}
			}
			TPS = (firstTempo.getBpm() / 60) * TPB;
			tickPosition = 0;
			slider.setEnabled(false);
			slider.setValue(0);
			timerThen = System.nanoTime();
			if(!t.isAlive()){
				t.start();
			}
		}
		
		public void stop(){
			playing = false;
			try {
				t.join();
			} catch(Exception e){
				e.printStackTrace();
			}
			slider.setValue(0);
			slider.setEnabled(true);
		}
		
		@Override
		public void run() {
			playing = true;
			int[] currentEvents = new int[getMidiEvents().size()];
			for(int i = 0; i < currentEvents.length; i++) currentEvents[i] = 0;
			while(playing){
				try {
					poly = 0;
					currentPolyphony = TGMSynthesizer.getActiveVoices();
					currentRenderingTime = TGMSynthesizer.getRenderingTime();
					label.setText(currentPolyphony + "/" + slider_4.getValue());
					lblCpuUsage.setText("CPU usage: " + currentRenderingTime + "% / " + (slider_3.getValue() == 0 ? "unlimited" : slider_3.getValue()) + "%");
					timerNow = System.nanoTime();
				    tickPosition += ((((double)timerNow - (double)timerThen)/1000000000D) * (TPS * ((double)(((double)slider_1.getValue() - 1D) / 10D) / 10D)));
				    timerThen = timerNow;
				    if(tickPosition >= lengthInTicks){
						playing = false;
					}
				    for(int i = 0; i < getMidiEvents().size(); i++){
					    if(currentEvents[i] >= getMidiEvents().get(i).size()){
					    	continue;
					    }
					    if(getMidiEvents().get(i).get(currentEvents[i]).getTick() <= tickPosition){
					    	processEvent(getMidiEvents().get(i).get(currentEvents[i]));
					    	currentEvents[i]++;
						    if(currentEvents[i] >= getMidiEvents().get(i).size()){
						    	continue;
						    }
					    	while(getMidiEvents().get(i).get(currentEvents[i]).getTick() <= tickPosition){
					    		processEvent(getMidiEvents().get(i).get(currentEvents[i]));
					    		currentEvents[i]++;
							    if(currentEvents[i] >= getMidiEvents().get(i).size()){
							    	break;
							    }
					    	}
					    }
				    }
				    if(tickPosition / (double)lengthInTicks * 100 < slider.getMaximum()){
				    	slider.setValue((int) (tickPosition / (double)lengthInTicks * 100));
				    }
				    Thread.sleep(1);
				}catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(instance, "Error during playback", "Error", JOptionPane.ERROR_MESSAGE);
					endPlayback();
					break;
				}
			}
			endPlayback();
		}
		
		private void processEvent(MIDIEvent event) throws Exception {
			if(event instanceof TempoEvent){
				TPS = (((TempoEvent)event).getBpm() / 60) * TPB;
			}
			if(event instanceof LyricsEvent){
				addLyrics(((LyricsEvent)event).getLyrics());
			}
			if(event instanceof ProgramChangeEvent){
				TGMSynthesizer.sendEvent(new MidiEvent(MidiEvent.MIDI_EVENT_PROGRAM, ((ProgramChangeEvent)event).getChannel(), ((ProgramChangeEvent)event).getProgram(), 0));
			}
			if(event instanceof NoteOn){
				if(chckbxAlsoApplyVoice.isSelected()){
					if(poly <= slider_4.getValue()){
						TGMSynthesizer.sendEvent(new MidiEvent(MidiEvent.MIDI_EVENT_NOTE, ((NoteOn) event).getChannel(), ((NoteOn) event).getNoteValue(), ((NoteOn) event).getVelocity()));
						poly++;
					}
				}else{
					TGMSynthesizer.sendEvent(new MidiEvent(MidiEvent.MIDI_EVENT_NOTE, ((NoteOn) event).getChannel(), ((NoteOn) event).getNoteValue(), ((NoteOn) event).getVelocity()));
				}
			}
			if(event instanceof NoteOff){
				TGMSynthesizer.sendEvent(new MidiEvent(MidiEvent.MIDI_EVENT_NOTE, ((NoteOff) event).getChannel(), ((NoteOff) event).getNoteValue(), 0));
			}
		}
		
		private void endPlayback(){
			playing = false;
			try {
				for(int i = 0; i < 16; i++){
					TGMSynthesizer.sendEvent(new MidiEvent(MidiEvent.MIDI_EVENT_NOTESOFF, i, 0,0));
				}
			} catch(Exception e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(instance, "Error sending NoteOff to all channels", "Error", JOptionPane.ERROR_MESSAGE);
			}
			currentPolyphony = 0;
			label.setText(currentPolyphony + "/" + slider_4.getValue());
			currentRenderingTime = 0;
			lblCpuUsage.setText("CPU usage: " + currentRenderingTime + "% / " + (slider_3.getValue() == 0 ? "unlimited" : slider_3.getValue()) + "%");
			btnPlay.setText("Play");
			slider.setValue(0);
			slider.setEnabled(true);
		}
		
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}
	
	@Override
	public void windowClosed(WindowEvent arg0) {}
	
	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			TGMSynthesizer.stopSynth();
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error closing TGM's synthesizer", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	
	@Override
	public void windowIconified(WindowEvent arg0) {}
	
	@Override
	public void windowOpened(WindowEvent arg0) {}
}