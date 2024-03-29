/*
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

package intelligent.wiki.editor.bot.compiler.AST;

import intelligent.wiki.editor.bot.compiler.Visitor;
import intelligent.wiki.editor.bot.io.wiki.templatedata.TemplateParameter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class, representing template argument: {@link TemplateParameter template parameter}
 * with some value, divided using '=' (equals) sign.
 *
 * @author Myroslav Rudnytskyi
 * @version 23.10.2015
 */
public class TemplateArgument implements TemplateParameter, Content {
	private final TemplateParameter parameter;
	private final StringProperty value = new SimpleStringProperty();
	private final String name;

	public TemplateArgument(TemplateParameter parameter) {
		this.parameter = parameter;
		name = null;
	}

	public TemplateArgument(String name, String value) {
		this.name = name;
		setValue(value);
		parameter = null;
	}

	public TemplateArgument(String name) {
		this(name, "");
	}

	@Override
	public String getName() {
		return isParameterProvided() ? parameter.getName() : name;
	}

	@Override
	public String getDescription() {
		return isParameterProvided() ? parameter.getDescription() : "";
	}

	@Override
	public String getDefault() {
		return isParameterProvided() ? parameter.getDefault() : "";
	}

	@Override
	public TemplateParameterType getType() {
		return isParameterProvided() ? parameter.getType() : TemplateParameterType.UNKNOWN;
	}

	@Override
	public boolean isRequired() {
		return isParameterProvided() && parameter.isRequired();
	}

	@Override
	public boolean isSuggested() {
		return isParameterProvided() && parameter.isSuggested();
	}

	@Override
	public boolean isDeprecated() {
		return isParameterProvided() && parameter.isDeprecated();
	}

	public String getValue() {
		return value.get();
	}

	public void setValue(String value) {
		this.value.set(value);
	}

	public StringProperty valueProperty() {
		return value;
	}

	@Override
	public String toString() {
		return getName() + " = " + (isValueProvided() ? value.get() : getDefault());
	}

	private boolean isValueProvided() {
		return value.get() != null;
	}

	private boolean isParameterProvided() {
		return parameter != null;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitTemplateArgument(this);
	}
}
