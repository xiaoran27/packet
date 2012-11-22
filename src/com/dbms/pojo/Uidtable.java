package com.dbms.pojo;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class Uidtable implements Serializable {

    /** identifier field */
    private String tablename;

    /** nullable persistent field */
    private Long nextuid;

    /** full constructor */
    public Uidtable(String tablename, Long nextuid) {
        this.tablename = tablename;
        this.nextuid = nextuid;
    }

    /** default constructor */
    public Uidtable() {
    }

    /** minimal constructor */
    public Uidtable(String tablename) {
        this.tablename = tablename;
    }

    public String getTablename() {
        return this.tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public Long getNextuid() {
        return this.nextuid;
    }

    public void setNextuid(Long nextuid) {
        this.nextuid = nextuid;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("tablename", getTablename())
            .append("nextuid", getNextuid())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof Uidtable) ) return false;
        Uidtable castOther = (Uidtable) other;
        return new EqualsBuilder()
            .append(this.getTablename(), castOther.getTablename())
            .append(this.getNextuid(), castOther.getNextuid())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTablename())
            .append(getNextuid())
            .toHashCode();
    }

}
