package sneer.bricks.pulp.natures.gui;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.Nature;

/** Bricks with this nature can freely manipulate Swing components without
 * having to worry about being in the GUI thread (AWT event dispatch thread).
 * 
 * The container does not allow GUI Bricks to call methods that throw Hiccup.
 * 
 * All methods of all classes in a brick annotated with @Brick(GUI.class) will be
 * intercepted and directed to {@link GuiThread#invokeAndWaitForWussies(Runnable)} for execution.
 */

@Brick
public interface GUI extends Nature {

}