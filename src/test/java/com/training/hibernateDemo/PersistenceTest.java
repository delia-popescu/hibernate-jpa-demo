package com.training.hibernateDemo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.training.hibernateDemo.model.Departament;
import com.training.hibernateDemo.model.Employee;
import com.training.hibernateDemo.model.Project;
import com.training.hibernateDemo.model.Task;
import com.training.hibernateDemo.util.Status;


public class PersistenceTest {

	private EntityManager em = PersistenceManager.INSTANCE.getEntityManager();

	@Before
	public void setup() {
		cleanupDatabase();
		populateDatabase();
	}

	@After
	public void tearDown() {
		em.close();
	}

	private void cleanupDatabase() {
		executeInEntityManagerTransaction(em, () -> {
			Query deleteEmpl = em.createNativeQuery("delete from EMPLOYEE");
			Query deleteTask = em.createNativeQuery("delete from TASK");
			Query deleteProject = em.createNativeQuery("delete from PROJECT");

			deleteTask.executeUpdate();
			deleteEmpl.executeUpdate();
			deleteProject.executeUpdate();
		});
	}

	private void populateDatabase() {
		executeInEntityManagerTransaction(em, () -> {
			Departament dep1 = new Departament().setName("departament1");
			Departament dep2 = new Departament().setName("departament2");
			Departament dep3 = new Departament().setName("departament3");

			persistDepartaments(dep1, dep2, dep3);

			Employee e1 = new Employee().setName("Popescu").setSurname("Maria").setDepartament(dep1);
			Employee e2 = new Employee().setName("Popescu").setSurname("Ion").setDepartament(dep2);
			Employee e3 = new Employee().setName("Ionescu").setSurname("Anabela").setDepartament(dep1);
			Employee e4 = new Employee().setName("Marinescu").setSurname("Ionela").setDepartament(dep3);

			persistEmployees(e1, e2, e3, e4);

			Calendar cal = Calendar.getInstance();
			cal.set(2016, 10, 10);
			Date d1 = cal.getTime();

			Task t1 = new Task().setTaskDescription("taskDescription1").setEmployee(e1).setStartDate(d1)
					.setStatus(Status.NEW);
			Task t2 = new Task().setTaskDescription("taskDescription2").setEmployee(e2).setStartDate(d1)
					.setStatus(Status.IN_PROGRESS);
			Task t3 = new Task().setTaskDescription("taskDescription3").setEmployee(e3).setStartDate(d1)
					.setStatus(Status.FINISHED);

			persistTasks(t1, t2, t3);

			Project p1 = new Project().setName("projectName1").setDescription("projectDescription1")
					.setStatus(Status.NEW).setTasks(Arrays.asList(t3));
			Project p2 = new Project().setName("projectName2").setDescription("projectDescription2")
					.setStatus(Status.IN_PROGRESS).setTasks(Arrays.asList(t1, t2));
			Project p3 = new Project().setName("projectName3").setDescription("projectDescription3")
					.setStatus(Status.NEW);

			persistProjects(p1, p2, p3);

			t1.setProject(p2);
			t2.setProject(p2);
			t3.setProject(p1);

			em.merge(t1);
			em.merge(t2);
			em.merge(t3);
		});
	}

	private void persistProjects(Project p1, Project p2, Project p3) {
		em.persist(p1);
		em.persist(p2);
		em.persist(p3);
	}

	private void persistTasks(Task t1, Task t2, Task t3) {
		em.persist(t1);
		em.persist(t2);
		em.persist(t3);
	}

	private void persistEmployees(Employee e1, Employee e2, Employee e3, Employee e4) {
		em.persist(e1);
		em.persist(e2);
		em.persist(e3);
		em.persist(e4);
	}

