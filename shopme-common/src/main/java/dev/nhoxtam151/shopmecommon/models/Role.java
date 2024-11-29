package dev.nhoxtam151.shopmecommon.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@Column(length = 100, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType name;
    @Column(length = 150, nullable = false)
    private String description;

    public Role() {
    }

    public Role(Long id) {
        this.id = id;
    }

    public Role(RoleType name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public RoleType getName() {
        return name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public enum RoleType {
        ADMIN, SALESPERSON, EDITOR, SHIPPER, ASSISTANT, TESTER, USER;
    }
}
