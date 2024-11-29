package dev.nhoxtam151.shopmecommon.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 128, nullable = false, unique = true)
    private String name;
    @Column(length = 64, nullable = false, unique = true)
    private String alias;
    private String photo;
    private boolean enabled;
    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<Category> children = new HashSet<>();

    public Category(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public Category(Long id) {
        this.id = id;
    }

    public Category(String name, String alias, Category parent) {
        this.name = name;
        this.alias = alias;
        this.parent = parent;
    }

    public Category() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
