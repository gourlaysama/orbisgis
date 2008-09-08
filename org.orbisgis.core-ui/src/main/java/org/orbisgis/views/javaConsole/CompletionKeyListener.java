/**
 * 
 */
package org.orbisgis.views.javaConsole;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.JTextComponent;

import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.javaManager.autocompletion.Completion;
import org.orbisgis.javaManager.autocompletion.Option;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.orbisgis.windows.mainFrame.UIManager;
import org.sif.UIFactory;

public class CompletionKeyListener extends KeyAdapter {
	private final boolean script;
	private JTextComponent txt;
	private CompletionPopUp pop;
	private Completion completion;

	public CompletionKeyListener(boolean script, JTextComponent txt) {
		this.txt = txt;
		this.script = script;
		try {
			completion = new Completion();
		} catch (LinkageError e) {
			Services.getService(ErrorManager.class).error(
					"Completion system cannot be initialized", e);
		}
	}

	public static void main(String[] args) throws Exception {
		JEditorPane textComponent = new JEditorPane();
		textComponent.setPreferredSize(new Dimension(400, 400));
		textComponent.addKeyListener(new CompletionKeyListener(false,
				textComponent));
		final JFrame frm = new JFrame();
		frm.getContentPane().add(textComponent, BorderLayout.CENTER);
		frm.pack();
		frm.setLocationRelativeTo(null);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);
		Services.registerService("org.orbisgis.UIManager", UIManager.class, "",
				new UIManager() {

					@Override
					public void refreshUI() {

					}

					@Override
					public JFrame getMainFrame() {
						return frm;
					}

				});
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (completion == null) {
		}

		String originalText = txt.getText();
		if ((e.getKeyCode() == KeyEvent.VK_SPACE) && e.isControlDown()) {
			Point p = txt.getCaret().getMagicCaretPosition();
			Option[] list = completion.getOptions(originalText, txt
					.getCaretPosition(), script);
			showList(list, p);
		} else if ((e.getKeyCode() == KeyEvent.VK_S) && e.isControlDown()
				&& e.isShiftDown()) {
			try {
				SaveFilePanel sfp = new SaveFilePanel(null,
						"Save code completion test case");
				sfp.setCurrentDirectory(new File("/home/fergonco/"
						+ "ogworkspace/geocognition/orbisgis-core/"
						+ "src/test/resources/" + "org/orbisgis/javaManager/"));
				sfp.addFilter("compl", "completion file");
				if (UIFactory.showDialog(sfp)) {
					Option[] list = completion.getOptions(originalText, txt
							.getCaretPosition(), script);
					DataOutputStream dos = new DataOutputStream(
							new FileOutputStream(sfp.getSelectedFile()));
					StringBuffer sb = new StringBuffer();
					sb.append(txt.getCaretPosition());
					for (Option option : list) {
						sb.append(";").append(option.getAsString());
					}
					dos.write(sb.append("\n").toString().getBytes());
					String content = originalText;
					dos.write(content.getBytes());
					dos.close();
				}
			} catch (IOException e1) {
				Services.getErrorManager().error(
						"Cannot save code completion test case", e1);
			}
		}
	}

	private void showList(final Option[] list, Point p) {
		if (list.length > 0) {
			UIManager ui = Services.getService(UIManager.class);
			JFrame mainFrame = ui.getMainFrame();
			pop = new CompletionPopUp(txt, list);
			pop.pack();

			// Place the pop up inside the frame so that it's a lightweight
			// component
			Point txtPoint = txt.getLocationOnScreen();
			Point frmPoint = mainFrame.getLocationOnScreen();
			int x1 = (txtPoint.x - frmPoint.x) + p.x;
			int y1 = (txtPoint.y - frmPoint.y) + p.y + 15;
			Dimension popSize = pop.getPreferredSize();
			int popWidth = popSize.width;
			int popHeight = popSize.height;
			if (txtPoint.y + p.y + popHeight + 15 > frmPoint.y
					+ mainFrame.getHeight()) {
				y1 = y1 - popHeight - 15;
			}
			if (txtPoint.x + p.x + popWidth > frmPoint.x + mainFrame.getWidth()) {
				x1 = x1 - popWidth;
			}
			// if (x1 < frmPoint.x) {
			// x1 = 0;
			// Dimension newDimension = new Dimension(
			// 3 * mainFrame.getWidth() / 4, popHeight);
			// pop.setPreferredSize(newDimension);
			// }
			// if (y1 < frmPoint.y) {
			// y1 = 0;
			// Dimension newDimension = new Dimension(popWidth, 3 * mainFrame
			// .getHeight() / 4);
			// pop.setPreferredSize(newDimension);
			// }
			//
			pop.show(mainFrame, x1, y1);
		}
	}
}