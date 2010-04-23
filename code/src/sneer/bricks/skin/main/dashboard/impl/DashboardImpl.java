package sneer.bricks.skin.main.dashboard.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.gui.timebox.TimeboxedEventQueue;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.skin.main.dashboard.Dashboard;
import sneer.bricks.skin.main.instrumentregistry.Instrument;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.main.synth.Synth;
import sneer.bricks.skin.main.synth.scroll.SynthScrolls;
import sneer.bricks.skin.main.title.ProcessTitle;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.Widget;
import sneer.bricks.skin.windowboundssetter.WindowBoundsSetter;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

class DashboardImpl implements Dashboard {

	private final Synth _synth = my(Synth.class);
	{ _synth.notInGuiThreadLoad(this.getClass()); }
	
	private final int WIDTH = (Integer) synthValue("Dashboard.WIDTH");
	private final int OFFSET = (Integer) synthValue("Dashboard.OFFSET");
	private final int HORIZONTAL_MARGIN = (Integer) synthValue("Dashboard.HORIZONTAL_MARGIN");  
	private final int TIMEOUT_FOR_GUI_EVENTS = (Integer) synthValue("Dashboard.TIMEOUT_FOR_GUI_EVENTS");
	
	private final  MainMenu _mainMenu = my(MainMenu.class);
	private final JScrollBar _scrollBar;{ 
		my(SynthScrolls.class);
		_scrollBar = new JScrollBar(Adjustable.VERTICAL){
			@Override public void setBounds(int x, int y, int width, int height) {
				super.setBounds(x, y, 10, height);
			}
			@Override
			public Dimension getPreferredSize() {
				Dimension size =  super.getPreferredSize().getSize();
				size.setSize(10, size.height);
				return size;			}
			
			@Override public Dimension getMaximumSize() {
				Dimension size =  super.getMaximumSize().getSize();
				size.setSize(10, size.height);
				return size;
			}
			
			@Override public Dimension getSize() {
				Dimension size =  super.getSize();
				size.setSize(10, size.height);
				return size;
			}
		};
		my(Synth.class).notInGuiThreadAttach(_scrollBar, "DashboardScrollBar");
	}
	
	private final DashboardPanel _dashboardPanel = new DashboardPanel(_scrollBar);
	private final JPanel _rootPanel = new JPanel();

	private Dimension _screenSize;
	private Rectangle _bounds;
	
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;

	
	{
		_refToAvoidGc = receiveInstruments();

		initGuiTimebox();
		initGui();
	}


	private WeakContract receiveInstruments() {
		return my(InstrumentRegistry.class).installedInstruments().addReceiver(new Consumer<CollectionChange<Instrument>>(){ @Override public void consume(CollectionChange<Instrument> change) {
			for (Instrument instrument : change.elementsAdded())
				_dashboardPanel.install(instrument);
			
			if (!change.elementsRemoved().isEmpty())
				throw new sneer.foundation.lang.exceptions.NotImplementedYet();
		}});
	}
	
	
	private <T>  T synthValue(String key){
		return (T)_synth.getDefaultProperty(key);
	}
	
	
	private void initGuiTimebox() {
		my(TimeboxedEventQueue.class).startQueueing(TIMEOUT_FOR_GUI_EVENTS);
	}
	
	
	private void initGui() {
		my(GuiThread.class).invokeLater(new Closure() { @Override public void run() {
			WindowSupport windowSupport = new WindowSupport();
			windowSupport.open();
			new TrayIconSupport(windowSupport);
		}});
	}
	
	
   class WindowSupport{
		private Widget<JFrame> _rwindow;
		private JFrame _frame;

		WindowSupport() {
			initWindow();
			initSynth();
			initRootPanel();	
			resizeWindow();
		}
		
		private void initSynth() {
			Container contentPane = _frame.getContentPane();
			_synth.attach((JPanel)contentPane, "DashboarContentPane");
			
//			JComponent menu = _mainMenu.getWidget(); Fix: Add Layout to Menu.
//			menu.setName("DashboarMenuBar");
//			_synth.attach(menu);
		}

		private void initWindow() {
			_rwindow = my(ReactiveWidgetFactory.class).newFrame(reactiveTitle());
			_frame = _rwindow.getMainWidget();
			_frame.setIconImage(IconUtil.getLogo());
			
			_frame.addWindowListener(new WindowAdapter() { @Override public void windowDeactivated(WindowEvent e) {
				_dashboardPanel.hideAllToolbars();
			}});
			
		}

		private void initRootPanel() {
			_rootPanel.setLayout(new BorderLayout());
			_rootPanel.add(_mainMenu.getMenuBarWidget(), BorderLayout.NORTH);
			_rootPanel.add(_dashboardPanel, BorderLayout.CENTER);
			_rootPanel.add(_scrollBar, BorderLayout.EAST);
			
			addListenerToHideToolbarsWhenMouseExited();
			
//			RunMe.logTree(_dashboardPanel);
		}

		private void addListenerToHideToolbarsWhenMouseExited() {
			//Fix: this method is a hack, consider to use a glasspane mouse listener
			_frame.setContentPane(_rootPanel);
			
			Insets insets = new Insets(HORIZONTAL_MARGIN, 0 , HORIZONTAL_MARGIN, 0);
			_rootPanel.setBorder(new EmptyBorder(insets));
			_rootPanel.addMouseListener(new MouseAdapter(){ @Override public void mouseEntered(MouseEvent e) {
				_dashboardPanel.hideAllToolbars();
			}});
		}	
		
		private void resizeWindow() {
			Dimension newSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			if(_bounds==null || _screenSize==null || !_screenSize.equals(newSize)){
				_screenSize  = newSize;
				_bounds = new Rectangle((int) _screenSize.getWidth() - WIDTH, 0, WIDTH,	
									   				  (int) _screenSize.getHeight() - OFFSET);
			}
			_rwindow.getMainWidget().setBounds(_bounds);
		}

		void changeWindowCloseEventToMinimizeEvent() {
			_frame.setDefaultCloseOperation ( WindowConstants.DO_NOTHING_ON_CLOSE );
			_frame.addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) {
				_bounds = _frame.getBounds();
				_frame.setState(Frame.ICONIFIED);
			}});
		}		
		
		void open() {
			_frame.setState(Frame.NORMAL);
			_frame.setVisible(true);
			_frame.requestFocusInWindow();
			my(WindowBoundsSetter.class).setDefaultBaseComponent(_rootPanel);
		}

		private Signal<String> reactiveTitle() {
			return my(ProcessTitle.class).title();
		}
	}
}