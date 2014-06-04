package messages;
import java.io.Serializable;

public class FileDescription implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;
	private long totalLength;
	private long fragmentLength;
	
	public FileDescription(String fileName, long totalLength, long fragmentLength) {
		this.setFileName(fileName);
		this.setTotalLength(totalLength);
		this.setFragmentLength(fragmentLength);
	}
	
	public FileDescription(String fileName) {
		this.setFileName(fileName);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	public long getFragmentLength() {
		return fragmentLength;
	}

	public void setFragmentLength(long fragmentLength) {
		this.fragmentLength = fragmentLength;
	}
}
