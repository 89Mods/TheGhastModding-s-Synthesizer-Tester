package theGhastModding.synthTester.midi;

public abstract class MIDIEvent {
	
	private long tick;
	private int eventMeta;
	private boolean used;
	
	public MIDIEvent(long tick, int eventMeta){
		this.tick = tick;
		used = false;
		this.eventMeta = eventMeta;
	}
	
	public long getTick(){
		return tick;
	}
	
	public boolean isUsed() {
		return used;
	}
	
	public void setUsed(boolean used) {
		this.used = used;
	}
	
	public int getEventMeta() {
		return eventMeta;
	}
	
}
