package org.macg33zr.utils

/*
 * A utility to safely load a POGO (Plain Old Groovy Object / Bean) from a 
 * map without throwing MissingPropertyException
 * 
 * Use it category style like this:
 * 
 * def personList = []
 * use (SafeGroovyBeanFromMap) {
 *    personList << Person.class.createFromMap( sql.eachRow("select * from person").toRowResult() ) 
 * }
 * 
 * 
 */
class SafeGroovyBeanFromMap {
	 
	static Object createFromMap(beanClass, propertyMap) {
		
		def filterBeanProperties = { _beanClass, _propertyMap ->
			
			def filtered = [:]
	
			_propertyMap.each { k,v ->
				!_beanClass.metaClass.properties.find {it.name == k } ?: filtered.put(k,v)
			}
			return filtered
		}
						
		beanClass.newInstance( filterBeanProperties( beanClass, propertyMap ) )	
	}
}
