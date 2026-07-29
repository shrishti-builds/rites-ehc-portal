package com.rites.ehc.dto;

import java.io.Serializable;

public class DependentDto implements Serializable {
    private String name;
    private String relation;
    private String dob;
    private String gender;

    public DependentDto() {}

    public DependentDto(String name, String relation, String dob, String gender) {
        this.name = name;
        this.relation = relation;
        this.dob = dob;
        this.gender = gender;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
