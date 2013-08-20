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
package ro.nextreports.engine.band;

import ro.nextreports.engine.util.ReportUtil;

/**
 * User: mihai.panaitescu
 * Date: 02-Dec-2009
 * Time: 11:17:54
 */
public class ImageBandElement extends BandElement {

    protected String image;
    protected Integer width;
    protected Integer height;

    public ImageBandElement(String image) {
        super("$I{" + ReportUtil.getFileName(image) + "}");
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;     
        setText("$I{" + ReportUtil.getFileName(image) + "}");
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public boolean isScaled() {
        return (width != null) && (width.intValue() > 0) && (height != null) && (height.intValue() > 0);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ImageBandElement that = (ImageBandElement) o;

        if (height != null ? !height.equals(that.height) : that.height != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        if (width != null ? !width.equals(that.width) : that.width != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (width != null ? width.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        return result;
    }
}
