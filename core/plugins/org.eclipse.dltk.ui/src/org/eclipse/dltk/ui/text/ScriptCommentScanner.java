/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Sam Faktorovich)
 *******************************************************************************/
package org.eclipse.dltk.ui.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.compiler.task.ITodoTaskPreferences;
import org.eclipse.dltk.ui.text.rules.CombinedWordRule;
import org.eclipse.dltk.ui.text.rules.CombinedWordRule.CharacterBuffer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;

public class ScriptCommentScanner extends AbstractScriptScanner {

	private final String[] fProperties;
	private final String fDefaultTokenProperty;

	private TaskTagMatcher fTaskTagMatcher;

	private final ITodoTaskPreferences preferences;

	private static class ScriptIdentifierDetector implements IWordDetector {

		public boolean isWordStart(char c) {
			return Character.isJavaIdentifierStart(c);
		}

		public boolean isWordPart(char c) {
			return Character.isJavaIdentifierPart(c);
		}
	}

	private static class TaskTagMatcher extends CombinedWordRule.WordMatcher {

		private IToken fToken;

		/**
		 * Uppercase words
		 */
		private Map<CharacterBuffer, IToken> fUppercaseWords = new HashMap<CharacterBuffer, IToken>();

		/**
		 * <code>true</code> if task tag detection is case-sensitive.
		 */
		private boolean fCaseSensitive = true;

		/**
		 * Buffer for uppercase word
		 */
		private CombinedWordRule.CharacterBuffer fBuffer = new CombinedWordRule.CharacterBuffer(
				16);

		public TaskTagMatcher(IToken token) {
			fToken = token;
		}

		@Override
		public synchronized void clearWords() {
			super.clearWords();
			fUppercaseWords.clear();
		}

		public synchronized void addTaskTags(String[] tasks) {
			for (int i = 0; i < tasks.length; i++) {
				if (tasks[i].length() > 0) {
					addWord(tasks[i], fToken);
				}
			}
		}

		@Override
		public synchronized void addWord(String word, IToken token) {
			Assert.isNotNull(word);
			Assert.isNotNull(token);

			super.addWord(word, token);
			fUppercaseWords.put(new CombinedWordRule.CharacterBuffer(word
					.toUpperCase()), token);
		}

		@Override
		public synchronized IToken evaluate(ICharacterScanner scanner,
				CombinedWordRule.CharacterBuffer word) {
			if (fCaseSensitive)
				return super.evaluate(scanner, word);

			fBuffer.clear();
			for (int i = 0, n = word.length(); i < n; i++)
				fBuffer.append(Character.toUpperCase(word.charAt(i)));

			IToken token = fUppercaseWords.get(fBuffer);
			if (token != null)
				return token;
			return Token.UNDEFINED;
		}

		/**
		 * Is task tag detection case-sensitive?
		 * 
		 * @return <code>true</code> iff task tag detection is case-sensitive
		 * @since 3.0
		 */
		@SuppressWarnings("unused")
		public boolean isCaseSensitive() {
			return fCaseSensitive;
		}

		/**
		 * Enables/disables the case-sensitivity of the task tag detection.
		 * 
		 * @param caseSensitive
		 *            <code>true</code> iff case-sensitivity should be enabled
		 * @since 3.0
		 */
		public void setCaseSensitive(boolean caseSensitive) {
			fCaseSensitive = caseSensitive;
		}
	}

	public ScriptCommentScanner(IColorManager manager, IPreferenceStore store,
			String comment, String todoTag, ITodoTaskPreferences preferences) {
		super(manager, store);

		fProperties = new String[] { comment, todoTag };
		fDefaultTokenProperty = comment;

		this.preferences = preferences;
		initialize();
	}

	@Override
	protected String[] getTokenProperties() {
		return fProperties;
	}

	@Override
	protected List<IRule> createRules() {
		IToken defaultToken = getToken(fDefaultTokenProperty);
		setDefaultReturnToken(defaultToken);

		List<IRule> list = new ArrayList<IRule>();
		list.add(createTodoRule());

		return list;
	}

