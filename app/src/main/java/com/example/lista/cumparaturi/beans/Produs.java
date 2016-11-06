package com.example.lista.cumparaturi.beans;

import com.example.lista.cumparaturi.ContainerDate;
import com.example.lista.cumparaturi.Utils;

import static com.example.lista.cumparaturi.Utils.*;

/**
 * Created by macbookproritena on 11/5/16.
 */

public class Produs {
    private final int id;
    private final String name;
    private final String desc;

    public Produs(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public Produs(String name) {
        this.name = name;
        this.id = defaultProdusId;
        this.desc = preventNullString;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Produs produs = (Produs) o;

        if (id != produs.id) return false;
        if (!name.equals(produs.name)) return false;
        return desc.equals(produs.desc);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + desc.hashCode();
        return result;
    }
}
