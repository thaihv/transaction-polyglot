package com.uitgis.plugin.tilegenerator;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
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
import com.uitgis.sdk.controls.MapTransform;
import com.uitgis.sdk.layer.AbstractLayer;
import com.uitgis.sdk.layer.FeatureLayer;
import com.uitgis.sdk.layer.GroupLayer;
import com.uitgis.sdk.layer.ILayer;
import com.uitgis.sdk.layer.RasterLayer;
import com.uitgis.sdk.layer.ScreenOffset;
import com.uitgis.sdk.layer.TileMapLayer;
import com.uitgis.sdk.layer.WMSLayer;
import com.uitgis.sdk.reference.CRSHelper;
import com.uitgis.sdk.reference.crs.CoordinateReferenceSystem;
import com.uitgis.sdk.reference.crs.GeographicCRS;
import com.uitgis.sdk.reference.datum.GeodeticDatum;
import com.uitgis.sdk.style.symbol.Context;
import com.vividsolutions.jts.geom.Envelope;

import framework.i18n.I18N;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;

public class TileGenTask extends Task<Void> {

	private static final String KEY_LEVEL = "{$L}";
	private static final String KEY_X = "{$X}";
	private static final String KEY_Y = "{$Y}";

	private Thread mThread;
	private int mThreadNum;
	private int totalWork;
	private int count = 0;
	Semaphore lock = new Semaphore(100);

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

		// Initialize a thread pool & task list
		mThread = Thread.currentThread();
		if (mThreadNum < 0)
			return null;
		ExecutorService executor = Executors.newFixedThreadPool(mThreadNum);
		
		//threadExecutor.setRejectedExecutionHandler(new );
//		List<Callable<String>> taskList = new ArrayList<Callable<String>>();
		
		List<Future<?>> tasks = new ArrayList<>();

		// Seting up Tile Configuration & check condition to gen
		mConfiguration = new TMConfiguration(model);
		mOverwriteMode = false;
		boolean buildable = false;

		int type = mConfiguration.getTypeOfTileMap();
		if (type == TMConfiguration.TYPE_FILE_TILEMAP) {
			buildable = checkFTMDuplication();
		} else if (type == TMConfiguration.TYPE_GSS_TILEMAP) {
			// buildable = checkGTMDuplication();
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
			int hEnd = getGridCellNo(envelope.getMaxX() - origin.getX(), hdistance, true);
			int vStart = getGridCellNo(envelope.getMinY() - origin.getY(), vdistance, false);
			int vEnd = getGridCellNo(envelope.getMaxY() - origin.getY(), vdistance, true);

			mLevelDefs[i] = new LevelDefinition(level, new TileRange(hStart, hEnd, vStart, vEnd), hdistance, vdistance);

			totalTiles += (hEnd - hStart + 1) * (vEnd - vStart + 1);
		}
		if (type == TMConfiguration.TYPE_FILE_TILEMAP) {
			ensureFTMPrerequisite();
		} else if (type == TMConfiguration.TYPE_GSS_TILEMAP) {
			// ensureGTMPrerequisite();
		}

		// Ok, Done to calculate Total of Works, let display to status
		totalWork = totalTiles;
		updateProgress(0, totalWork);
		updateMessage("Building ... (total number of tiles: " + totalWork + ")");
		

		
		// Divide the task to units and assign to thread
		long time = System.currentTimeMillis();

