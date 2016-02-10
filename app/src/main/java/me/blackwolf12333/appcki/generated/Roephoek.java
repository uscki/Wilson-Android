package me.blackwolf12333.appcki.generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class Roephoek {

    @SerializedName("totalElements")
    @Expose
    private Integer totalElements;
    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;
    @SerializedName("sort")
    @Expose
    private Sort sort;
    @SerializedName("numberOfElements")
    @Expose
    private Integer numberOfElements;
    @SerializedName("first")
    @Expose
    private Boolean first;
    @SerializedName("last")
    @Expose
    private Boolean last;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("content")
    @Expose
    private List<RoephoekItem> content = new ArrayList<RoephoekItem>();

    /**
     *
     * @return
     * The totalElements
     */
    public Integer getTotalElements() {
        return totalElements;
    }

    /**
     *
     * @param totalElements
     * The totalElements
     */
    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    /**
     *
     * @return
     * The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     *
     * @param totalPages
     * The totalPages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     *
     * @return
     * The sort
     */
    public Sort getSort() {
        return sort;
    }

    /**
     *
     * @param sort
     * The sort
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     *
     * @return
     * The numberOfElements
     */
    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    /**
     *
     * @param numberOfElements
     * The numberOfElements
     */
    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    /**
     *
     * @return
     * The first
     */
    public Boolean getFirst() {
        return first;
    }

    /**
     *
     * @param first
     * The first
     */
    public void setFirst(Boolean first) {
        this.first = first;
    }

    /**
     *
     * @return
     * The last
     */
    public Boolean getLast() {
        return last;
    }

    /**
     *
     * @param last
     * The last
     */
    public void setLast(Boolean last) {
        this.last = last;
    }

    /**
     *
     * @return
     * The size
     */
    public Integer getSize() {
        return size;
    }

    /**
     *
     * @param size
     * The size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     *
     * @return
     * The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     *
     * @param number
     * The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     *
     * @return
     * The content
     */
    public List<RoephoekItem> getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(List<RoephoekItem> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}