package ro.nextreports.engine.band;

/**
 * Image stored as blob in database column 
 * 
 * @author Mihai Dinca-Panaitescu
 * @date 07.04.2014
 */
public class ImageColumnBandElement extends ColumnBandElement {

	protected Integer width;
	protected Integer height;

	public ImageColumnBandElement(String column) {
		super(column);
		setText("$IC{" + column + "}");
	}
	
	public void setColumn(String column) {
		super.setColumn(column);
		setText("$IC{" + column + "}");
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
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		ImageColumnBandElement that = (ImageColumnBandElement) o;

		if (height != null ? !height.equals(that.height) : that.height != null)
			return false;
		if (width != null ? !width.equals(that.width) : that.width != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (width != null ? width.hashCode() : 0);
		result = 31 * result + (height != null ? height.hashCode() : 0);
		return result;
	}

}
