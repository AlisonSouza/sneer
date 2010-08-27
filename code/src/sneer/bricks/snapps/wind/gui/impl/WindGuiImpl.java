package sneer.bricks.snapps.wind.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultStyledDocument;

import sneer.bricks.hardware.gui.trayicon.TrayIcons;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.main.synth.Synth;
import sneer.bricks.skin.main.synth.scroll.SynthScrolls;
import sneer.bricks.skin.widgets.reactive.NotificationPolicy;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.skin.widgets.reactive.autoscroll.ReactiveAutoScroll;
import sneer.bricks.snapps.wind.Shout;
import sneer.bricks.snapps.wind.Wind;
import sneer.bricks.snapps.wind.gui.WindGui;
import sneer.foundation.lang.Consumer;

class WindGuiImpl implements WindGui {

	{ my(Synth.class).load(this.getClass()); }

	private Container _container;
	private final Wind _wind = my(Wind.class);
	private final JTextPane _shoutsList = new JTextPane();

	private final TextWidget<JTextPane> _myShout = my(ReactiveWidgetFactory.class).newTextPane(
		my(Signals.class).newRegister("").output(),  _wind.megaphone(), NotificationPolicy.OnEnterPressed
	);

	private final JScrollPane _scrollPane = my(ReactiveAutoScroll.class).create(_wind.shoutsHeard(),
		new Consumer<CollectionChange<Shout>>() { 

			ShoutPainter _shoutPainter = new ShoutPainter((DefaultStyledDocument) _shoutsList.getStyledDocument());

			@Override 
			public void consume(CollectionChange<Shout> change) {
				if (!change.elementsRemoved().isEmpty()){
					_shoutPainter.repaintAllShouts(_wind.shoutsHeard());
					return;
				}

				for (Shout shout : change.elementsAdded())
					_shoutPainter.appendShout(shout);

			}
		}
	);

	@SuppressWarnings("unused") private Object _refToAvoidGc;

	public WindGuiImpl() {
		my(InstrumentRegistry.class).registerInstrument(this);
	} 
	
	@Override
	public void init(InstrumentPanel window) {
		_container = window.contentPane();
		initGui();
		initShoutReceiver();
		new WindClipboardSupport();
	}

	private void initGui() {
		_scrollPane.getViewport().add(_shoutsList);
		JScrollPane scrollShout = my(SynthScrolls.class).create();
		JPanel horizontalLimit = new JPanel(){
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				preferredSize.setSize(_container.getWidth()-30, preferredSize.getHeight());
				return preferredSize;
			}
		};
		horizontalLimit.setLayout(new BorderLayout());
		horizontalLimit.add(_myShout.getComponent());
		scrollShout.getViewport().add(horizontalLimit);	
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _scrollPane, scrollShout);
		split.setBorder(new EmptyBorder(0,0,0,0));

		split.setOpaque(false);
		split.setDividerLocation((int) (defaultHeight()*0.68)); 
		split.setDividerSize(3);
		_container.setLayout(new BorderLayout());
		_container.add(split, BorderLayout.CENTER);

		_shoutsList.setBorder(new EmptyBorder(0,0,0,0));
		_myShout.getComponent().setBorder(new EmptyBorder(0,0,0,0));
		
		_shoutsList.addFocusListener(new FocusListener(){
			@Override public void focusGained(FocusEvent e) { 	_shoutsList.setEditable(false); }
			@Override public void focusLost(FocusEvent e) {		_shoutsList.setEditable(true); }});
	}

	private void initShoutReceiver() {
		_refToAvoidGc = _wind.shoutsHeard().addReceiver(new Consumer<CollectionChange<Shout>>() { @Override public void consume(CollectionChange<Shout> shout) {
			shoutAlert(shout.elementsAdded());
		}});
	}
	
	private void shoutAlert(Collection<Shout> shouts) {
		Window window = SwingUtilities.windowForComponent(_container);
		boolean windowActive = window.isActive();
		if(windowActive) return;
		
		alertUser(shouts);
	}

	private synchronized void alertUser(Collection<Shout> shouts) {

		String shoutsAsString = shoutsAsString(shouts);
		my(TrayIcons.class).messageBalloon("New shouts heard", shoutsAsString.toString());
		//		_player.play(this.getClass().getResource("alert.wav"));
	}

	private String shoutsAsString(Collection<Shout> shouts) {
		StringBuilder shoutsAsString = new StringBuilder();
		for (Shout shout : shouts){
			
			if (shoutsAsString.length() > 0){
				shoutsAsString.append("\n");
			}
			
			Seal publisher = shout.publisher;
			shoutsAsString.append(nicknameOf(publisher) + " - " + shout.phrase);
		}
		return shoutsAsString.toString();
	}

	private String nicknameOf(Seal publisher) {
		return my(ContactSeals.class).nicknameGiven(publisher).currentValue();
	}

	@Override
	public int defaultHeight() {
		return 248;
	}

	@Override
	public String title() {
		return "Wind";
	}
	
	private final class WindClipboardSupport implements ClipboardOwner{
		
		private WindClipboardSupport(){
			addKeyStrokeListener();
		}

		private void addKeyStrokeListener() {
			int modifiers = getPortableSoModifiers();
			final KeyStroke ctrlc = KeyStroke.getKeyStroke(KeyEvent.VK_C, modifiers);
			_shoutsList.getInputMap().put(ctrlc,  "ctrlc");
			_shoutsList.getActionMap().put("ctrlc",  new AbstractAction(){@Override public void actionPerformed(ActionEvent e) {
				copySelectedShoutToClipboard();
			}});
		}
		
		@Override
		public void lostOwnership(Clipboard arg0, Transferable arg1) {
			my(Logger.class).log("Lost Clipboard Ownership.");
		}
		
		private int getPortableSoModifiers() {
			return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		}
		
		private void copySelectedShoutToClipboard() {
			StringSelection fieldContent = new StringSelection(_shoutsList.getSelectedText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(fieldContent, this);	
		}
	}

}
