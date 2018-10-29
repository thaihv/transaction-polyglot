package com.uitgis.plugin.tilegenerator;

import com.uitgis.maple.common.util.Noti;

import framework.i18n.I18N;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class TileGenTask extends Task<Void> {
	private Thread mThread;

	public TileGenTask() {
		super();
	}
	@Override
	protected Void call() throws Exception {
		mThread = Thread.currentThread();

		updateMessage(I18N.getText("Msg_StartTile"));
        final int max = 1000;
        updateProgress(0, max);
        for (int i = 1; i <= max; i++) {
			if (isCancelled()) {
				break;
			}
        	updateMessage(I18N.getText("Msg_GenTileProcess") + " " + + ++i + "/" + max);
        	System.out.println(Thread.currentThread().getName() + " > "   + I18N.getText("Msg_GenTileProcess") + " " + i + "/" + max);
        	Thread.sleep(10);
//            updateProgress(i, max);
        }
		Platform.runLater(() -> {
			Noti.showInfo(I18N.getText("Msg_GenTileCompleted"));
		});
		done();
		
		return null;
	}
	@Override
	protected void cancelled() {
		try {
			mThread.interrupt();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		super.cancelled();
	}
}