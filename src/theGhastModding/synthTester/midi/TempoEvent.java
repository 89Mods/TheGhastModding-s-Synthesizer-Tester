package theGhastModding.synthTester.midi;

public class TempoEvent extends MIDIEvent {
	
	private float bpm;
	private int mpqn;
	
	public TempoEvent(long tick, float bpm, int mpqn){
		super(tick, 0xff);
		this.bpm = bpm;
		this.mpqn = mpqn;
	}
	
	public float getBpm(){
		return bpm;
	}
	
	public int getMpqn(){
		return mpqn;
	}
	
}
