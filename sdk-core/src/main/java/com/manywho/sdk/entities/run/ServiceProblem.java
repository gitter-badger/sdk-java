package com.manywho.sdk.entities.run;

import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.enums.ProblemKind;

public class ServiceProblem extends ApiProblem {
    private InvokeType invokeType;
    private String action;

    public ServiceProblem() {
    }

    public ServiceProblem(ServiceProblemException serviceProblemException) {
        this(serviceProblemException, serviceProblemException.getInvokeType(), serviceProblemException.getAction());
    }

    public ServiceProblem(ApiProblemException apiProblemException, InvokeType invokeType, String action) {
        super(ProblemKind.Service, apiProblemException);
        this.invokeType = invokeType;
        this.action = action;
    }

    public ServiceProblem(String uri, int statusCode, String message) {
        super(ProblemKind.Service, uri, statusCode, message);
    }

    public InvokeType getInvokeType() {
        return invokeType;
    }

    public String getAction() {
        return action;
    }
}
