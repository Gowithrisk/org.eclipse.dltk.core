package org.eclipse.dltk.internal.ui.rse;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.internal.rse.DLTKRSEPlugin;
import org.eclipse.dltk.core.internal.rse.RSEConnectionQueryManager;
import org.eclipse.dltk.core.internal.rse.RSEConnectionQueryManager.IConnector;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @since 2.0
 */
public class RSEConnector implements IConnector {
	private static boolean running = true;
	private Thread processingThread = null;

	private Thread createPoprocessingThread() {
		return new Thread("RSE connection resolver") {
			public void run() {
				while (running) {
					if (RSEConnectionQueryManager.getInstance().hasHosts()) {
						Display display = PlatformUI.getWorkbench()
								.getDisplay();
						display.syncExec(new Runnable() {
							public void run() {
								IHost host = RSEConnectionQueryManager
										.getInstance().getNextHost(false);
								if (host != null) {
									connect(host);
									RSEConnectionQueryManager.getInstance()
											.markHostAsFinished(host);
								}
							}
						});
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						DLTKRSEPlugin.log(e);
					}
				}
			}
		};
	}

	private void connect(IHost host) {
		ISubSystem[] subSystems = host.getSubSystems();
		for (ISubSystem subsystem : subSystems) {
			if (subsystem instanceof IRemoteFileSubSystem) {
				try {
					subsystem.connect(new NullProgressMonitor(), false);
				} catch (Exception e) {
					DLTKRSEPlugin.log(e);
				}
			}
		}
	};

	public RSEConnector() {
	}

	public boolean isDirectProcessingRequired() {
		// Process direct connection request.
		Display current = Display.getCurrent();
		if (current != null) {
			// We are in UI thread
			return true;
		}
		return false;
	}

	public void register() {
		if (processingThread == null) {
			processingThread = createPoprocessingThread();
			processingThread.start();
		}
	}

	public void runDisplayRunnables() {
		// We need to interrupt processingThread if it is no executing.
		Display current = Display.getCurrent();
		while (RSEConnectionQueryManager.getInstance().hasHosts()
				&& current.readAndDispatch() && !current.isDisposed()) {
			IHost host = RSEConnectionQueryManager.getInstance().getNextHost(
					false);
			if (host != null) {
				connect(host);
				RSEConnectionQueryManager.getInstance()
						.markHostAsFinished(host);
			}
			current.sleep();
		}
	}

	public static void stop() {
		running = false;
	}
}
