package com.manywho.sdk.entities.run.elements.type;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ListFilterMinimal {
    private String comparisonType;
    private ListFilterWhereCollection where;
    private List<ListFilterMinimal> listFilters;

    public String getComparisonType() {
        return comparisonType;
    }

    public ListFilterMinimal setComparisonType(String comparisonType) {
        this.comparisonType = comparisonType;
        return this;
    }

    public ListFilterWhereCollection getWhere() {
        return where;
    }

    public boolean hasWhere() {
        return CollectionUtils.isNotEmpty(where);
    }

    public ListFilterMinimal setWhere(ListFilterWhereCollection where) {
        this.where = where;
        return this;
    }

    public List<ListFilterMinimal> getListFilters() {
        return listFilters;
    }

    public boolean hasListFilters() {
        return CollectionUtils.isNotEmpty(listFilters);
    }

    public ListFilterMinimal setListFilters(List<ListFilterMinimal> listFilters) {
        this.listFilters = listFilters;
        return this;
    }
}