	private void persistDepartaments(Departament dep1, Departament dep2, Departament dep3) {
		em.persist(dep1);
		em.persist(dep2);
		em.persist(dep3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void listAllProjects() {
		executeInEntityManagerTransaction(em, () -> {
			List<Project> projects = (List<Project>) em.createQuery("select p from Project p order by p.id")
					.getResultList();
			System.out.println("ALL PROJECTS: " + projects);
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void removeFirstProject() {
		executeInEntityManagerTransaction(em, () -> {
			List<Project> projects = (List<Project>) em.createQuery("select p from Project p order by p.id")
					.getResultList();
			em.remove(projects.get(0));

			// force propagation of the modifications to underlying database
			em.flush();

			projects = (List<Project>) em.createQuery("select p from Project p").getResultList();
			System.out.println("PROJECTS AFTER REMOVAL : " + projects);
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listTasksInProgress() {
		executeInEntityManagerTransaction(em, () -> {
			List<Task> inProgressTasks = em.createNamedQuery("findTasksByStatus")
					.setParameter("status", Status.IN_PROGRESS).getResultList();
			System.out.println("IN PROGRESS TASKS: " + inProgressTasks);
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listEmployeesWithSpecifiedNameAndSurname() {
		executeInEntityManagerTransaction(em, () -> {
			List<Employee> employees = em
					.createQuery("select e from Employee e where e.name like ?1 " + "and e.surname like ?2")
					.setParameter(1, "Popescu%").setParameter(2, "Ion").getResultList();
			System.out.println("EMPLOYEES WITH NAME starting with Popescu: " + employees);
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listProjectsWithOpenTasks() {
		executeInEntityManagerTransaction(em, () -> {
			List<Employee> projects = em.createQuery("select p from Project p join p.tasks t where "
					+ "t.status != com.training.hibernateDemo.util.Status.FINISHED ").getResultList();
			System.out.println("PROJECTS WITH OPEN TASKS: " + projects);

		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listEmployeesWithoutTask() {
		executeInEntityManagerTransaction(em, () -> {
			List<Employee> employees = em
					.createQuery(
							"select e from Employee e where e NOT IN " + "(select e from Task t join t.employee e)")
					.getResultList();
			System.out.println("EMPLOYEES WITHOUT TASKS: " + employees);
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void countTasksByStatus() {
		executeInEntityManagerTransaction(em, () -> {
			String query = "select t.status, COUNT(t) from Task t GROUP BY t.status";
			List<Object[]> results = em.createQuery(query).getResultList();
			if (results != null) {
				for (Object[] result : results) {
					System.out.println(result[0] + " - " + result[1]);
				}
			}
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listProjectsWithoutTasks() {
		executeInEntityManagerTransaction(em, () -> {
			List<Project> emptyProjects = (List<Project>) em
					.createQuery("select p from Project p " + "where p.tasks is EMPTY").getResultList();
			System.out.println("PROJECTS WITHOUT TASKS: " + emptyProjects);

		});
	}

	@Test
	public void saveAndRemoveProject() {
		Project p4 = new Project().setName("projectName4").setDescription("projectDescription4").setStatus(Status.NEW);

		executeInEntityManagerTransaction(em, () -> {
			em.persist(p4);
		});

		Long projectId = p4.getId();
		//we have an id for project now
		System.out.println("PROJECT ID: " + projectId);

		// em is still active (close() method wasn't called, so we can open other
		// transactions)
		//remove the newly added project
		executeInEntityManagerTransaction(em, () -> {
			em.remove(p4);
		});

		EntityManager em1 = PersistenceManager.INSTANCE.getEntityManager();
		//is the new project in the database, can we load it?
		Project project = em1.find(Project.class, projectId);
		System.out.println("NEW PROJECT: " + project);
	}

	@Test
	public void saveAndUpdateProject() {
		Project p5 = new Project().setName("projectName5").setDescription("projectDescription5").setStatus(Status.NEW);

		executeInEntityManagerTransaction(em, () -> {
			em.persist(p5);

			// p5 is in the persistence context now and it is modified
			// the modifications will be propagated in the database on commit
			p5.setDescription("projectDescription-MODIFIED");
		});

		Project project = em.find(Project.class, p5.getId());
		System.out.println(project);
	}

	@Test
	public void queryWithPositionalParameters() {
		@SuppressWarnings("unchecked")
		List<Employee> employees = (List<Employee>) em
				.createQuery("select e from Employee e where e.name like ?7 and e.surname like ?3")
				.setParameter(3, "Ion").setParameter(7, "Popescu%").getResultList();

		System.out.println(employees);
		// note that the positional parameters must not be ordered values like ?1, ?2, ?3
		// name and order of the positional parameters is not important
		@SuppressWarnings("unchecked")
		List<Employee> employees2 = (List<Employee>) em
				.createQuery("select e from Employee e where e.name like ?7 and e.surname like ?3")
				.setParameter(3, "?3").setParameter(7, "Popescu%").getResultList();

		System.out.println(employees2);
	}

	@Test
	public void listDepartaments() {
		@SuppressWarnings("unchecked")
		List<Departament> departaments = (List<Departament>) em.createQuery("select d from Departament d")
				.getResultList();
		System.out.println("DEPARTAMENTS: " + departaments);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listEmployeesByDepartament() {
		List<Employee> employees = (List<Employee>) em
				.createQuery("select e from Employee e order by e.departament.name ASC").getResultList();
		System.out.println("Employees sorted by departament name: " + employees);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void countEmployeesByDepartament() {
		executeInEntityManagerTransaction(em, () -> {
			String query = "select e.departament.name, COUNT(e) from Employee e GROUP BY e.departament.name";

			List<Object[]> results = em.createQuery(query).getResultList();
			if (results != null) {
				for (Object[] result : results) {
					System.out.println(result[0] + " - " + result[1] + " employees");
				}
			}
		});
	}

	private void executeInEntityManagerTransaction(EntityManager em, Runnable operation) {
		em.getTransaction().begin();
		operation.run();
		em.getTransaction().commit();
	}
}
