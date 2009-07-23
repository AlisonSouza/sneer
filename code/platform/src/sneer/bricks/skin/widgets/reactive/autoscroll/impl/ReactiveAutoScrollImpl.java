package sneer.bricks.skin.widgets.reactive.autoscroll.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.FocusAdapter;

import javax.swing.JScrollPane;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.synth.scroll.SynthScrolls;
import sneer.bricks.skin.widgets.autoscroll.AutoScroll;
import sneer.bricks.skin.widgets.reactive.autoscroll.ReactiveAutoScroll;
import sneer.foundation.lang.Consumer;

public class ReactiveAutoScrollImpl implements ReactiveAutoScroll {

	@Override
	public <T> JScrollPane create(EventSource<T> eventSource, final Consumer<T> receiver) {
		
		final JScrollPane result = my(SynthScrolls.class).create();
		
		Contract reception = my(Signals.class).receive(eventSource, new Consumer<T>() {  @Override public void consume(final T change) {
			my(AutoScroll.class).runWithAutoscroll(result, new Runnable() {  @Override public void run() {
				receiver.consume(change);
			}});
		}});
		
		holdReceivers(result, reception);
		return result;
	}

	private void holdReceivers(JScrollPane scroll, final Contract reception) {
		scroll.addFocusListener(new FocusAdapter(){
			@SuppressWarnings({ "unused" })
			Contract _refToAvoidGc = reception;
		});
	}
}
