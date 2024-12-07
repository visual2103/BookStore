package org.example.repository.book;

import java.util.*;

public class Cache<T> {
    public List<T> storage ;
    public List<T> load(){
        return storage ;
    }

    public void save(List<T> storage){
        this.storage = storage ;
    }
    public boolean hasResult(){
        return storage != null ;
    }
    public void invalidateCache(){
        storage = null ;
    }

}
