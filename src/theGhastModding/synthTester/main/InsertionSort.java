package theGhastModding.synthTester.main;

import java.util.List;

import theGhastModding.synthTester.midi.MIDIEvent;

public class InsertionSort {
	
	public static List<MIDIEvent> sortByTickTGMMIDIEvents(List<MIDIEvent> list){
		MIDIEvent event;
		for (int i = 1; i < list.size(); i++) {
			event = list.get(i);
			int j = i;
			while (j > 0 && list.get(j - 1).getTick() > event.getTick()) {
				list.set(j, list.get(j - 1));
				j--;
			}
			list.set(j, event);
		}
		return list;
	}
	
}