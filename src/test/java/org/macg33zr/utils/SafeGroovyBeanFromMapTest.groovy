package org.macg33zr.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson
import groovy.sql.Sql


public class SafeGroovyBeanFromMapTest extends SafeGroovyBeanFromMap {

	@Test
	public void test_CorrectProperties_Normal_Construction() {
		
		def props = [first:'Tom', last: 'Smith', age: 42]
		
		def person = new Person(props)
		
		assert person.first == 'Tom'
		assert person.last  == 'Smith'
		assert person.age   == 42
	}
	
	@Test
	public void test_FewProperties_Normal_Construction() {
		
		def props = [first:'Tom', age: 42]
		
		def person = new Person(props)
		
		assert person.first == 'Tom'
		assert person.last  == null
		assert person.age   == 42
	}
	
	@Test(expected = groovy.lang.MissingPropertyException.class)
	public void test_The_Problem_Trying_To_Solve() {
		
		def props = [first:'Tom', last: 'Smith', age: 42, city:'Chicago']
		
		def person = new Person(props)		
	}
	
	@Test
	public void test_Safe_Construction_Too_Many_Properties() {
		
		def props = [first:'Tom', last: 'Smith', age: 42, city:'Chicago']
		
		def person = SafeGroovyBeanFromMap.createFromMap(Person, props)
		
		println "Person: ${new Gson().toJson(person).toString()}"
		
		assert person.first == 'Tom'
		assert person.last  == 'Smith'
		assert person.age   == 42
	}
	
	@Test
	public void test_Safe_Construction_Correct_Properties() {
		
		def props = [first:'Tom', last: 'Smith', age: 42]
		
		def person = SafeGroovyBeanFromMap.createFromMap(Person, props)
		
		println "Person: ${new Gson().toJson(person).toString()}"
		
		assert person.first == 'Tom'
		assert person.last  == 'Smith'
		assert person.age   == 42
	}

	@Test
	public void test_Safe_Construction_Correct_Properties_Category_Style() {
		
		def props = [first:'Tom', last: 'Smith', age: 42]
		
		def person 
		
		use ( SafeGroovyBeanFromMap ) {
			person = Person.class.createFromMap( props)
		}
		
		println "Person: ${new Gson().toJson(person).toString()}"
		
		assert person.first == 'Tom'
		assert person.last  == 'Smith'
		assert person.age   == 42
	}
		
	@Test
	public void test_Safe_Construction_Few_Properties() {
		
		def props = [first:'Tom', age: 42]
		
		def person = SafeGroovyBeanFromMap.createFromMap(Person, props)
		
		println "Person: ${new Gson().toJson(person).toString()}"
		
		assert person.first == 'Tom'
		assert person.last  == null
		assert person.age   == 42
	}
	
	
	class DataBaseObject {
	
		String name
		String description	
	}

	@Test
	public void test_From_Database() {
		
		def sql = Sql.newInstance("jdbc:hsqldb:mem:testdb", "sa", "", "org.hsqldb.jdbcDriver")
		
		sql.execute("""
	    create table test (
	        "name" varchar(100),
	        "description" varchar(100),
			 "rogue"      varchar(32)
	    )""")
		
		def names = ["tom", "dick", "harry"]
		names.each {
			sql.execute("""insert into test("name", "description", "rogue") VALUES (?,?,?)""", [it, "This is $it".toString(), "ROGUE! $it".toString()])
		}
	
		def list = []
		sql.eachRow("""select "name", "description", "rogue" from test""") { row ->
			
			use(SafeGroovyBeanFromMap) {
			
				list <<  DataBaseObject.class.createFromMap(row.toRowResult() )
			}
		}
		
		list.each {
			println "${new Gson().toJson(it).toString()}"
			
			assert it.name in names
			assert it.description != null
		}
	}

}
