package sneer.bricks.skin.widgets.reactive;

import javax.swing.JComponent;

import sneer.bricks.hardware.cpu.lang.PickyConsumer;
import sneer.bricks.pulp.reactive.Signal;


public interface TextWidget<WIDGET extends JComponent> extends ComponentWidget<WIDGET> {

	Signal<?> output();
	
	PickyConsumer<? super String> setter();	

	JComponent[] getWidgets();
}