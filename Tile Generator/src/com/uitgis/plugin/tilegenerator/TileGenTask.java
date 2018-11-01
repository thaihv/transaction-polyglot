package com.uitgis.plugin.tilegenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.inject.Inject;
import com.uitgis.maple.common.util.Noti;
import com.uitgis.plugin.tilegenerator.model.WizardData;
import com.uitgis.sdk.layer.GroupLayer;
import com.uitgis.sdk.layer.ILayer;
import com.uitgis.sdk.reference.CRSHelper;
import com.uitgis.sdk.reference.crs.CoordinateReferenceSystem;
import com.uitgis.sdk.reference.crs.GeographicCRS;
import com.uitgis.sdk.reference.datum.GeodeticDatum;
import com.vividsolutions.jts.geom.Envelope;

import framework.i18n.I18N;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;

public class TileGenTask extends Task<Void> {

	private Thread mThread;
	private int mThreadNum;
	private int totalWork;
	private int count = 0;

	private LevelDefinition[] mLevelDefs;
	private TMConfiguration mConfiguration;
	private boolean mOverwriteMode;

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
		
		// Initilize a thread pool & task list
		mThread = Thread.currentThread();
		if (mThreadNum < 0)
			return null;
		ExecutorService executor = Executors.newFixedThreadPool(mThreadNum);
		List<Callable<String>> taskList = new ArrayList<Callable<String>>();
		
		// Seting up Tile Configuration & check condition to gen
		mConfiguration = new TMConfiguration(model);
		mOverwriteMode = false;
		boolean buildable = false;
		
		int type = mConfiguration.getTypeOfTileMap();
		if (type == TMConfiguration.TYPE_FILE_TILEMAP) {
			buildable = checkFTMDuplication();
		} else if (type == TMConfiguration.TYPE_GSS_TILEMAP) {
			//buildable = checkGTMDuplication();
		}

		if (!buildable) {
			return null;
		}

		// Check grouplayer is existed?
		GroupLayer group = model.getGDX().getRootGroupLayer();
		List<ILayer> layers = group.getLayers();
		if (layers.isEmpty()) {
			Platform.runLater(() -> {
				Noti.showInfo(I18N.getText("err.message.1"));
			});
			return null;
		}

		// Get levels to calculate
		TMConfiguration.LevelSpec[] levels = mConfiguration.getLevels();
		if (levels.length == 0) {
			Platform.runLater(() -> {
				Noti.showInfo(I18N.getText("err.message.2"));
			});
			return null;
		}

		CoordinateReferenceSystem targetCRS = mConfiguration.getTargetCRS();
		Envelope envelope = mConfiguration.getTargetEnvelope();

		Envelope groupBounds = layers.get(0).getDataEnvelope();
		for (ILayer l : layers) {
			groupBounds = groupBounds.intersection(l.getDataEnvelope());

		}
		envelope = CRSHelper.getIntersectionBounds(envelope, groupBounds, targetCRS);

		Point2D origin = mConfiguration.getOrigin();

		double factor = 1;
		if (targetCRS instanceof GeographicCRS) {
			GeodeticDatum datum = (GeodeticDatum) ((GeographicCRS) targetCRS).getDatum();
			double semiMajorAxis = datum.getEllipsoid().getSemiMajorAxis();
			double circumference = 2D * Math.PI * semiMajorAxis;
			factor = 360D / circumference;
		}

		int numOfLevels = mConfiguration.getNumberOfLevels();
		mLevelDefs = new LevelDefinition[numOfLevels];

		int totalTiles = 0;

