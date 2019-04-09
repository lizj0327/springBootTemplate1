package com.tmp.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tmp.jpa.domain.JpaEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="test2")
@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "aa", scope = Test1.class)
public class Test1  extends JpaEntity<String> {

	@Id
	private String aa;
	private String bb;
	private String cc;
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return aa;
	}
	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub
		this.aa = id;
	}
}
