package org.eclipse.dltk.core.model.binary;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelProvider;
import org.eclipse.dltk.core.IProblemRequestor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.internal.core.AbstractSourceModule;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.ModelProviderManager;
import org.eclipse.dltk.internal.core.OpenableElementInfo;
import org.eclipse.dltk.utils.CorePrinter;

/**
 * @since 2.0
 */
public class BinaryModule extends AbstractSourceModule implements
		IBinaryModule, IExternalSourceModule {
	private SourceMapper sourceMapper = new SourceMapper();

	protected BinaryModule(ModelElement parent, String name,
			WorkingCopyOwner owner) {
		super(parent, name, owner);
	}

	@Override
	protected Object createElementInfo() {
		return new BinaryModuleElementInfo();
	}

	@Override
	protected IStatus validateSourceModule(IDLTKLanguageToolkit toolkit,
			IResource resource) {
		return Status.OK_STATUS;
	}

	@Override
	protected IStatus validateSourceModule(IResource resource)
			throws CoreException {
		return Status.OK_STATUS;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	protected boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm, Map newElements, IResource underlyingResource)
			throws ModelException {
		final BinaryModuleElementInfo moduleInfo = (BinaryModuleElementInfo) info;

		IBinaryElementParser binaryParser = DLTKLanguageManager
				.getBinaryElementParser(this);
		if (binaryParser == null) {
			DLTKCore.error("Binary parser for binary module are not found:");
			return false;
		}
		BinaryModuleStructureRequestor requestor = new BinaryModuleStructureRequestor(
				this, moduleInfo, this.getSourceMapper());
		binaryParser.setRequestor(requestor);
		binaryParser.parseBinaryModule(this);

		// We need to update children contents using model providers
		List<IModelElement> childrenSet = new ArrayList<IModelElement>(
				moduleInfo.getChildrenAsList());
		// Call for extra model providers
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(this);
		IModelProvider[] providers = ModelProviderManager.getProviders(toolkit
				.getNatureId());
		if (providers != null) {
			for (int i = 0; i < providers.length; i++) {
				providers[i].provideModelChanges(this, childrenSet);
			}
		}
		moduleInfo.setChildren(childrenSet);

		return moduleInfo.isStructureKnown();
	}

	public IResource getResource() {
		return null;
	}

	public void setSourceMapper(SourceMapper mapper) {
		this.sourceMapper = mapper;
	}

	public SourceMapper getSourceMapper() {
		return this.sourceMapper;
	}

	public boolean isBinary() {
		return true;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return JEM_USER_ELEMENT;
	}

	@Override
	public void printNode(CorePrinter output) {
	}

	public int getElementType() {
		// TODO: Replace with BINARY_MODULE then full support of binary modules
		// will be implemented.
		return SOURCE_MODULE;
	}

	public String getSource() throws ModelException {
		return "//Binary source are not available";
	}

	public ISourceRange getSourceRange() throws ModelException {
		return null;
	}

	public char[] getSourceAsCharArray() throws ModelException {
		return getSource().toCharArray();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof BinaryModule)) {
			return false;
		}
		return super.equals(obj);
	}

	public void becomeWorkingCopy(IProblemRequestor problemRequestor,
			IProgressMonitor monitor) throws ModelException {
	}

	public void commitWorkingCopy(boolean force, IProgressMonitor monitor)
			throws ModelException {
	}

	public void discardWorkingCopy() throws ModelException {
	}

	public ISourceModule getPrimary() {
		return this;
	}

	public ISourceModule getWorkingCopy(IProgressMonitor monitor)
			throws ModelException {
		return this;
	}

	public ISourceModule getWorkingCopy(WorkingCopyOwner owner,
			IProblemRequestor problemRequestor, IProgressMonitor monitor)
			throws ModelException {
		return this;
	}

	public boolean isBuiltin() {
		return false;
	}

	public boolean isPrimary() {
		return true;
	}

	public boolean isWorkingCopy() {
		return true;
	}

	public void reconcile(boolean forceProblemDetection,
			WorkingCopyOwner owner, IProgressMonitor monitor)
			throws ModelException {
	}

	public void copy(IModelElement container, IModelElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws ModelException {
	}

	public void delete(boolean force, IProgressMonitor monitor)
			throws ModelException {
	}

	public void move(IModelElement container, IModelElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws ModelException {
	}

	public void rename(String name, boolean replace, IProgressMonitor monitor)
			throws ModelException {
	}

	@Override
	protected char[] getBufferContent() throws ModelException {
		return getSource().toCharArray();
	}

	@Override
	protected String getModuleType() {
		return "Binary Source Module";
	}

	@Override
	protected ISourceModule getOriginalSourceModule() {
		return this;
	}

	@Override
	protected String getNatureId() throws CoreException {
		IDLTKLanguageToolkit lookupLanguageToolkit = lookupLanguageToolkit(getScriptProject());
		if (lookupLanguageToolkit == null) {
			return null;
		}
		return lookupLanguageToolkit.getNatureId();
	}

	public char[] getFileName() {
		return this.getPath().toOSString().toCharArray();
	}

	@Override
	public IPath getPath() {
		return this.getParent().getPath().append(this.getElementName());
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(getSource().getBytes());
	}

	public IPath getFullPath() {
		return getPath();
	}

	public String getName() {
		return getPath().lastSegment();
	}
}
