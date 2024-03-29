/*
 * Command.java	25.01.2015
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
package intelligent.wiki.editor.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import intelligent.wiki.editor.gui.fx.WikiEditor;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class represents abstract command. To write concrete commands, make
 * subclasses of these class and place them in these file.
 * 
 * @author Myroslav Rudnytskyi
 * @version 0.1 25.01.2015
 */
// TODO: add i18n
public abstract class Command {
	
	/**
	 * @return command name, used to call it in command-line interface
	 */
	public abstract String getName();
	
	/**
	 * Method for making command actions. 
	 * Note, that it should not used directly! 
	 */
	protected abstract void act();

	/**
	 * Method used to execute command.
	 */
	public void doAction() {
		act();
		clear();
	}
	
	/**
	 * @return array, containing {@link String}s for displaying help message
	 */
	public abstract String[] getHelp();
	
	/**
	 * Method for rolling back command object to initial state. 
	 * Note, that it should not used directly! 
	 */
	protected void clear() {}
	
	/**
	 * @return true if it is exit command, false - otherwise
	 */
	public boolean isExit() {
		return false;
	}
	
	@Parameters
	public static class ExitCommand extends Command {

		@Override
		public String getName() {
			return "exit";
		}
		
		@Override
		protected void act() {
			System.out.println("Thanks for using! Goodbye!");
			System.exit(0);
		}

		@Override
		public String[] getHelp() {
			return new String[] {getName(), "stops the application", "exit",
				"Command immediately stops the application. No options are "
				+ "available.", "> exit", "None"
			};
		}
		
		@Override
		public boolean isExit() {
			return true;
		}
	}
	
	@Parameters
	public static class HelpCommand extends Command {
		
		@Parameter
		private List<String> args = new ArrayList<>();
		
		@Parameter(names = "-i")
		private boolean info;
		
		@Parameter(names = "-a")
		private boolean available;
		
		private static String makePretty(String... args) {
			final int STRING_WIDTH = 70;
			Object[] changedArgs = new Object[args.length];
			int k = 0;
			for (String s : args) {
				if (s.length() <= STRING_WIDTH) {
					changedArgs[k] = s;
				} else {
					int dividerPos = STRING_WIDTH;
					List<Integer> insertPositions = new ArrayList<>();
					while (true) {
						if (s.length() <= dividerPos) {
							break;
						} else {
							while (s.charAt(dividerPos) != ' ') {
								dividerPos--;
							}
							insertPositions.add(dividerPos);
							dividerPos += STRING_WIDTH;
						}
					}
					StringBuilder sb = new StringBuilder(s);
					int insertionOffset = 0;
					for (Integer i : insertPositions) {
						sb.insert(i + insertionOffset + 1, "\n\t");
						insertionOffset += 2;
					}
					changedArgs[k] = sb.toString();
				}
				k++;
			}
			final String helpTemplate = "NAME\n\t%s -- %s\nUSAGE\n\t%s\n" + 
					"DESCRIPTION\n\t%s\nEXAMPLES\n\t%s\nSEE ALSO\n\t%s";
			return String.format(helpTemplate, changedArgs);
		}

		@Override
		public String getName() {
			return "help";
		}
		
		@Override
		protected void act() {
			if (info || args.isEmpty()) {
				System.out.println("This is second version Wikipedia editor.");
			}
			if (available) {
				System.out.print("Now you can use: ");
				System.out.println(Arrays.deepToString(
						CommandsManager.getAvailable()));
			}
			for (String commandName : args) {
				Command currCommand = CommandsManager.getCommand(commandName);
				if (currCommand == null) {
					System.err.println("Can not find command \"" + 
							commandName + "\"!");
					continue;
				}
				System.out.println(makePretty(currCommand.getHelp()));
			}
		}
		
		@Override
		protected void clear() {
			args.clear();
			info = false;
			available = false;
		}

		@Override
		public String[] getHelp() {
			return new String[] {getName(), "displays documentation pages",
				"help [-a] [-i] <command name> ... <command name>",
				"Outputs information about every argument. Can be used without"
				+ " any, or with \"-i\" parameter, then displays general info "
				+ "about software. When multiply arguments used, it will show "
				+ "information about every command, named in parameters. Also "
				+ "it write warnings, when some arguments are unknown. When "
				+ "parameter \"-a\" is used, command displays list of all "
				+ "available.", "> help\n\t> help -a help exit", "None"};
		}	
	}
	
	@Parameters
	public static class GUICommand extends Command {

		@Override
		public String getName() {
			return "gui";
		}

		@Override
		protected void act() {
			Application.launch(WikiEditor.class);
			new ExitCommand().act();
		}

		@Override
		public String[] getHelp() {
			return new String[] {getName(), "opens graphical user interface",
					"gui", "Opens main frame of wiki editor. No options for "
							+ "this command are available.", "> gui", "None"};
		}
	}
}