		for (int i = 0; i < numOfLevels; i++) {
			if (!levels[i].isOn) {
				continue;
			}

			int level = mConfiguration.getLevelOrder() == TMConfiguration.ORDER_DESCENDING ? levels.length - i - 1 : i;
			double hdistance = model.getTileWidth() * levels[i].scale * factor;
			double vdistance = model.getTileHeight() * levels[i].scale * factor;
			int hStart = getGridCellNo(envelope.getMinX() - origin.getX(), hdistance, false);
			int hEnd   = getGridCellNo(envelope.getMaxX() - origin.getX(), hdistance, true);
			int vStart = getGridCellNo(envelope.getMinY() - origin.getY(), vdistance, false);
			int vEnd   = getGridCellNo(envelope.getMaxY() - origin.getY(), vdistance, true);

			mLevelDefs[i] = new LevelDefinition(level, new TileRange(hStart, hEnd, vStart, vEnd), hdistance, vdistance);

			totalTiles += (hEnd - hStart + 1) * (vEnd - vStart + 1);
		}
		if (type == TMConfiguration.TYPE_FILE_TILEMAP) {
			ensureFTMPrerequisite();
		} else if (type == TMConfiguration.TYPE_GSS_TILEMAP) {
			//ensureGTMPrerequisite();
		}

		// Ok, Done to calculate Total of Works, let display to status
		totalWork = totalTiles;
		updateProgress(0, totalWork);
		updateMessage("Building pyramid of tiles ... (total number of tiles: " + totalWork);
		
		// Divide the task to units and assign to thread
		long time = System.currentTimeMillis();
		
		// for each level
		for (int i = 0; i < numOfLevels; i++) {
			if (mLevelDefs[i] == null) {
				continue;
			}
			
			TileRange range = mLevelDefs[i].getTileRange();
			// Calculate by Y
			for (int vindex=range.getMinimumY()-1, vEnd=range.getMaximumY(); vindex<vEnd; vindex ++) {
				double y = origin.getY() + vindex * mLevelDefs[i].getTileDistanceOnYAXIS();
				// Calculate by X
				for (int hindex=range.getMinimumX()-1, hEnd=range.getMaximumX(); hindex<hEnd; hindex ++) {
					if (isCancelled()) {
						break;
					}
					
					double x = origin.getX() + hindex * mLevelDefs[i].getTileDistanceOnXAXIS();
					Envelope bbox = new Envelope(x, y, 
							x + mLevelDefs[i].getTileDistanceOnXAXIS(), y + mLevelDefs[i].getTileDistanceOnYAXIS(),
							mConfiguration.getTargetCRS());
					
					
					
//					BuilderJob job = (type == TMConfiguration.TYPE_FILE_TILEMAP) ?
//							new BuilderJobOnFile(mLevelDefs[i].getLevel(), hindex, vindex, bbox) :
//							new BuilderJobOnGSS(mLevelDefs[i].getLevel(), hindex, vindex, bbox);
//					queue.enqueue(job);
					
				}	// X
			}	// Y
		}	// LEVEL	
		
		System.out.println((System.currentTimeMillis()-time) + "ms has been elapsed to build " + totalTiles + " tiles.");
		

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
			return (int) Math.round(d - 0.5D) + 1;
		}
		return (!excludeTouch || d > (int) d) ? (int) d + 1 : (int) d;
	}

	private boolean checkFTMDuplication() {
		File tileMapFolder = new File(mConfiguration.getDestinationFolder(), mConfiguration.getTileMapName());
		File tileMapFile = new File(tileMapFolder, "tilemap.xml"); //$NON-NLS-1$
		boolean buildable = true;

		if (tileMapFile.exists()) {
			if (mConfiguration.overwriteAllowed()) {
				mOverwriteMode = true;
			} else {
				buildable = false;
			}
		}

		return buildable;
	}

	private void ensureFTMPrerequisite() {
		if (mOverwriteMode) {
			return;
		}

		File tileMapFolder = new File(model.getDestinationFolder(), mConfiguration.getTileMapName());
		File tileMapFile = new File(tileMapFolder, "tilemap.xml"); //$NON-NLS-1$

		tileMapFolder.mkdirs();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(tileMapFile);

			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			fac.setNamespaceAware(true);
			fac.setValidating(false);
			fac.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", //$NON-NLS-1$
					"http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$
			DocumentBuilder builder = fac.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.newDocument();
			TileMapDomHelper.encodeTileMapXML(doc, mConfiguration);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			t.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$

			DOMSource doms = new DOMSource(doc);
			StreamResult sr = new StreamResult(out);

			t.transform(doms, sr);

			out.flush();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				out.close();
			} catch (Throwable t) {
			}
		}
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