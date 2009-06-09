package sneer.bricks.skin.widgets.reactive.autoscroll;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import sneer.bricks.hardware.cpu.lang.Consumer;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.brickness.Brick;

@Brick
public interface AutoScrolls {
	
	<T> JScrollPane create(JTextPane component, ListSignal<T> inputSignal, Consumer<CollectionChange<T>> receiver);
	<T> JScrollPane create(EventSource<T> eventSource);

}