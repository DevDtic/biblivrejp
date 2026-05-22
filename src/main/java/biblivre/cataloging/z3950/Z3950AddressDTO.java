package biblivre.cataloging.z3950;

import biblivre.core.AbstractDTO;
import java.io.Serial;

public class Z3950AddressDTO extends AbstractDTO {
    @Serial private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String url;
    private Integer port;
    private String collection;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getCollection() {
        return this.collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
