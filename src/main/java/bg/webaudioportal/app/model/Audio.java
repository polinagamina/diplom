package bg.webaudioportal.app.model;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table(name = "audio")
public class Audio {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "uploader")
	private String uploader;
	
	@Column(name = "size")
	private double size;
	
	@Column(name = "date")
	private String date;
	
	@Column(name = "path")
	private String path;
	
	@Column(name = "active")
	private boolean active;



	@Column(name = "contents")
	@Lob
	private byte[] audioFile;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public byte[] getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(byte[] audioFile) {
		this.audioFile = audioFile;
	}
}