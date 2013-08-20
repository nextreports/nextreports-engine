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
package ro.nextreports.engine.chart;

class YRange {
	
	private Number min;
	private Number max;
	private int step;
	
	public YRange(Number min, Number max) {
		super();
		this.min = min;
		this.max = max;
	}	
	
	private YRange(Number min, Number max, int step) {
		super();
		this.min = min;
		this.max = max;
		this.step = step;
	}		
	
	public Number getMin() {
		return min;
	}

	public Number getMax() {
		return max;
	}

	public int getStep() {
		return step;
	}

	private int getStep(Number min, Number max) {
        int step;
        if (!min.equals(max)) {
            step = (int) Math.ceil((max.doubleValue() - min.doubleValue()) / 10);
        } else {
            step = (int) (max.doubleValue() / 10);
        }
        if (step == 0) {
            step = 1;
        }
        // step can be any number from 1 to 10 or a multiple of 10
        if (step > 10) {
            step = (int) Math.ceil(((double) step) / 10) * 10;
        }
        return step;
    }
    
    private double getDelta(Number min, Number max, int step) {
        double delta;
        if ((max.doubleValue() - min.doubleValue()) > step) {
            delta = step;
        } else if ((max.doubleValue() - min.doubleValue()) > 0) {
            delta = max.doubleValue() - min.doubleValue();
            // do not allow delta to be to small (if there is only one value on Y axis,
            // the line grid can be under the X axis
            if (delta < 0.13) {
                delta = 0.13;
            }
        } else {
            delta = 1;
        }
        return delta;
    }
    
    public YRange update() {
    	
    	Number min = this.min;
    	Number max = this.max;
    	
    	int step = getStep(min, max);    	
        // adjust min and max with a delta
        double delta = getDelta(min, max, step);
        double minAbs = Math.abs(min.doubleValue());
        if (minAbs > delta) {
            min = min.doubleValue() - delta;           
        } else {
            min = 0;
        }
        max = max.doubleValue() + delta;        
        return new YRange(min, max, step);
    }        		

}
