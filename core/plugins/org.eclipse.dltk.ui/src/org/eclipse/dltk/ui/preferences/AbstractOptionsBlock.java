package org.eclipse.dltk.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.internal.ui.preferences.OptionsConfigurationBlock;
import org.eclipse.dltk.ui.dialogs.PropToPrefLinkArea;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public abstract class AbstractOptionsBlock extends OptionsConfigurationBlock
		implements IPreferenceDelegate {

	private List<PreferenceKey> keys = new ArrayList<PreferenceKey>();

	private ControlBindingManager bindManager;

	public AbstractOptionsBlock(IStatusChangeListener context,
			IProject project, PreferenceKey[] allKeys,
			IWorkbenchPreferenceContainer container) {
		super(context, project, allKeys, container);

		this.bindManager = new ControlBindingManager(this, context);
	}

	public Control createContents(Composite parent) {
		setShell(parent.getShell());
		Control control = createOptionsBlock(parent);
		initialize();

		return control;
	}

	protected void initialize() {
		bindManager.initialize();
	}

	protected abstract Control createOptionsBlock(Composite parent);

	protected final void bindControl(Button button, PreferenceKey key,
			Control[] dependencies) {
		bindManager.bindControl(button, key, dependencies);
		keys.add(key);
	}

	protected final void bindControl(Text textBox, PreferenceKey key,
			IFieldValidator validator) {
		bindManager.bindControl(textBox, key, validator);
		keys.add(key);
	}

	/**
	 * Binds the specified combobox. The result of {@link Combo#getItem(int)}
	 * will be used as value.
	 */
	protected final void bindControl(Combo combo, PreferenceKey key) {
		bindManager.bindControl(combo, key);
		keys.add(key);
	}

	/**
	 * Binds the specified combobox. Values are specified via the
	 * <code>itemValues</code> array.
	 */
	protected final void bindControl(Combo combo, PreferenceKey key,
			String[] itemValues) {
		bindManager.bindControl(combo, key, itemValues);
		keys.add(key);
	}

	@Override
	protected PreferenceKey[] getPreferenceKeys() {
		PreferenceKey[] prefKeys = super.getPreferenceKeys();
		if (keys.size() > 0) {
			PreferenceKey[] allKeys = new PreferenceKey[prefKeys.length
					+ keys.size()];
			System.arraycopy(prefKeys, 0, allKeys, 0, prefKeys.length);
			int counter = prefKeys.length;
			for (PreferenceKey preferenceKey : keys) {
				allKeys[counter++] = preferenceKey;
			}
			prefKeys = allKeys;
		}
		return prefKeys;
	}

	/**
	 * Returns the string that should be used as the title in the popup box that
	 * indicates a build needs to occur.
	 * 
	 * <p>
	 * Default implementation returns null. Clients should override to return
	 * context appropriate title. Clients must also override
	 * <code>getFullBuildDialogMessage()</code> and
	 * <code>getProjectBuildDialogMessage()</code> in order to trigger the popup
	 * box.
	 * </p>
	 * 
	 * @deprecated
	 * @see #getPreferenceChangeRebuildPrompt(boolean, Collection)
	 */
	protected final String getBuildDialogTitle() {
		return null;
	}

	/**
	 * Returns the string that should be used in the popup box that indicates a
	 * full build needs to occur.
	 * 
	 * <p>
	 * Default implementation returns null. Clients should override to return
	 * context appropriate message. Clients must also override
	 * <code>getBuildDialogTitle()</code> and
	 * <code>getProjectBuildDialogMessage()</code> in order to trigger the popup
	 * box.
	 * </p>
	 * 
	 * @deprecated
	 * @see #getPreferenceChangeRebuildPrompt(boolean, Collection)
	 */
	protected final String getFullBuildDialogMessage() {
		return null;
	}

	/**
	 * Returns the string that should be used in the popup box that indicates a
	 * project build needs to occur.
	 * 
	 * <p>
	 * Default implementation returns null. Clients should override to return
	 * context appropriate message. Clients must also override
	 * <code>getBuildDialogTitle()</code> and
	 * <code>getFullBuildDialogMessage()</code> in order to trigger the popup
	 * box.
	 * </p>
	 * 
	 * @deprecated
	 * @see #getPreferenceChangeRebuildPrompt(boolean, Collection)
	 */
	protected final String getProjectBuildDialogMessage() {
		return null;
	}

	protected final boolean isProjectPreferencePage() {
		return fProject != null;
	}

	/*
	 * @seeorg.eclipse.dltk.internal.ui.preferences.OptionsConfigurationBlock#
	 * performDefaults()
	 */
	public void performDefaults() {
		super.performDefaults();
		bindManager.initialize();
	}

	protected boolean saveValues() {
		return true;
	}

	/*
	 * Override performOk() as public API.
	 * 
	 * @see OptionsConfigurationBlock#performOk()
	 */
	public boolean performOk() {
		return saveValues() && super.performOk();
	}

	/*
	 * Override performApply() as public API.
	 * 
	 * @see OptionsConfigurationBlock#performApply()
	 */
	public boolean performApply() {
		return saveValues() && super.performApply();
	}

	/*
	 * @see
	 * org.eclipse.dltk.ui.preferences.IPreferenceDelegate#getBoolean(java.lang
	 * .Object)
	 */
	public final boolean getBoolean(Object key) {
		return getBooleanValue((PreferenceKey) key);
	}

	/*
	 * @see
	 * org.eclipse.dltk.ui.preferences.IPreferenceDelegate#getString(java.lang
	 * .Object)
	 */
	public final String getString(Object key) {
		return getValue((PreferenceKey) key);
	}

	/*
	 * @see
	 * org.eclipse.dltk.ui.preferences.IPreferenceDelegate#setBoolean(java.lang
	 * .Object, boolean)
	 */
	public final void setBoolean(Object key, boolean value) {
		super.setValue((PreferenceKey) key, value);
	}

	/*
	 * @see
	 * org.eclipse.dltk.ui.preferences.IPreferenceDelegate#setString(java.lang
	 * .Object, java.lang.String)
	 */
	public final void setString(Object key, String value) {
		setValue((PreferenceKey) key, value);
	}

	protected final IProject getProject() {
		return fProject;
	}

	protected final void updateStatus(IStatus status) {
		bindManager.updateStatus(status);
	}

	protected void createPrefLink(Composite composite, String message,
			final String prefPageId, final Object data) {
		PreferenceLinkArea area = new PreferenceLinkArea(composite, SWT.NONE,
				prefPageId, message, getPreferenceContainer(), data);

		area.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	protected void createPropToPrefLink(Composite composite, String message,
			final String prefPageId, final Object data) {
		PropToPrefLinkArea area = new PropToPrefLinkArea(composite, SWT.NONE,
				prefPageId, message, getShell(), data);

		area.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	/*
	 * Override getShell() method as public API.
	 * 
	 * @see OptionsConfigurationBlock#getShell()
	 */
	protected Shell getShell() {
		return super.getShell();
	}

	/*
	 * Override dispose() method as public API.
	 * 
	 * @see OptionsConfigurationBlock#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

	/*
	 * Override statusChanged() as public API.
	 * 
	 * @see OptionsConfigurationBlock#statusChanged(IStatus)
	 */
	protected void statusChanged(IStatus status) {
		super.statusChanged(status);
	}

	/*
	 * Override getPreferenceChangeRebuildPrompt() as public API
	 * 
	 * @see OptionsConfigurationBlock#getPreferenceChangeRebuildPrompt(boolean,
	 * java.util.Collection)
	 */
	protected IPreferenceChangeRebuildPrompt getPreferenceChangeRebuildPrompt(
			boolean workspaceSettings, Collection<PreferenceKey> changedOptions) {
		return super.getPreferenceChangeRebuildPrompt(workspaceSettings,
				changedOptions);
	}

}
