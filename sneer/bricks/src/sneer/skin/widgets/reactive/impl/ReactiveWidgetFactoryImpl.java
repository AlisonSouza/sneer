package sneer.skin.widgets.reactive.impl;

import static sneer.commons.environments.Environments.my;

import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import sneer.hardware.cpu.lang.PickyConsumer;
import sneer.hardware.gui.guithread.GuiThread;
import sneer.pulp.reactive.Signal;
import sneer.pulp.reactive.Signals;
import sneer.pulp.reactive.collections.ListSignal;
import sneer.pulp.reactive.signalchooser.SignalChooser;
import sneer.skin.widgets.reactive.ImageWidget;
import sneer.skin.widgets.reactive.LabelProvider;
import sneer.skin.widgets.reactive.ListWidget;
import sneer.skin.widgets.reactive.NotificationPolicy;
import sneer.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.skin.widgets.reactive.TextWidget;
import sneer.skin.widgets.reactive.Widget;

class ReactiveWidgetFactoryImpl implements ReactiveWidgetFactory {
	
	@Override
	public TextWidget<JLabel> newLabel(Signal<?> source) {
		my(GuiThread.class).assertInGuiThread();
		return new RLabelImpl(source);
	}
	
	@Override
	public TextWidget<JLabel> newLabel(Signal<String> source, String synthName) {
		my(GuiThread.class).assertInGuiThread();
		return new RLabelImpl(source, synthName);
	}

	@Override
	public ImageWidget newImage(Signal<Image> source) {
		my(GuiThread.class).assertInGuiThread();
		return new RImageImpl(source);
	}
	
	
	@Override
	public <T> ListModel newListSignalModel(ListSignal<T> input, SignalChooser<T> chooser) {
		return new ListSignalModel<T>(input, chooser);
	}
	
	
	@Override
	public Widget<JFrame> newFrame(Signal<?> source) {
		my(GuiThread.class).assertInGuiThread();
		return new RFrameImpl(source);
	}

	
	@Override
	public TextWidget<JTextField> newEditableLabel(Signal<?> source, PickyConsumer<? super String> setter, NotificationPolicy notificationPolicy) {
		my(GuiThread.class).assertInGuiThread();
		return new REditableLabelImpl(source, setter, notificationPolicy);
	}
	@Override public TextWidget<JTextField> newEditableLabel(Signal<?> source, PickyConsumer<? super String> setter) { return newEditableLabel(source, setter, NotificationPolicy.OnTyping);}
	
	
	@Override
	public TextWidget<JTextField> newTextField(Signal<?> source, PickyConsumer<? super String> setter, NotificationPolicy notificationPolicy) {
		my(GuiThread.class).assertInGuiThread();
		return new RTextFieldImpl(source, setter, notificationPolicy);
	}
	@Override public TextWidget<JTextField> newTextField(Signal<?> source, PickyConsumer<? super String> setter) { return newTextField(source, setter, NotificationPolicy.OnTyping); }

	
	@Override
	public TextWidget<JTextPane> newTextPane(Signal<?> source, PickyConsumer<? super String> setter, NotificationPolicy notificationPolicy) {
		my(GuiThread.class).assertInGuiThread();
		return new RTextPaneImpl(source, setter, notificationPolicy);
	}
	@Override public TextWidget<JTextPane> newTextPane(Signal<?> source, PickyConsumer<? super String> setter) { return newTextPane(source, setter, NotificationPolicy.OnTyping); }
	
	
	@Override 
	public <T> ListWidget<T> newList(ListSignal<T> source) {
		return newList(source, new LabelProvider<T>(){
			@Override public Signal<? extends Image> imageFor(T element) { return my(Signals.class).constant(null); }
			@Override public Signal<String> labelFor(T element) { return my(Signals.class).constant((String)element);
			}});
	}
	@Override
	public <T> ListWidget<T> newList(ListSignal<T> source, LabelProvider<T> provider, ListCellRenderer cellRenderer) {
		my(GuiThread.class).assertInGuiThread();
		return new RListImpl<T>(source, provider, cellRenderer);
	}
	@Override public <T> ListWidget<T> newList(ListSignal<T> source, LabelProvider<T> provider) { return newList(source, provider, null); }
}