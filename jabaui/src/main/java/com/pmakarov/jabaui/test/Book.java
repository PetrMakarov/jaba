package com.pmakarov.jabaui.test;

import com.pmakarov.jabaui.boot.metadata.JabaModelXml;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.Date;

@Data
@NoArgsConstructor
@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "name", "date"})
@JabaModelXml(resource = "${LOCALAPPDATA}/jabatest/testEntity.xml")
public class Book {

    @XmlAttribute
    private Long id;
    @XmlElement(name = "title")
    private String name;
    @XmlTransient
    private String author;
    private Date date;

    public Book(Date date) {
        this.date = date;
    }

}
