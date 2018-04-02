package com.db.hibernateDemo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="employeeSequence")
	@SequenceGenerator(name = "employeeSequence", sequenceName = "GLOBAL_SEQUENCE", initialValue = 1, allocationSize = 1)
	private Long id;
	
	private String name;
	
	private String surname;
	
	@ManyToOne
	@JoinColumn(name = "DEPARTAMENT_ID", referencedColumnName = "ID")
	private Departament departament;

	public Long getId() {
		return id;
	}

	public Employee setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Employee setName(String name) {
		this.name = name;
		return this;
	}

	public String getSurname() {
		return surname;
	}

	public Employee setSurname(String surname) {
		this.surname = surname;
		return this;
	}
	
	public Departament getDepartament() {
		return departament;
	}

	public Employee setDepartament(Departament departament) {
		this.departament = departament;
		return this;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", surname=" + surname + ", departament=" + departament + "]";
	}
}
