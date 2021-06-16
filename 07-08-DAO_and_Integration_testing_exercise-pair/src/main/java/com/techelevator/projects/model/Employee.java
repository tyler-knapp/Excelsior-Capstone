package com.techelevator.projects.model;

import java.time.LocalDate;
import java.util.Objects;

public class Employee {
	private Long employeeId;
	private Long departmentId;
	private String firstName;
	private String lastName;
	private LocalDate birthDay;
	private LocalDate hireDate;
	
	public Long getId() {
		return employeeId;
	}
	public void setId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public long getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public LocalDate getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(LocalDate birthDay) {
		this.birthDay = birthDay;
	}
	public LocalDate getHireDate() {
		return hireDate;
	}
	public void setHireDate(LocalDate hireDate) {
		this.hireDate = hireDate;
	}
	public String toString() {
		return lastName + ", " + firstName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Employee employee = (Employee) o;
		return Objects.equals(employeeId, employee.employeeId) && Objects.equals(departmentId, employee.departmentId) && Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName) && Objects.equals(birthDay, employee.birthDay) && Objects.equals(hireDate, employee.hireDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(employeeId, departmentId, firstName, lastName, birthDay, hireDate);
	}
}
