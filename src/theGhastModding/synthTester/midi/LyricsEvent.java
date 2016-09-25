package theGhastModding.synthTester.midi;

public class LyricsEvent extends MIDIEvent {
	
	private String text = "";
	
	public LyricsEvent(long tick, String text){
		super(tick, 0xff);
		this.text = text;
	}
	
	public String getLyrics(){
		return text;
	}
	
}