		// For each level
		for (int i = 0; i < numOfLevels; i++) {
			if (mLevelDefs[i] == null) {
				continue;
			}

			TileRange range = mLevelDefs[i].getTileRange();
			// Calculate by Y
			for (int vindex = range.getMinimumY() - 1, vEnd = range.getMaximumY(); vindex < vEnd; vindex++) {
				double y = origin.getY() + vindex * mLevelDefs[i].getTileDistanceOnYAXIS();
				// Calculate by X
				for (int hindex = range.getMinimumX() - 1, hEnd = range.getMaximumX(); hindex < hEnd; hindex++) {
					if (isCancelled()) {
						break;
					}

					double x = origin.getX() + hindex * mLevelDefs[i].getTileDistanceOnXAXIS();

					Envelope bbox = new Envelope(x, x + mLevelDefs[i].getTileDistanceOnXAXIS(), y, y + mLevelDefs[i].getTileDistanceOnYAXIS(), mConfiguration.getTargetCRS());
					BufferedImage bi = new BufferedImage(mConfiguration.getTileWidth(), mConfiguration.getTileHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics g = bi.getGraphics();
					Context ctx = new Context(model.getGDX(), (Graphics2D) g);
					
//					while (tasks.size() > 1000) {
//						try {
//						
//							for (Future<?> fut : tasks) {
//								if (fut.isDone())
//									tasks.remove(fut.get());
//							}
//							
//						}
//						catch (Throwable t) {
//						}
//					}
					
					lock.acquire();
					
					TileGenCallable callable = new TileGenCallable(ctx, bbox, layers, bi, i, hindex, vindex);
					
					Future<?> task = executor.submit(callable);			
					
					tasks.add(task);
					

					
//					taskList.add(callable);

				} // X
			} // Y
		} // LEVEL

//		List<Future<String>> futureList = executor.invokeAll(taskList);
//		boolean allDone = true;
//		for (Future<String> fut : futureList) {
//			try {
//
//				System.out.println(new Date() + "::" + fut.get());
//				allDone &= fut.isDone();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}

		
		boolean allDone = true;
		for (Future<?> fut : tasks) {
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
		System.out.println((System.currentTimeMillis() - time) + "ms has been elapsed to build " + totalTiles + " tiles.");
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

	private File getTileFile(TMConfiguration configuration, int level, int xIndex, int yIndex) {
		String expression = configuration.getTilePathExpression();
		expression = replaceVariables(expression, KEY_LEVEL, String.valueOf(level));
		expression = replaceVariables(expression, KEY_X, String.valueOf(xIndex));
		expression = replaceVariables(expression, KEY_Y, String.valueOf(yIndex));

		File tileMapFolder = new File(configuration.getDestinationFolder(), configuration.getTileMapName());
		return new File(tileMapFolder, expression + "." + configuration.getOutputTypeAsString());
	}

	private String replaceVariables(String source, String key, String replaceValue) {
		int index = -1;
		while ((index = source.indexOf(key)) > -1) {
			String pre = source.substring(0, index);
			String sub = source.substring(index + key.length());
			source = pre + replaceValue + sub;
		}
		return source;
	}

	protected void save(BufferedImage bi, File tileFile, String format) throws IOException {
		FileOutputStream out = null;
		try {
			tileFile.getParentFile().mkdirs();

			out = new FileOutputStream(tileFile);
			writeRaster(bi, format, out);
		} finally {
			try {
				out.close();
			} catch (Throwable t) {
			}
		}
	}

	protected void writeRaster(BufferedImage bi, String format, OutputStream out) throws IOException {
		ImageIO.write(bi, format, out);
	}

	public void getMapImage(Context context, List<ILayer> layers) {

		if (context.getEnvelope() != null) {
			MapTransform transform = new MapTransform(context.getEnvelope(), context.getWidth(), context.getHeight(),
					context.getMapCRS());
			context.setMapToScreenTransform(transform);

			if (layers == null || layers.size() == 0) {
				for (int i = model.getGDX().getLayerCount() - 1; i >= 0; i--) {
					ILayer layer = model.getGDX().getLayer(i);

					context.setDrawAllVertices(true);
					drawLayer(context, layer);
					if (context.isStopped()) {
						break;
					}
				}

				for (int i = model.getGDX().getLayerCount() - 1; i >= 0; i--) {
					ILayer layer = model.getGDX().getLayer(i);
					drawLabels(context, layer);
					if (context.isStopped()) {
						break;
					}
				}
			} else {
				for (int i = layers.size() - 1; i >= 0; i--) {
					ILayer layer = layers.get(i);
					drawLayer(context, layer);
					if (context.isStopped()) {
						break;
					}
				}

				for (int i = layers.size() - 1; i >= 0; i--) {
					ILayer layer = layers.get(i);
					drawLabels(context, layer);
					if (context.isStopped()) {
						break;
					}
				}
			}

			// anti-aliasing
			Object textAntiAlasing = context.getGraphics().getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
			Object graphicAntiAliasing = context.getGraphics().getRenderingHint(RenderingHints.KEY_ANTIALIASING);
			context.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					(textAntiAlasing == RenderingHints.VALUE_TEXT_ANTIALIAS_ON ? RenderingHints.VALUE_ANTIALIAS_ON
							: RenderingHints.VALUE_ANTIALIAS_OFF));

			context.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, graphicAntiAliasing);
		}
	}

	private void drawLayer(Context ctx, ILayer layer) {

		if (!layer.isVisible()) {
			return;
		}

		if (ctx.getEnvelope() == null) {
			return;
		}
		if (layer instanceof GroupLayer) {
			GroupLayer grouplayer = (GroupLayer) layer;

			ArrayList<ILayer> layers = new ArrayList<ILayer>();
			int cnt = grouplayer.getLayerCount();
			for (int i = 0; i < cnt; i++) {
				layers.add(grouplayer.getLayer(i));
			}
			if (isDrawableLayer(ctx, grouplayer)) {
				for (int i = layers.size() - 1; i >= 0; i--) {
					ILayer sublayer = layers.get(i);
					drawLayer(ctx, sublayer);
				}
			}
		} else if (isDrawableLayer(ctx, layer)) {
			if (layer instanceof FeatureLayer) {

				FeatureLayer fl = (FeatureLayer) layer;
				ScreenOffset sOffset = fl.getScreenOffset();
				if (sOffset != null) {
					ctx.getGraphics().translate(sOffset.getPixelOffsetX(ctx.getScale()),
							sOffset.getPixelOffsetY(ctx.getScale()));
				}

				fl.drawLayer(ctx);

				if (sOffset != null) {
					ctx.getGraphics().translate(sOffset.getPixelOffsetX(ctx.getScale()) * -1,
							sOffset.getPixelOffsetY(ctx.getScale()) * -1);
				}
			} else if (layer instanceof RasterLayer) {
			} else if (layer instanceof WMSLayer) {
				((WMSLayer) layer).drawLayer(ctx);
			} else if (layer instanceof TileMapLayer) {
				((TileMapLayer) layer).drawLayer(ctx);
			}
		}
	}

