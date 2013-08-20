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
package ro.nextreports.engine.template;

import java.io.ObjectStreamException;
import java.io.Serializable;

import ro.nextreports.engine.band.BandElement;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jan 22, 2007
 * Time: 5:42:36 PM
 */
public class ReportTemplate implements Serializable {

    private static final long serialVersionUID = 1083505613100046061L;

    private BandElement titleBand;
    private BandElement headerBand;
    private BandElement detailBand;
    private BandElement footerBand;
    private String version;
    
    public ReportTemplate() {
    }

    public BandElement getTitleBand() {
        return titleBand;
    }

    public void setTitleBand(BandElement titleBand) {
        this.titleBand = titleBand;
    }

    public BandElement getHeaderBand() {
        return headerBand;
    }

    public void setHeaderBand(BandElement headerBand) {
        this.headerBand = headerBand;
    }

    public BandElement getDetailBand() {
        return detailBand;
    }

    public void setDetailBand(BandElement detailBand) {
        this.detailBand = detailBand;
    }        

    public BandElement getFooterBand() {
		return footerBand;
	}

	public void setFooterBand(BandElement footerBand) {
		this.footerBand = footerBand;
	}

	public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    private Object readResolve() throws ObjectStreamException {
        // Read/initialize additional fields
        if (footerBand == null)  {
        	footerBand = new BandElement("");
        }
        
        return this;
      }
}
