package com.training.hibernateDemo.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class Departament {	
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="departamentSequence")
	@SequenceGenerator(name = "departamentSequence", sequenceName = "GLOBAL_SEQUENCE", initialValue = 1, allocationSize = 1)
	private Long id;
	
	private String name;
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy="departament")
	List<Employee> employees;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Departament setName(String name) {
		this.name = name;
		return this;
	}
	
	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	@Override
	public String toString() {
		return "Departament [id=" + id + ", name=" + name + "]";
	}
}
