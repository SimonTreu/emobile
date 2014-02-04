package de.dfki.gs.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: glenn
 * @since: 04.12.13
 */
public class ThreadSaveList<T> {

    private List<T> objectList;


    public ThreadSaveList() {

        objectList = new ArrayList<T>();

    }

    synchronized public void addElement( T element ) {

        objectList.add( element );

    }

    public List<T> getList() {
        return objectList;
    }

}
