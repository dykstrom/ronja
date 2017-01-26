/*
 * Copyright (C) 2016 Johan Dykstrom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.dykstrom.ronja.common.book;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class corresponds to the XML structure in the opening book file.
 * It is used by JAXB when loading the file.
 */
@XmlRootElement(name="move")
public class XmlBookMove {

    private String can = "";
    private int weight = 0;
    private String name;
    private List<XmlBookMove> subMoves = new ArrayList<>();

    @Override
    public String toString() {
        return "[" + can + ", " + weight + ", " + name + ", " + subMoves + "]";
    }

    @XmlAttribute(name="can", required=true)
    public void setCan(String can) {
        this.can = can;
    }

    public String getCan() {
        return can;
    }

    @XmlAttribute(name="weight", required=true)
    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    @XmlAttribute(name="name")
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @XmlElement(name="move")
    public void setSubMoves(List<XmlBookMove> subMoves) {
        this.subMoves = subMoves;
    }

    public List<XmlBookMove> getSubMoves() {
        return subMoves;
    }
}
