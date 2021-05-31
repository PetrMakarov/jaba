
package com.pmakarov.jabaui.test;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for local complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="local">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="forwardPort" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "local", propOrder = {

})
@ToString
public class Local {

    @XmlElement(required = true)
    protected Integer forwardPort = 22;

    /**
     * Gets the value of the forwardPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getForwardPort() {
        return forwardPort;
    }

    /**
     * Sets the value of the forwardPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForwardPort(Integer value) {
        this.forwardPort = value;
    }

}
