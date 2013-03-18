package compress;

import java.io.Serializable;

/**
 * js/jsx,css压缩的实体
 * 
 * @author fangzhibin
 * @since 2.2
 */
public class Compress implements Serializable {

	private static final long serialVersionUID = 4866126713739602161L;
	private String id;
	private String type;
	private String includes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIncludes() {
		return includes;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

}