	protected IRule createTodoRule() {
		CombinedWordRule combinedWordRule = new CombinedWordRule(
				new ScriptIdentifierDetector(), Token.UNDEFINED);

		List<TaskTagMatcher> matchers = createMatchers();
		if (matchers.size() > 0) {
			for (int i = 0, n = matchers.size(); i < n; i++) {
				combinedWordRule.addWordMatcher(matchers.get(i));
			}
		}

		return combinedWordRule;
	}

	/**
	 * Creates a list of word matchers.
	 * 
	 * @return the list of word matchers
	 */
	protected List<TaskTagMatcher> createMatchers() {
		List<TaskTagMatcher> list = new ArrayList<TaskTagMatcher>();

		boolean isCaseSensitive = preferences.isCaseSensitive();
		String[] tasks = preferences.getTagNames();

		if (tasks != null) {
			fTaskTagMatcher = new TaskTagMatcher(
					getToken(DLTKColorConstants.TASK_TAG));
			fTaskTagMatcher.addTaskTags(tasks);
			fTaskTagMatcher.setCaseSensitive(isCaseSensitive);
			list.add(fTaskTagMatcher);
		}

		return list;
	}

	/**
	 * Returns the character used to identify a comment.
	 * 
	 * <p>
	 * Default implementation returns <code>#</code>. Clients may override if
	 * their language uses a different identifier.
	 * </p>
	 */
	protected char getCommentChar() {
		return '#';
	}

	@Override
	public void setRange(IDocument document, int offset, int length) {
		super.setRange(document, offset, length);
		state = STATE_START;
	}

	@Override
	public void adaptToPreferenceChange(PropertyChangeEvent event) {
		if (fTaskTagMatcher != null
				&& event.getProperty().equals(ITodoTaskPreferences.TAGS)) {

			Object value = event.getNewValue();
			if (value instanceof String) {
				synchronized (fTaskTagMatcher) {
					fTaskTagMatcher.clearWords();
					fTaskTagMatcher.addTaskTags(preferences.getTagNames());
				}
			}
		} else if (fTaskTagMatcher != null
				&& event.getProperty().equals(
						ITodoTaskPreferences.CASE_SENSITIVE)) {
			Object value = event.getNewValue();
			if (value instanceof String) {
				boolean caseSensitive = Boolean.valueOf((String) value)
						.booleanValue();
				fTaskTagMatcher.setCaseSensitive(caseSensitive);
			}
		} else if (super.affectsBehavior(event)) {
			super.adaptToPreferenceChange(event);
		}
	}

	@Override
	public boolean affectsBehavior(PropertyChangeEvent event) {
		if (event.getProperty().equals(ITodoTaskPreferences.TAGS)) {
			return true;
		}

		if (event.getProperty().equals(ITodoTaskPreferences.CASE_SENSITIVE)) {
			return true;
		}

		return super.affectsBehavior(event);
	}

	private int state = STATE_START;

	private static final int STATE_START = 0;
	private static final int STATE_STARTED = 1;
	private static final int STATE_BODY = 2;

	/**
	 * Skip possible comment characters. Returns the number of characters
	 * skipped, zero if none.
	 * 
	 * @return
	 */
	protected int skipCommentChars() {
		int c = read();
		if (c == getCommentChar()) {
			return 1;
		} else {
			unread();
			return 0;
		}
	}

	/*
	 * We overload nextToken() because of the way task parsing is implemented:
	 * the TO-DO tasks are recognized only at the beginning of the comment
	 */
	@Override
	public IToken nextToken() {
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;
		if (state == STATE_START) {
			state = STATE_STARTED;
			int count = skipCommentChars();
			int c = read();
			while (c != EOF && Character.isWhitespace((char) c)) {
				c = read();
				++count;
			}
			unread();
			if (count > 0) {
				return fDefaultReturnToken;
			} else if (c == EOF) {
				return Token.EOF;
			}
		}
		if (state == STATE_STARTED) {
			state = STATE_BODY;
			final IToken token = fRules[0].evaluate(this);
			if (!token.isUndefined()) {
				return token;
			}
		}
		int count = 0;
		while (read() != EOF) {
			++count;
		}
		return count > 0 ? fDefaultReturnToken : Token.EOF;
	}
}
