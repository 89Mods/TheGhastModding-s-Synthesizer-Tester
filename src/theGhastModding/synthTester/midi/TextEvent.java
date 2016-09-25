package theGhastModding.synthTester.midi;

public class TextEvent extends MIDIEvent {
	
	private String text = "";
	
	public TextEvent(long tick, String text){
		super(tick, 0xff);
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
}