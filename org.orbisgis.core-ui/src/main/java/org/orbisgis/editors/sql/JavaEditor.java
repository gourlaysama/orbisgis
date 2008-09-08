package org.orbisgis.editors.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.orbisgis.PersistenceException;
import org.orbisgis.editor.IEditor;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.GeocognitionElementListener;
import org.orbisgis.geocognition.sql.Code;
import org.orbisgis.geocognition.sql.CodeListener;
import org.orbisgis.ui.text.UndoableDocument;
import org.orbisgis.views.javaConsole.CompletionKeyListener;
import org.orbisgis.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.views.sqlConsole.ui.ConsolePanel;

public abstract class JavaEditor implements IEditor {

	private GeocognitionElement element;
	private Code code;
	private ConsolePanel consolePanel;
	private CodeListener codeListener;
	private SaveListener saveListener = new SaveListener();
	private MarkNavigator errorNavigator;

	public JavaEditor() {
		codeListener = new CodeModificationListener();
	}

	@Override
	public GeocognitionElement getElement() {
		return element;
	}

	@Override
	public String getTitle() {
		return element.getId();
	}

	@Override
	public void setElement(GeocognitionElement element) {
		this.element = element;
		this.element.addElementListener(saveListener);
		this.code = (Code) element.getObject();
		this.code.addCodeListener(codeListener);
		markErrors();
	}

	@Override
	public void delete() {
		this.code.removeCodeListener(codeListener);
		this.element.removeElementListener(saveListener);
	}

	@Override
	public Component getComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		consolePanel = new ConsolePanel(false, new ConsoleListener() {

			public boolean showControlButtons() {
				return false;
			}

			public void save(String text) throws IOException {

			}

			public String open() throws IOException {
				return null;
			}

			public void execute(String text) {
			}

			public void change() {
				if (!consolePanel.getText().equals(
						JavaEditor.this.code.getCode())) {
					JavaEditor.this.code.setCode(consolePanel.getText());
				}
			}

		});
		consolePanel.setText(code.getCode());
		JTextComponent textComponent = consolePanel.getTextComponent();
		((UndoableDocument) textComponent.getDocument()).resetUndoEdits();
		textComponent.addKeyListener(new CompletionKeyListener(false,
				textComponent));
		panel.add(consolePanel, BorderLayout.CENTER);
		errorNavigator = new MarkNavigator();
		errorNavigator.setPreferredSize(new Dimension(10, 100));
		markErrors();
		panel.add(errorNavigator, BorderLayout.EAST);

		return panel;
	}

	@Override
	public void initialize() {

	}

	@Override
	public void loadStatus() throws PersistenceException {

	}

	@Override
	public void saveStatus() throws PersistenceException {

	}

	private class CodeModificationListener implements CodeListener {

		@Override
		public void codeChanged(Code code) {
			JTextComponent txt = consolePanel.getTextComponent();
			if (!txt.getText().equals(code.getCode())) {
				int caretPosition = txt.getCaretPosition();
				txt.setText(code.getCode());
				if (caretPosition < code.getCode().length()) {
					txt.setCaretPosition(caretPosition);
				}
			}
		}

	}

	private void markErrors() {
		if (errorNavigator != null) {
			errorNavigator.setTotalLines(code.getLineCount());
			errorNavigator.setErrorLines(code.getErrorLines());
		}
	}

	private class SaveListener implements GeocognitionElementListener {

		@Override
		public void contentChanged(GeocognitionElement element) {
		}

		@Override
		public void idChanged(GeocognitionElement element) {
		}

		@Override
		public void saved(GeocognitionElement element) {
			markErrors();
		}

	}

}