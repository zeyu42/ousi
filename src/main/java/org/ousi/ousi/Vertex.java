package org.ousi.ousi;

import java.io.Serializable;

public class Vertex implements Serializable {
    /*
    Vertex class.
     */

    static private long count = 0;

    private long id;
    private String label;
    private String description;

    Vertex() {
        id = count++;
        label = "";
        description = "";
    }

    Vertex(String label) {
        id = count++;
        this.label = label;
        description = "";
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
