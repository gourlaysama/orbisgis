//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-600 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.07.10 at 05:39:41 PM CEST 
//


package org.orbisgis.renderer.legend.carto.persistence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.orbisgis.renderer.symbol.collection.persistence.SymbolType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="symbol" type="{org.orbisgis.symbol}symbol-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="init-value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="init-included" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="end-value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="end-included" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="label" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "symbol"
})
@XmlRootElement(name = "interval-classification")
public class IntervalClassification {

    @XmlElement(required = true)
    protected SymbolType symbol;
    @XmlAttribute(name = "init-value")
    protected String initValue;
    @XmlAttribute(name = "init-included")
    protected Boolean initIncluded;
    @XmlAttribute(name = "end-value")
    protected String endValue;
    @XmlAttribute(name = "end-included")
    protected Boolean endIncluded;
    @XmlAttribute(required = true)
    protected String label;

    /**
     * Gets the value of the symbol property.
     * 
     * @return
     *     possible object is
     *     {@link SymbolType }
     *     
     */
    public SymbolType getSymbol() {
        return symbol;
    }

    /**
     * Sets the value of the symbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link SymbolType }
     *     
     */
    public void setSymbol(SymbolType value) {
        this.symbol = value;
    }

    /**
     * Gets the value of the initValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitValue() {
        return initValue;
    }

    /**
     * Sets the value of the initValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitValue(String value) {
        this.initValue = value;
    }

    /**
     * Gets the value of the initIncluded property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInitIncluded() {
        return initIncluded;
    }

    /**
     * Sets the value of the initIncluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInitIncluded(Boolean value) {
        this.initIncluded = value;
    }

    /**
     * Gets the value of the endValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndValue() {
        return endValue;
    }

    /**
     * Sets the value of the endValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndValue(String value) {
        this.endValue = value;
    }

    /**
     * Gets the value of the endIncluded property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEndIncluded() {
        return endIncluded;
    }

    /**
     * Sets the value of the endIncluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEndIncluded(Boolean value) {
        this.endIncluded = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

}