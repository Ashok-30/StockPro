package com.stockpro.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;

    @OneToMany(mappedBy = "store")
    private List<User> users;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	

   
}
