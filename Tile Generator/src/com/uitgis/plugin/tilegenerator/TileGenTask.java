package com.uitgis.plugin.tilegenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.uitgis.maple.common.util.Noti;

import framework.i18n.I18N;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class TileGenTask extends Task<Void> {

	private Thread mThread;
	private int mThreadNum;
	private final int totalWork = 500;
	private int count = 0;

	private synchronized void amountSync() {
		count = count + 1;
	}

	public TileGenTask(int mThreadNum) {
		super();
		this.mThreadNum = mThreadNum;
	}

	public int getmThreadNum() {
		return mThreadNum;
	}

	public void setmThreadNum(int mThreadNum) {
		this.mThreadNum = mThreadNum;
	}

	@Override
	protected Void call() throws Exception {
		mThread = Thread.currentThread();
		updateProgress(0, totalWork);

		if (mThreadNum < 0)
			return null;

		ExecutorService executor = Executors.newFixedThreadPool(mThreadNum);
		List<Callable<String>> taskList = new ArrayList<Callable<String>>();

		for (int i = 0; i < mThreadNum; i++) {

			TileGenCallable callable = new TileGenCallable(1, totalWork / mThreadNum);
			taskList.add(callable);

		}

		List<Future<String>> futureList = executor.invokeAll(taskList);
		boolean allDone = true;
		for (Future<String> fut : futureList) {
			try {

				System.out.println(new Date() + "::" + fut.get());
				allDone &= fut.isDone();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		if (allDone) {
			Platform.runLater(() -> {
				Noti.showInfo(I18N.getText("Msg_GenTileCompleted"));
			});
		}
		done();

		return null;
	}

	@Override
	protected void cancelled() {
		try {
			mThread.interrupt();
			Platform.runLater(() -> {
				Noti.showInfo(I18N.getText("Msg_GenTileCancelled"));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.cancelled();
	}

	public class TileGenCallable implements Callable<String> {

		private int min, max;

		public int getMin() {
			return min;
		}

		public void setMin(int min) {
			this.min = min;
		}

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}

		public TileGenCallable(int min, int max) {
			super();
			this.min = min;
			this.max = max;
		}

		@Override
		public String call() throws Exception {
			for (int tile = min; tile <= max; tile++) {
				if (isCancelled()) {
					break;
				}
				Thread.sleep(50);
				amountSync();
				updateMessage(I18N.getText("Msg_GenTileProcess") + " " + ": Amount " + count + "/" + totalWork);
				System.out.println(Thread.currentThread().getName() + " " + ": Amount " + count + "/" + totalWork);
			}

			return Thread.currentThread().getName();
		}

	};
}