import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Right click menu
 *
 */
public class SuperMenu extends JPopupMenu {
	private JMenuItem[] enumSelections;
	
	public SuperMenu(ActionListener l){
		enumSelections = new JMenuItem[SuperEnum.values().length];
		for(int i = 0 ;i<enumSelections.length; i++){
			enumSelections[i] = new JMenuItem(SuperEnum.values()[i].name);
			enumSelections[i].addActionListener(l);
			add(enumSelections[i]);
		}
	}
}
