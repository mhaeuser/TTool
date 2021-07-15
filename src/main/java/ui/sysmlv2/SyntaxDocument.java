/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package ui.sysmlv2;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import myutil.*;
import ui.*;


/**
 * Class SyntaxDocument
 * Sysmlv2 edition
 * Creation: 15/07/2021
 * @version 1.0 15/07/2021
 * @author Ludovic APVRILLE
 */
class SyntaxDocument extends DefaultStyledDocument {
	
	private static Color ENSTRed = new Color(221, 0, 53); 
	private static Color Orange = new Color(217, 131, 19); 
	private static Color Brown = new Color(153, 145, 74); 
	private static Color Rose = new Color(241, 8, 145); 
	private static Color Green = new Color(43, 125, 15);
	private static Color ColorComment = new Color(156, 0, 255);
	private static Color ColorText = Color.black;
	private static Color Color0 = Color.blue;
	private static Color Color1 = Green;
	private static Color Color2 = Color.orange;
	private static Color Color3 = ENSTRed;
	private static Color ColorFind = Color.magenta;
	
	
	private static Color ColorMispell = new Color(0, 255, 124);
	
	private DefaultStyledDocument doc;
	private Element rootElement;
	
	private boolean multiLineComment;
	private MutableAttributeSet normal;
	
	private MutableAttributeSet comment;
	private MutableAttributeSet quote;
	private MutableAttributeSet mispell;
	private MutableAttributeSet masFind;
	
	private final static int KEYWORD_CLASS = 5;
	private HashSet[] keywords;
	private MutableAttributeSet[] keyword;
	private boolean checkForSpelling = true;
	
	private String find;
	private int findFound = 0;
	private boolean matchCase = false;
	
	private  int tabSize = 2;

	@SuppressWarnings("unchecked")
	public SyntaxDocument() {

		
		doc = this;
		rootElement = doc.getDefaultRootElement();
		putProperty( DefaultEditorKit.EndOfLineStringProperty, "\n" );
		
		normal = new SimpleAttributeSet();
		StyleConstants.setForeground(normal, ColorText);
		
		comment = new SimpleAttributeSet();
		StyleConstants.setForeground(comment, ColorComment);
		StyleConstants.setItalic(comment, true);
		
		quote = new SimpleAttributeSet();
		StyleConstants.setForeground(quote, Brown);
		
		mispell = new SimpleAttributeSet();
		//StyleConstants.setForeground(mispell, ColorMispell);
		StyleConstants.setUnderline(mispell, true);
		
		masFind = new SimpleAttributeSet();
		StyleConstants.setBackground(masFind, ColorFind);
		
		// Keywords
		keywords = new HashSet[KEYWORD_CLASS];
		keyword = new MutableAttributeSet[KEYWORD_CLASS];
		for(int i=0; i<KEYWORD_CLASS; i++) {
			keywords[i] = new HashSet();
			keyword[i] = new SimpleAttributeSet();
		}
		StyleConstants.setForeground(keyword[0], Color0);
		StyleConstants.setForeground(keyword[1], Color1);    
		StyleConstants.setForeground(keyword[2], Color2);   
		StyleConstants.setForeground(keyword[3], Color3);

		// main keywords
		keywords[0].add( "package");
		keywords[0].add( "language");
		keywords[0].add( "import");
		keywords[0].add( "metadata");
		keywords[0].add( "viewpoint");


		// elements of diagrams
		keywords[1].add( "stakeholder");
		keywords[1].add( "concern");
		keywords[1].add( "part");
		keywords[1].add( "def");
		keywords[1].add( "enum");
		keywords[1].add( "item");
		keywords[1].add( "connection");
		keywords[1].add( "port");
		keywords[1].add( "interface");
		keywords[1].add( "action");
		keywords[1].add( "succession");
		keywords[1].add( "transition");
		keywords[1].add( "state");
		keywords[1].add( "exhibit");
		keywords[1].add( "attribute");
		keywords[1].add( "individual");
		keywords[1].add( "calc");
		keywords[1].add( "constraint");
		keywords[1].add( "assert");
		keywords[1].add( "objective");
		keywords[1].add( "assume");
		keywords[1].add( "require");
		keywords[1].add( "requirement");
		keywords[1].add( "subject");
		keywords[1].add( "analysis");
		keywords[1].add( "satisfy");
		keywords[1].add( "verification");
		keywords[1].add( "perform");
		keywords[1].add( "variation");
		keywords[3].add( "allocate");
		keywords[3].add( "frame");

		// doc & comments
		keywords[2].add( "doc");
		keywords[2].add( "comment");

		// Secondary keywords
		keywords[3].add( "dependency");
		keywords[3].add( "private");
		keywords[3].add( "public");
		keywords[3].add( "alias");
		keywords[3].add( "as");
		keywords[3].add( "abstract");
		keywords[3].add( "redefines");
		keywords[3].add( "specializes");
		keywords[3].add( "connect");
		keywords[3].add( "to");
		keywords[3].add( "in");
		keywords[3].add( "out");
		keywords[3].add( "bind");
		keywords[3].add( "flow");
		keywords[3].add( "stream");
		keywords[3].add( "end");
		keywords[3].add( "then");
		keywords[3].add( "merge");
		keywords[3].add( "fork");
		keywords[3].add( "decide");
		keywords[3].add( "perform");
		keywords[3].add( "entry");
		keywords[3].add( "first");
		keywords[3].add( "accept");
		keywords[3].add( "do");
		keywords[3].add( "exit");
		keywords[3].add( "send");
		keywords[2].add( "ordered");
		keywords[2].add( "ref");
		keywords[2].add( "occurrence");
		keywords[2].add( "event");
		keywords[2].add( "message");
		keywords[2].add( "from");
		keywords[2].add( "to");
		keywords[2].add( "return");
		keywords[2].add( "filter");
		keywords[2].add( "affect");
		keywords[2].add( "render");
		keywords[2].add( "expose");

		// Time
		keywords[4].add( "timeslice");
		keywords[4].add( "snapshot");

		
	}
	
