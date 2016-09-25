package theGhastModding.synthTester.midi;

public class EndOfTrackEvent extends MIDIEvent {
	
	public EndOfTrackEvent(long time){
		super(time, 0xff);
	}
	
}