	private boolean isDrawableLayer(Context ctx, ILayer layer) {
		return layer.isVisible() && layer.getOpacity() > 0 && ctx.getScale() >= ((AbstractLayer) layer).getMinScale()
				&& ctx.getScale() < ((AbstractLayer) layer).getMaxScale() && ((AbstractLayer) layer).isDataUsable();
	}

	private void drawLabels(Context ctx, ILayer layer) {
		if (layer instanceof GroupLayer) {
			GroupLayer grouplayer = (GroupLayer) layer;
//			ArrayList<ILayer> layers = grouplayer.getLayers();
			ArrayList<ILayer> layers = new ArrayList<ILayer>();
			int cnt = grouplayer.getLayerCount();
			for (int i = 0; i < cnt; i++) {
				layers.add(grouplayer.getLayer(i));
			}

			if (isDrawableLayer(ctx, grouplayer)) {
				for (int i = 0; i < layers.size(); i++) {
					ILayer sublayer = layers.get(i);
					drawLabels(ctx, sublayer);
				}
			}
		} else {
			if (layer instanceof FeatureLayer) {
				if (isDrawableLayer(ctx, layer)) {
					((FeatureLayer) layer).drawLabel(ctx);
				}
			}
		}
	}

	public class TileGenCallable implements Callable<String> {

		private Context context;
		private Envelope envelope;
		private int level;
		private int xTileIndex;
		private int yTileIndex;
		private List<ILayer> Ilayers;
		private BufferedImage bi;

		public TileGenCallable(Context context, Envelope envelope, List<ILayer> Ilayers, BufferedImage bi, int level, int xTileIndex, int yTileIndex) {
			super();
			this.context = context;
			this.envelope = envelope;
			this.level = level;
			this.xTileIndex = xTileIndex;
			this.yTileIndex = yTileIndex;
			this.Ilayers = Ilayers;
			this.bi = bi;
		}

		public List<ILayer> getIlayers() {
			return Ilayers;
		}

		public void setIlayers(List<ILayer> ilayers) {
			Ilayers = ilayers;
		}

		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public Envelope getEnvelope() {
			return envelope;
		}

		public void setEnvelope(Envelope envelope) {
			this.envelope = envelope;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public int getxTileIndex() {
			return xTileIndex;
		}

		public void setxTileIndex(int xTileIndex) {
			this.xTileIndex = xTileIndex;
		}

		public int getyTileIndex() {
			return yTileIndex;
		}

		public void setyTileIndex(int yTileIndex) {
			this.yTileIndex = yTileIndex;
		}

		@Override
		public String call() throws Exception {
			if (isCancelled()) {
				return null;
			}
			try {
				double scale = envelope.getWidth() / mConfiguration.getTileWidth()
						/ DisplayConstant.STANDARD_PIXEL_SIZE_IN_METER;

//				context.setY(yTileIndex);
//				context.setX(xTileIndex);
//				context.setWidth(mConfiguration.getTileWidth());
//				context.setHeight(mConfiguration.getTileHeight());
//				context.setScale(mConfiguration.getLevels()[level].scale);
				context.setEnvelope(envelope);

				System.out.println(context.getWidth() + ":" +context.getHeight()  + " Scale: " + context.getScale() + " BBOX: " + context.getEnvelope() + " X: " + context.getX() + " Y: " + context.getY());
				getMapImage(context, Ilayers);

				File tileFile = getTileFile(mConfiguration, level, xTileIndex, yTileIndex);
				save(bi, tileFile, mConfiguration.getOutputTypeAsString());

				amountSync();
				updateMessage(I18N.getText("Msg_GenTileProcess") + " " + ": Amount " + count + "/" + totalWork);
				System.out.println(Thread.currentThread().getName() + "> Level:" + level + " X:" + xTileIndex + " Y:" + yTileIndex);
			} catch (Throwable t) {
				System.err.println("ERROR [Level:" + level + " X:" + xTileIndex + " Y:" + yTileIndex + "]");				
				t.printStackTrace(System.err);
			}
			finally {
	              lock.release();
	        }
			return Thread.currentThread().getName();
		}

	};
}