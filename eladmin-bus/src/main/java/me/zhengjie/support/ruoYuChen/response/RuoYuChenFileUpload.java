package me.zhengjie.support.ruoYuChen.response;

public class RuoYuChenFileUpload {
    /**
     * 附件名
     */
    private String name;
    /**
     * 附件大小 字节
     */
    private String size;
    /**
     * 附件后缀
     */
    private String suffix;
    /**
     * 附件全链接
     */
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
