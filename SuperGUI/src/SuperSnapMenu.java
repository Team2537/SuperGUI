import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class SuperSnapMenu extends JPopupMenu {
	private JMenuItem[] snapSelections;
	
	public SuperSnapMenu(ActionListener l){
		snapSelections = new JMenuItem[SuperSnapEnum.values().length];
		for(int i = 0 ;i<snapSelections.length; i++){
			snapSelections[i] = new JMenuItem(SuperSnapEnum.values()[i].name);
			snapSelections[i].addActionListener(l);
			add(snapSelections[i]);
		}
	}
}
