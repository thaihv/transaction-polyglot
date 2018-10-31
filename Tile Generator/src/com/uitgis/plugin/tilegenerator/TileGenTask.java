package com.uitgis.plugin.tilegenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.inject.Inject;
import com.uitgis.maple.common.util.Noti;
import com.uitgis.plugin.tilegenerator.model.TileScale;
import com.uitgis.plugin.tilegenerator.model.WizardData;
import com.uitgis.sdk.layer.GroupLayer;
import com.uitgis.sdk.layer.ILayer;
import com.vividsolutions.jts.geom.Envelope;

import framework.i18n.I18N;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;

public class TileGenTask extends Task<Void> {

	private Thread mThread;
	private int mThreadNum;
	private int totalWork;
	private int count = 0;
	
	private LevelDefinition[] mLevelDefs;
	
	@Inject
	WizardData model;
	

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

		GroupLayer group = model.getGDX().getRootGroupLayer();		
		List<ILayer> layers = group.getLayers();
		if (layers.isEmpty()) {
			Platform.runLater(() -> {
				Noti.showInfo(I18N.getText("err.message.1"));
			});
			return null;
		}

		ObservableList<TileScale> levels = model.getListTileScale();
		if (levels.isEmpty()) {
			Platform.runLater(() -> {
				Noti.showInfo(I18N.getText("err.message.2"));
			});
			return null;
		}		
		
		Envelope envelope = model.getGDX().getEnvelope();  // Calc target?
		
		Point2D origin = new Point2D(Double.parseDouble(model.getOriginX()), Double.parseDouble(model.getOriginY()));
		
		double factor = 1;
		
		int numOfLevels = levels.size();
		mLevelDefs = new LevelDefinition[numOfLevels];
		
		int totalTiles = 0;
		
		for (int i=0; i<numOfLevels; i++) {
			if (!levels.get(i).isActive()) {
				continue;
			}
			
			int level = model.getOrderLevel() == 1 ? levels.size() - i - 1 : i;
			
			double hdistance = model.getTileWidth() * levels.get(i).getScale() * factor;
			double vdistance = model.getTileHeight() * levels.get(i).getScale() * factor;
			

			
			int hStart = getGridCellNo(envelope.getMinX() - origin.getX(), hdistance, false);
			int hEnd = getGridCellNo(envelope.getMaxX() - origin.getX(), hdistance, true);
			int vStart = getGridCellNo(envelope.getMinY() - origin.getY(), vdistance, false);
			int vEnd = getGridCellNo(envelope.getMaxY() - origin.getY(), vdistance, true);
			
			mLevelDefs[i] = new LevelDefinition(level, new TileRange(hStart, hEnd, vStart, vEnd), hdistance, vdistance);
			
			totalTiles += (hEnd - hStart + 1) * (vEnd - vStart + 1);
		}		
		
		totalWork = totalTiles;
		updateMessage("Building pyramid of tiles ... (total number of tiles: " + totalWork);
		
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
	
	private int getGridCellNo(double coordinate, double gridSize, boolean excludeTouch) {
		double d = coordinate / gridSize;
		if (d < 0) {
			return (int)Math.round(d - 0.5D) + 1;
		}
		return (!excludeTouch || d > (int)d) ? (int)d + 1 : (int)d;
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