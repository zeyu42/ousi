package org.ousi.ousi;

import java.io.Serializable;

public class Vertex implements Serializable {
    /*
    Vertex class.
     */

    static private long count = 0;

    private long id;

    Vertex() {
        id = count++;
    }

    public long getId() {
        return id;
    }

}
