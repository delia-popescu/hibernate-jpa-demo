package com.training.hibernateDemo.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.training.hibernateDemo.util.Status;

@Entity
@Table(name = "project")
public class Project {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="projectSequence")
	@SequenceGenerator(name = "projectSequence", sequenceName = "GLOBAL_SEQUENCE", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "NAME", length = 50, nullable = false)
	private String name;
	
	private String description;
	
	private Status status;
	
	@Transient
	private String metaInfo;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "project")
	List<Task> tasks;

	public Long getId() {
		return id;
	}

	public Project setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Project setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name = "DESCRIPTION", length = 255, nullable = false)
	public String getDescription() {
		return description;
	}

	public Project setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getMetaInfo() {
		return metaInfo;
	}

	public Project setMetaInfo(String metaInfo) {
		this.metaInfo = metaInfo;
		return this;
	}
	
	public Status getStatus() {
		return status;
	}

	public Project setStatus(Status status) {
		this.status = status;
		return this;
	}

	public List<Task> getTasks() {
		return tasks;
	}
	
	public Project setTasks(List<Task> tasks) {
		this.tasks = tasks;
		return this;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", description=" + description + ", status=" + status
				+ ", tasks=" + tasks + "]";
	}
}
