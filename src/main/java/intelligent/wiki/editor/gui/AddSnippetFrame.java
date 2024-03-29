package intelligent.wiki.editor.gui;
/*
 * AddSnippetFrame.java	19.04.2015
 * Copyright (C) 2015 Myroslav Rudnytskyi
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

import intelligent.wiki.editor.bot.nlp.Snippet;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Frame for adding {@link Snippet text fragments} to application.
 * 
 * @author Myroslav Rudnytskyi
 * @version 0.1 19.04.2015
 */
// TODO: database loading?
@Deprecated
public class AddSnippetFrame extends ApplicationFrame {

	private static final long serialVersionUID = 5148754148151526106L;

	private final JTextArea text = new JTextArea("Enter snippet here");

	private final JTextField source = new JTextField("Enter source here");

	private final JList<String> tags;

	/**
	 * Constructs new frame with specified content and title.
	 * 
	 * @param listener
	 *            necessary object, which will receive {@link Snippet} object,
	 *            listening for changing <code>add-snippet</code> property
	 * @param tags
	 *            array of available tags names to categorize snippet
	 */
	public AddSnippetFrame(PropertyChangeListener listener, String[] tags) {
		super("Add snippet");
		this.tags = new JList<String>(tags);
		setLayout(new BorderLayout());
		add(createContent(), BorderLayout.CENTER);
		addPropertyChangeListener(listener);
		setResizable(false);
		setAlwaysOnTop(true);
		pack();
		moveToScreenCenter();
	}

	/**
	 * Returns array, initialized by actions objects.
	 */
	@Override
	protected AbstractAction[] createActions() {
		List<AbstractAction> actions = new ArrayList<AbstractAction>();
		actions.add(new Action(this, "Cancel", "add-snippet-cancel",
				"Cancel all changes", "", "src/main/resources/images/cancel_big.png", 0));
		actions.add(new Action(this, "OK", "add-snippet-OK",
				"Apply all changes", "", "src/main/resources/images/ok_big.png", 0));
		return actions.toArray(new AbstractAction[actions.size()]);
	}

	private JPanel createContent() {
		JPanel content = new JPanel(new BorderLayout());

		JPanel topPanel = new JPanel();
		JScrollPane text = new JScrollPane(this.text);
		text.setBorder(BorderFactory.createTitledBorder("Snippet"));
		JScrollPane src = new JScrollPane(this.source);
		src.setBorder(BorderFactory.createTitledBorder("Source"));
		JScrollPane tags = new JScrollPane(this.tags);
		tags.setBorder(BorderFactory.createTitledBorder("Tags"));

		GroupLayout group = new GroupLayout(topPanel);
		int WIDTH = 300;
		int HEIGHT = 200;
		int DEF = GroupLayout.DEFAULT_SIZE;
		int PREF = GroupLayout.PREFERRED_SIZE;
		group.setHorizontalGroup(group.createParallelGroup(Alignment.LEADING)
				.addGroup(
						group.createSequentialGroup().addGroup(
								group.createParallelGroup(Alignment.LEADING)
										.addComponent(tags, DEF, WIDTH,
												Short.MAX_VALUE)
										.addComponent(text, DEF, WIDTH,
												Short.MAX_VALUE)
										.addComponent(src, DEF, WIDTH,
												Short.MAX_VALUE))));
		group.setVerticalGroup(group.createParallelGroup(Alignment.LEADING)
				.addGroup(
						group.createSequentialGroup()
								.addComponent(text, PREF, HEIGHT, PREF)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(src, PREF, DEF, PREF)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(tags, PREF, HEIGHT, PREF)));
		topPanel.setLayout(group);

		JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 50, 0));
		bottomPanel.add(new JButton(getAction("add-snippet-OK")));
		bottomPanel.add(new JButton(getAction("add-snippet-cancel")));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

		content.add(topPanel, BorderLayout.CENTER);
		content.add(bottomPanel, BorderLayout.SOUTH);
		content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		return content;
	}

	/**
	 * Method provides code for frame actions.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case "add-snippet-cancel":
			setVisible(false);
			break;
		case "add-snippet-OK":
			setVisible(false);
			String[] topicArray = tags.getSelectedValuesList().toArray(
					new String[tags.getSelectedIndices().length]);
			firePropertyChange("add-snippet", null, new Snippet(text.getText(),
					source.getText(), topicArray));
			break;
		}
	}
}