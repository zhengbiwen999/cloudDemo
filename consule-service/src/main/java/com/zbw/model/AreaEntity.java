package com.zbw.model;

import javax.persistence.*;

@Table(name = "tbarea")
public class AreaEntity {
    @Id
    @Column(name = "fiCityId")
    private Integer ficityid;

    @Column(name = "fiDistrictId")
    private Integer fidistrictid;

    @Column(name = "fsDistrictName")
    private String fsdistrictname;

    @Column(name = "fsAreaSort")
    private String fsareasort;

    /**
     * @return fiCityId
     */
    public Integer getFicityid() {
        return ficityid;
    }

    /**
     * @param ficityid
     */
    public void setFicityid(Integer ficityid) {
        this.ficityid = ficityid;
    }

    /**
     * @return fiDistrictId
     */
    public Integer getFidistrictid() {
        return fidistrictid;
    }

    /**
     * @param fidistrictid
     */
    public void setFidistrictid(Integer fidistrictid) {
        this.fidistrictid = fidistrictid;
    }

    /**
     * @return fsDistrictName
     */
    public String getFsdistrictname() {
        return fsdistrictname;
    }

    /**
     * @param fsdistrictname
     */
    public void setFsdistrictname(String fsdistrictname) {
        this.fsdistrictname = fsdistrictname;
    }

    /**
     * @return fsAreaSort
     */
    public String getFsareasort() {
        return fsareasort;
    }

    /**
     * @param fsareasort
     */
    public void setFsareasort(String fsareasort) {
        this.fsareasort = fsareasort;
    }
}