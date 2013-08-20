/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.engine.util.chart;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * A custom renderer that draws cylinders to represent data from a
 * CategoryDataset in a CategoryPlot.
 */
public class CylinderRenderer extends BarRenderer3D {

	/**
	 * Default constructor.
	 */
	public CylinderRenderer() {
		super();
	}

	/**
	 * Creates a new renderer.
	 * 
	 * @param xOffset the x-offset for the 3D effect.
	 * @param yOffset the y-offset for the 3D effect.
	 */
	public CylinderRenderer(double xOffset, double yOffset) {
		super(xOffset, yOffset);
	}

	/**
	 * Draws a cylinder to represent one data item.
	 * 
	 * @param g2 the graphics device.
	 * @param state the renderer state.
	 * @param dataArea the area for plotting the data.
	 * @param plot the plot.
	 * @param domainAxis the domain axis.
	 * @param rangeAxis the range axis.
	 * @param dataset the dataset.
	 * @param row the row index (zero-based).
	 * @param column the column index (zero-based).
	 * @param pass the pass index.
	 */
	public void drawItem(Graphics2D g2, CategoryItemRendererState state,
			Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
			ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
			int pass) {

		// check the value we are plotting...
		Number dataValue = dataset.getValue(row, column);
		if (dataValue == null) {
			return;
		}

		double value = dataValue.doubleValue();

		Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + getYOffset(), 
				dataArea.getWidth()	- getXOffset(), dataArea.getHeight() - getYOffset());

		PlotOrientation orientation = plot.getOrientation();

		double barW0 = calculateBarW0(plot, orientation, adjusted, domainAxis, state, row, column);
		double[] barL0L1 = calculateBarL0L1(value);
		if (barL0L1 == null) {
			return; // the bar is not visible
		}

		RectangleEdge edge = plot.getRangeAxisEdge();
		float transL0 = (float) rangeAxis.valueToJava2D(barL0L1[0], adjusted, edge);
		float transL1 = (float) rangeAxis.valueToJava2D(barL0L1[1], adjusted, edge);
		float barL0 = Math.min(transL0, transL1);
		float barLength = Math.abs(transL1 - transL0);

		// draw the bar...
		GeneralPath bar = new GeneralPath();
		if (orientation == PlotOrientation.HORIZONTAL) {
			bar.moveTo(barL0, (float) barW0);
			bar.lineTo(barL0, (float) (barW0 + state.getBarWidth()));
			bar.lineTo(barL0 + barLength, (float) (barW0 + state.getBarWidth()));
			bar.lineTo(barL0 + barLength, (float) barW0);
			bar.closePath();
		} else {
			bar.moveTo((float) barW0, (float) (barL0 - getYOffset() / 2));
			bar.lineTo((float) barW0, (float) (barL0 + barLength - getYOffset() / 2));
			Arc2D arc = new Arc2D.Double(barW0,	(barL0 + barLength - getYOffset()), state.getBarWidth(),
					getYOffset(), 180, 180, Arc2D.OPEN);
			bar.append(arc, true);
			bar.lineTo((float) (barW0 + state.getBarWidth()), (float) (barL0 - getYOffset() / 2));
			arc = new Arc2D.Double(barW0, (barL0 - getYOffset()), state.getBarWidth(), getYOffset(), 0, -180, Arc2D.OPEN);
			bar.append(arc, true);
			bar.closePath();
		}
		Paint itemPaint = getItemPaint(row, column);
		if (getGradientPaintTransformer() != null && itemPaint instanceof GradientPaint) {
			GradientPaint gp = (GradientPaint) itemPaint;
			itemPaint = getGradientPaintTransformer().transform(gp, bar);
		}
		g2.setPaint(itemPaint);
		g2.fill(bar);

		Shape bar3dTop = new Ellipse2D.Double(barW0, barL0 - getYOffset(), state.getBarWidth(), getYOffset());
		if (itemPaint instanceof GradientPaint) {
			g2.setPaint(((GradientPaint) itemPaint).getColor2());
		}
		g2.fill(bar3dTop);

		if (isDrawBarOutline() && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
			g2.setStroke(getItemOutlineStroke(row, column));
			g2.setPaint(getItemOutlinePaint(row, column));
			g2.draw(bar);
			if (bar3dTop != null) {
				g2.draw(bar3dTop);
			}
		}

		CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
		if (generator != null && isItemLabelVisible(row, column)) {
			drawItemLabel(g2, dataset, row, column, plot, generator, bar.getBounds2D(), (value < 0.0));
		}

		// collect entity and tool tip information...
		if (state.getInfo() != null) {
			EntityCollection entities = state.getEntityCollection();
			if (entities != null) {

				String tip = null;
				CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
				if (tipster != null) {
					tip = tipster.generateToolTip(dataset, row, column);
				}
				String url = null;
				if (getItemURLGenerator(row, column) != null) {
					url = getItemURLGenerator(row, column).generateURL(dataset,	row, column);
				}
				CategoryItemEntity entity = new CategoryItemEntity(bar.getBounds2D(), tip, url, dataset,
						dataset.getRowKey(row), dataset.getColumnKey(column));
				entities.add(entity);
			}
		}

	}

}
