package theGhastModding.synthTester.midi;

public class ProgramChangeEvent extends MIDIEvent {
	
	private int program;
	private int channel;
	
	public ProgramChangeEvent(long tick, int program, int channel) {
		super(tick, 0xC0);
		this.program = program;
		this.channel = channel;
	}
	
	public int getProgram(){
		return program;
	}
	
	public int getChannel(){
		return channel;
	}
	
}
