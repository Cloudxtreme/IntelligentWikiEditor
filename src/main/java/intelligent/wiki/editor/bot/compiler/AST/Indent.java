package intelligent.wiki.editor.bot.compiler.AST;
/*
 * Indent.java	19.11.2014
 * Copyright (C) 2014 Myroslav Rudnytskyi
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

import intelligent.wiki.editor.bot.compiler.Visitor;

/**
 * Class represents AST node with indent.
 * 
 * @author Myroslav Rudnytskyi
 * @version 0.1 19.11.2014
 */
public class Indent extends AbstractContentHolder {

	public Indent(Content[] content) {
		super(content);
	}
	
	@Override
	public String toString() {
		return ": " + super.toString();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitIndent(this);
	}

	@Override
	public CharSequence getWikiSource() {
		return toString();
	}
}