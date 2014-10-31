package org.jackey.MavenBuildOrder;

public class MavenModule {
	private String groupId;
	private String artifactId;
	private String path;
	private String name;
	private String version;
	private boolean isBuilt = false;

	public MavenModule() {

	}

	public MavenModule(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBuilt() {
		return isBuilt;
	}

	public void setBuilt(boolean isBuilt) {
		this.isBuilt = isBuilt;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MavenModule other = (MavenModule) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return groupId + " " + artifactId + " " + version;
	}

	public void clone(MavenModule module) {
		this.groupId = module.getGroupId() == null ? null : module.getGroupId();
		this.artifactId = module.getArtifactId() == null ? null : module
				.getArtifactId();
		this.path = module.getPath() == null ? null : module.getPath();
		this.version = module.getVersion() == null ? null : module.getVersion();
		this.name = module.getName() == null ? null : module.getName();
		this.isBuilt = module.isBuilt;
	}

}