	public void setFind(String _find) {
		find  = _find;
	}
	
	public String getFind() {
		return find;
	}
	
	public void setMatchCase(boolean _matchCase) {
		matchCase  = _matchCase;
	}
	
	public boolean getMatchCase() {
		return matchCase;
	}
	
	public int findFound() {
		return findFound;
	}
	
	public void setTabSize(int _tabSize) {
		tabSize = _tabSize;
	}
	
	public MutableAttributeSet getNormalAttributeSet() {
		return normal;
	}
	
	public void setCheckForSpelling(boolean _checkForSpelling) {
		checkForSpelling = _checkForSpelling;
	}

	/*
	*  Override to apply syntax highlighting after the document has been updated
	*/
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		/*if (str.equals("{"))
		str = addMatchingBrace(offset);*/
		
		super.insertString(offset, str, a);
		processChangedLines(offset, str.length());
	}
	
	/*
	*  Override to apply syntax highlighting after the document has been updated
	*/
	public void remove(int offset, int length) throws BadLocationException
	{
		super.remove(offset, length);
		processChangedLines(offset, 0);
	}
	
	public void processAllLinesChanged() throws BadLocationException {
		processChangedLines(0, doc.getLength());
	}
	
	/*
	*  Determine how many lines have been changed,
	*  then apply highlighting to each line
	*/
	public void processChangedLines(int offset, int length) throws BadLocationException {
		findFound = 0;
		//TraceManager.addDev("Setting to 0 nb of occurences");
		String content = doc.getText(0, doc.getLength());
		
		//  The lines affected by the latest document update
		
		int startLine = rootElement.getElementIndex( offset );
		int endLine = rootElement.getElementIndex( offset + length );
		
		//  Make sure all comment lines prior to the start line are commented
		//  and determine if the start line is still in a multi line comment
		
		//setMultiLineComment( commentLinesBefore( content, startLine ) );
		
		//  Do the actual highlighting
		
		for (int i = startLine; i <= endLine; i++)
		{
			applyHighlighting(content, i);
		}
		
		//  Resolve highlighting to the next end multi line delimiter
		
		/*if (isMultiLineComment())
			commentLinesAfter(content, endLine);
		else
			highlightLinesAfter(content, endLine);*/
	}
	
	/*
	*  Highlight lines when a multi line comment is still 'open'
	*  (ie. matching end delimiter has not yet been encountered)
	*/
	/*private boolean commentLinesBefore(String content, int line)
	{
		int offset = rootElement.getElement( line ).getStartOffset();
		
		//  Start of comment not found, nothing to do
		
		//int startDelimiter = lastIndexOf( content, getStartDelimiter(), offset - 2 );
		
		//if (startDelimiter < 0)
		//return false;
		
		//  Matching start/end of comment found, nothing to do
		
		//int endDelimiter = indexOf( content, getEndDelimiter(), startDelimiter );
		
		//if (endDelimiter < offset & endDelimiter != -1)
		//	return false;
		
		//  End of comment not found, highlight the lines
		
		//doc.setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, comment, false);
		return true;
	}*/
	
	/*
	*  Highlight comment lines to matching end delimiter
	*/
	/*private void commentLinesAfter(String content, int line)
	{
		int offset = rootElement.getElement( line ).getEndOffset();
		
		//  End of comment not found, nothing to do
		
		int endDelimiter = indexOf( content, getEndDelimiter(), offset );
		
		if (endDelimiter < 0)
			return;
		
		//  Matching start/end of comment found, comment the lines
		
		int startDelimiter = lastIndexOf( content, getStartDelimiter(), endDelimiter );
		
		if (startDelimiter < 0 || startDelimiter <= offset)
		{
			doc.setCharacterAttributes(offset, endDelimiter - offset + 1, comment, false);
		}
	}*/
	
	/*
	*  Highlight lines to start or end delimiter
	*/
	/*private void highlightLinesAfter(String content, int line) throws BadLocationException {
		int offset = rootElement.getElement( line ).getEndOffset();
		
		//  Start/End delimiter not found, nothing to do
		
		int startDelimiter = indexOf( content, getStartDelimiter(), offset );
		int endDelimiter = indexOf( content, getEndDelimiter(), offset );
		
		if (startDelimiter < 0)
			startDelimiter = content.length();
		
		if (endDelimiter < 0)
			endDelimiter = content.length();
		
		int delimiter = Math.min(startDelimiter, endDelimiter);
		
		if (delimiter < offset)
			return;
		
		//	Start/End delimiter found, reapply highlighting
		
		int endLine = rootElement.getElementIndex( delimiter );
		
		for (int i = line + 1; i < endLine; i++)
		{
			Element branch = rootElement.getElement( i );
			Element leaf = doc.getCharacterElement( branch.getStartOffset() );
			AttributeSet as = leaf.getAttributes();
			
			if ( as.isEqual(comment) )
				applyHighlighting(content, i);
		}
	}*/
	
	private void applyHighlighting(String content, int line) throws BadLocationException
	{
		int startOffset = rootElement.getElement( line ).getStartOffset();
		int endOffset = rootElement.getElement( line ).getEndOffset() - 1;
		
		int lineLength = endOffset - startOffset;
		int contentLength = content.length();
		
		if (endOffset >= contentLength)
			endOffset = contentLength - 1;
		
		//  check for multi line comments
		//  (always set the comment attribute for the entire line)
		
		/*if (endingMultiLineComment(content, startOffset, endOffset)
			||  isMultiLineComment()
		||  startingMultiLineComment(content, startOffset, endOffset) )
		{
			doc.setCharacterAttributes(startOffset, endOffset - startOffset + 1, comment, false);
			return;
		}*/
		
		//  set normal attributes for the line
		
		doc.setCharacterAttributes(startOffset, lineLength, normal, true);
		
		//  check for single line comment
		
		int index = content.indexOf(getSingleLineDelimiter(), startOffset);
		
		
		if ( (index > 0) && (index < endOffset) )
		{
			if (content.charAt(index-1) != '\\') {
				doc.setCharacterAttributes(index, endOffset - index + 1, comment, false);
				endOffset = index - 1;
			}
		}
		
		//  check for tokens
		
		checkForTokens(content, startOffset, endOffset);
		
		// Check for find
		if (isFind()) {
			if (matchCase) {
				while((lineLength>0) && ((index = doc.getText(startOffset, lineLength).indexOf(find))!= -1)) {
					findFound ++;
					doc.setCharacterAttributes(startOffset+index, find.length(), masFind, false);
					startOffset += index + 1;
					lineLength = lineLength - index - 1;
				} 
			} else {
				String flc = find.toLowerCase();
				while((lineLength>0) && ((index = doc.getText(startOffset, lineLength).toLowerCase().indexOf(flc))!= -1)) {
					findFound ++;
					doc.setCharacterAttributes(startOffset+index, find.length(), masFind, false);
					startOffset += index + 1;
					lineLength = lineLength - index - 1;
				} 
			}
		}
		
	}
	
	/*
	*  Does this line contain the start delimiter
	*/
	/*private boolean startingMultiLineComment(String content, int startOffset, int endOffset)
	throws BadLocationException
	{
		int index = indexOf( content, getStartDelimiter(), startOffset );
		
		if ( (index < 0) || (index > endOffset) )
			return false;
		else
		{
			setMultiLineComment( true );
			return true;
		}
	}*/
	
	/*
	*  Does this line contain the end delimiter
	*/
	/*private boolean endingMultiLineComment(String content, int startOffset, int endOffset)
	throws BadLocationException
	{
		int index = indexOf( content, getEndDelimiter(), startOffset );
		
		if ( (index < 0) || (index > endOffset) )
			return false;
		else
		{
			setMultiLineComment( false );
			return true;
		}
	}*/
	
	/*
	*  We have found a start delimiter
	*  and are still searching for the end delimiter
	*/
	/*private boolean isMultiLineComment() {
		return multiLineComment;
	}
	
	private void setMultiLineComment(boolean value)
	{
		multiLineComment = value;
	}*/
	
	/*
	*	Parse the line for tokens to highlight
	*/
	private void checkForTokens(String content, int startOffset, int endOffset)
	{
		while (startOffset <= endOffset)
		{
			//  skip the delimiters to find the start of a new token
			
			while ( isDelimiter( content.substring(startOffset, startOffset + 1) ) )
			{
				if (startOffset < endOffset)
					startOffset++;
				else
					return;
			}
			
			//  Extract and process the entire token
			
			if ( isQuoteDelimiter( content.substring(startOffset, startOffset + 1) ) )
				startOffset = getQuoteToken(content, startOffset, endOffset);
			else
				startOffset = getOtherToken(content, startOffset, endOffset);
		}
	}
	
	/*
	*
	*/
	private int getQuoteToken(String content, int startOffset, int endOffset) {
		String quoteDelimiter = content.substring(startOffset, startOffset + 1);
		String escapeString = getEscapeString(quoteDelimiter);
		
		int index;
		int endOfQuote = startOffset;
		
		//  skip over the escape quotes in this quote
		
		index = content.indexOf(escapeString, endOfQuote + 1);
		
		while ( (index > -1) && (index < endOffset) )
		{
			endOfQuote = index + 1;
			index = content.indexOf(escapeString, endOfQuote);
		}
		
		// now find the matching delimiter
		
		index = content.indexOf(quoteDelimiter, endOfQuote + 1);
		
		if ( (index < 0) || (index > endOffset) )
			endOfQuote = endOffset;
		else
			endOfQuote = index;
		
		doc.setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, quote, false);
		
		return endOfQuote + 1;
	}
	
	/*
	*
	*/
	private int getOtherToken(String content, int startOffset, int endOffset) {
		int endOfToken = startOffset + 1;
		
		while ( endOfToken <= endOffset )
		{
			if ( isDelimiter( content.substring(endOfToken, endOfToken + 1) ) )
				break;
			
			endOfToken++;
		}
		
		String token = content.substring(startOffset, endOfToken);
		
		/*if (isFind(token)) {
			findFound ++;
			doc.setCharacterAttributes(startOffset, endOfToken - startOffset, masFind, false);
			return endOfToken + 1;
		}*/
		
		boolean keywordF = false;
		for(int i=0; i<KEYWORD_CLASS; i++) {
			if (isKeyword(token, i)) {
				keywordF = true;
				doc.setCharacterAttributes(startOffset, endOfToken - startOffset, keyword[i], false);
				break;
			}
		}

		
		return endOfToken + 1;
	}
	
	/*
	*  Assume the needle will the found at the start/end of the line
	*/
	private int indexOf(String content, String needle, int offset) {
		int index;
		
		while ( (index = content.indexOf(needle, offset)) != -1 )
		{
			String text = getLine( content, index ).trim();
			
			if (text.startsWith(needle) || text.endsWith(needle))
				break;
			else
				offset = index + 1;
		}
		
		return index;
	}
	
	/*
	*  Assume the needle will the found at the start/end of the line
	*/
	private int lastIndexOf(String content, String needle, int offset) {
		int index;
		
		while ( (index = content.lastIndexOf(needle, offset)) != -1 )
		{
			String text = getLine( content, index ).trim();
			
			if (text.startsWith(needle) || text.endsWith(needle))
				break;
			else
				offset = index - 1;
		}
		
		return index;
	}
	
	private String getLine(String content, int offset) {
		int line = rootElement.getElementIndex( offset );
		Element lineElement = rootElement.getElement( line );
		int start = lineElement.getStartOffset();
		int end = lineElement.getEndOffset();
		return content.substring(start, end - 1);
	}
	
	/*
	*  Override for other languages
	*/
	protected boolean isDelimiter(String character) {
		String operands = ".,;:{}()[]+-/%<=>?!&|^~*";
		
		if (Character.isWhitespace(character.charAt(0) ) ||
			operands.indexOf(character) != -1 )
		return true;
		else
			return false;
	}
	
	/*
	*  Override for other languages
	*/
	protected boolean isQuoteDelimiter(String character) {
		String quoteDelimiters = "\"'";
		
		if (quoteDelimiters.indexOf(character) < 0)
			return false;
		else
			return true;
	}
	
	/*
	*  Override for other languages
	*/
	protected boolean isKeyword(String token, int i){
		return keywords[i].contains(token);
	}
	
	protected boolean isFind() {
		return (find != null) && (find.length() > 0);
	}
	
	protected boolean isFind(String token){
		//TraceManager.addDev("Comparing find=" + find + " token=" + token);
		if ((find == null) || (find.length() == 0)) {
			return false;
		}
		return token.startsWith(find);
	}
	

	
	
	/*
	*  Override for other languages
	*/
	/*protected String getStartDelimiter() {
		return "%";
	}*/
	
	/*
	*  Override for other languages
	*/
	//protected String getEndDelimiter() {
	//	return "*/";
	//}
	
	
	/*
	*  Override for other languages
	*/
	protected String getSingleLineDelimiter() {
		//return "//";
		return "%";
	}
	
	/*
	*  Override for other languages
	*/
	protected String getEscapeString(String quoteDelimiter) {
		return "\\" + quoteDelimiter;
	}
	
	/*
	*
	*/
	protected String addMatchingBrace(int offset) throws BadLocationException
	{
		StringBuffer whiteSpace = new StringBuffer();
		int line = rootElement.getElementIndex( offset );
		int i = rootElement.getElement(line).getStartOffset();
		
		while (true)
		{
			String temp = doc.getText(i, 1);
			
			if (temp.equals(" ") || temp.equals("\t"))
			{
				whiteSpace.append(temp);
				i++;
			}
			else
				break;
		}
		
		return "{\n" + whiteSpace.toString() + "\t\n" + whiteSpace.toString() + "}";
	}
	
}
