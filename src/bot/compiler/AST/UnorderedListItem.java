package bot.compiler.AST;

import bot.compiler.Visitor;

/**
 * Class represent one item of unordered list.
 * 
 * @author Mir4ik
 * @version 0.1 21.11.2014
 */
public class UnorderedListItem extends AbstractContentHolder {

	public UnorderedListItem(Content[] content) {
		super(content);
	}
	
	@Override
	public String toString() {
		return "* " + super.toString();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitUnorderedListItem(this);
	}

	@Override
	public CharSequence getWikiSource() {
		return toString();
	}
}