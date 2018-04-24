package net.bgx.bgxnetwork.bgxop.graph;
import java.awt.Font;
import java.awt.FontMetrics;
/**
 * Class NameParser
 * 
 * @author travmik 
 * @version 1.0
 */
public class NameParser {
	public static final int PAGE_LINES = 1;
	protected char[] buffer;
	protected int[] lineBreaks;
	protected int maxLine;
	public static final int H_GAP = 1;
	public NameParser(String text, int maxWidth, FontMetrics metrix) {
		setText(text, maxWidth, metrix);
	}
	protected void addLine(int startPos, int endPos) {
		if (maxLine * 2 >= lineBreaks.length) {
			int temp[] = new int[lineBreaks.length * 2 + 2];
			System.arraycopy(lineBreaks, 0, temp, 0, lineBreaks.length);
			lineBreaks = temp;
		}
		lineBreaks[maxLine * 2] = startPos;
		lineBreaks[maxLine * 2 + 1] = endPos;
		maxLine++;
	}
	public void setText(String _text, int _maxWidth, FontMetrics metrix) {
		clear();
		if (_text == null || metrix == null || _maxWidth <= 0)
			return;
		int start = 0, end = 0, lastSpace = -1;
		int curWidth = H_GAP;
		char ch;
		final int length = _text.length();
		reset(_text);
		for (int pos = 0; pos < length; pos++) {
			ch = buffer[pos];
			if (ch == '\n') {
				addLine(start, pos);
				curWidth = H_GAP;
				buffer[pos] = ' ';
				lastSpace = start = pos + 1;
				continue;
			}
			if (ch == ' ') {
				lastSpace = pos;
				if (start == pos) {
					start++;
					continue;
				}
			}
			curWidth += metrix.charWidth(ch);
			if (curWidth >= _maxWidth - H_GAP) {
				end = (lastSpace > start) ? lastSpace + 1 : pos;
				addLine(start, end);
				curWidth = H_GAP;
				lastSpace = start = end;
				pos = end - 1;
			}
		}
		addLine(start, length);
	}
	public int getNumLines() {
		return maxLine;
	}
	public String getLine(int num) {
		if (num >= maxLine)
			return null;
		int offset = lineBreaks[num * 2];
		int strlen = lineBreaks[num * 2 + 1] - offset;
		return new String(buffer, offset, strlen);
	}
	public void clear() {
		buffer = null;
		lineBreaks = null;
		maxLine = 0;
	}
	private void reset(String text) {
		buffer = text.toCharArray();
		lineBreaks = new int[PAGE_LINES * 2];
		maxLine = 0;
	}
}
