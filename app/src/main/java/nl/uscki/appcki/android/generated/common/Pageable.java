package nl.uscki.appcki.android.generated.common;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter on 2/14/16.
 */
public class Pageable<T> {
    @Expose
    private List<T> content = new ArrayList<>();
    @Expose
    private Integer totalElements;
    @Expose
    private Integer totalPages;
    @Expose
    private Sort sort;
    @Expose
    private Integer numberOfElements;
    @Expose
    private Boolean first;
    @Expose
    private Boolean last;
    @Expose
    private Integer size;
    @Expose
    private Integer number;

    /**
     *
     * @return
     *     The totalElements
     */
    public Integer getTotalElements() {
        return totalElements;
    }

    /**
     *
     * @param totalElements
     *     The totalElements
     */
    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    /**
     *
     * @return
     *     The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     *
     * @param totalPages
     *     The totalPages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     *
     * @return
     *     The sort
     */
    public Sort getSort() {
        return sort;
    }

    /**
     *
     * @param sort
     *     The sort
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     *
     * @return
     *     The numberOfElements
     */
    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    /**
     *
     * @param numberOfElements
     *     The numberOfElements
     */
    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    /**
     *
     * @return
     *     The first
     */
    public Boolean getFirst() {
        return first;
    }

    /**
     *
     * @param first
     *     The first
     */
    public void setFirst(Boolean first) {
        this.first = first;
    }

    /**
     *
     * @return
     *     The last
     */
    public Boolean getLast() {
        return last;
    }

    /**
     *
     * @param last
     *     The last
     */
    public void setLast(Boolean last) {
        this.last = last;
    }

    /**
     *
     * @return
     *     The size
     */
    public Integer getSize() {
        return size;
    }

    /**
     *
     * @param size
     *     The size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     *
     * @return
     *     The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     *
     * @param number
     *     The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     *
     * @return
     *     The content
     */
    public List<T> getContent() {
        return content;
    }

    /**
     *
     * @param content
     *     The content
     */
    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
