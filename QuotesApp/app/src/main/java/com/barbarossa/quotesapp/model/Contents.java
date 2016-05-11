package com.barbarossa.quotesapp.model;

/**
 * Created by Ioan on 11.05.2016.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Contents {

    private String quote;
    private String author;
    private String id;
    private String requestedCategory;
    private List<String> categories = new ArrayList<String>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The quote
     */
    public String getQuote() {
        return quote;
    }

    /**
     *
     * @param quote
     * The quote
     */
    public void setQuote(String quote) {
        this.quote = quote;
    }

    /**
     *
     * @return
     * The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     * The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The requestedCategory
     */
    public String getRequestedCategory() {
        return requestedCategory;
    }

    /**
     *
     * @param requestedCategory
     * The requested_category
     */
    public void setRequestedCategory(String requestedCategory) {
        this.requestedCategory = requestedCategory;
    }

    /**
     *
     * @return
     * The categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     *
     * @param categories
     * The categories
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
