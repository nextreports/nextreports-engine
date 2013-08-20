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

import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.awt.Shape;

public class Star {
    public Star(float x, float y) {
        start = new Point2D.Float(x, y);                      // store start point
        createStar();
    }

    // Create the path from start
    private void createStar() {
        Point2D.Float point = start;
        p = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        p.moveTo(point.x, point.y);
        p.lineTo(point.x + 3.0f, point.y - 1.5f);            // Line from start to A
        point = (Point2D.Float) p.getCurrentPoint();
        p.lineTo(point.x + 1.5f, point.y - 3.0f);            // Line from A to B
        point = (Point2D.Float) p.getCurrentPoint();
        p.lineTo(point.x + 1.5f, point.y + 3.0f);            // Line from B to C
        point = (Point2D.Float) p.getCurrentPoint();
        p.lineTo(point.x + 3.0f, point.y + 1.5f);            // Line from C to D
        point = (Point2D.Float) p.getCurrentPoint();
        p.lineTo(point.x - 3.0f, point.y + 1.5f);            // Line from D to E
        point = (Point2D.Float) p.getCurrentPoint();
        p.lineTo(point.x - 1.5f, point.y + 3.0f);            // Line from E to F
        point = (Point2D.Float) p.getCurrentPoint();
        p.lineTo(point.x - 1.5f, point.y - 3.0f);            // Line from F to g
        p.closePath();                                        // Line from G to start
    }

    // Modify the location of this star
    Shape atLocation(float x, float y) {
        start.setLocation(x, y);                              // Store new start
        p.reset();                                            // Erase current path
        createStar();                                         // create new path
        return p;                                             // Return the path
    }

    // Make the path available
    public Shape getShape() {
        return p;
    }

    private Point2D.Float start;                           // Start point for star
    private GeneralPath p;                                 // Star path
}
