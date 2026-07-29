package com.rites.ehc.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequestDto implements Serializable {
    private String ehcId;
    private String empNo;
    private String empName;
    private String designation;
    private String division;
    private String mobile;
    private String landline;
    private String puHead;
    private String state;
    private String city;
    private String hospitalName;
    private String status;
    private String remarks;
    private String submissionDate;
    private Object billDetails;
    private String financeRemarks;
    private Object disbursementDetails;
    private List<DependentDto> dependents = new ArrayList<>();

    public RequestDto() {}

    public String getEhcId() { return ehcId; }
    public void setEhcId(String ehcId) { this.ehcId = ehcId; }

    public String getEmpNo() { return empNo; }
    public void setEmpNo(String empNo) { this.empNo = empNo; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getLandline() { return landline; }
    public void setLandline(String landline) { this.landline = landline; }

    public String getPuHead() { return puHead; }
    public void setPuHead(String puHead) { this.puHead = puHead; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(String submissionDate) { this.submissionDate = submissionDate; }

    public Object getBillDetails() { return billDetails; }
    public void setBillDetails(Object billDetails) { this.billDetails = billDetails; }

    public String getFinanceRemarks() { return financeRemarks; }
    public void setFinanceRemarks(String financeRemarks) { this.financeRemarks = financeRemarks; }

    public Object getDisbursementDetails() { return disbursementDetails; }
    public void setDisbursementDetails(Object disbursementDetails) { this.disbursementDetails = disbursementDetails; }

    public List<DependentDto> getDependents() { return dependents; }
    public void setDependents(List<DependentDto> dependents) { this.dependents = dependents; }
}
