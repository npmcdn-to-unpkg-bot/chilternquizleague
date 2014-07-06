package org.chilternquizleague.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Ref;

public class Utils {
	

private final static DateFormat format  = new SimpleDateFormat("yyyyMMdd");

	
	public static <T> List<Ref<T>> entitiesToRefs(List<T> entities){
		
		final List<Ref<T>> refs = new ArrayList<>();
		
		for(T entity : entities){
			
			refs.add(Ref.create(entity));
		}
		
		return refs;
	}
	
	public static <T> List<T> refsToEntities(List<Ref<T>> refs){
		
		final List<T> entities = new ArrayList<>();
		
		for(Ref<T> ref : refs){
			
			entities.add(ref.get());
		}
		
		return entities;
	}
	
	public static <U,T> Map<U, List<Ref<T>>> entitiesToRefs(Map<U, List<T>> entities){
		
		final  Map<U, List<Ref<T>>> refs = new HashMap<>();
		
		for(Map.Entry<U,List<T>> entry : entities.entrySet()){
			
			refs.put(entry.getKey(), entitiesToRefs(entry.getValue()));
		}
		
		return refs;
	}
	
	public static <U,T> Map<U, List<T>> refsToEntities(Map<U, List<Ref<T>>> refs){
		
		final Map<U, List<T>> entities = new HashMap<>();
		
		for(Map.Entry<U,List<Ref<T>>> entry : refs.entrySet()){
			
			entities.put(entry.getKey(), refsToEntities(entry.getValue()));
		}
		
		return entities;
	}
	
	public static <U,T> Map<U, Ref<T>> entityToRef(Map<U, T> entities){
		
		final  Map<U, Ref<T>> refs = new HashMap<>();
		
		for(Map.Entry<U,T> entry : entities.entrySet()){
			
			refs.put(entry.getKey(), Ref.create(entry.getValue()));
		}
		
		return refs;
	}
	
	public static <U,T> Map<U, T> refToEntity(Map<U, Ref<T>> refs){
		
		final Map<U, T> entities = new HashMap<>();
		
		for(Map.Entry<U,Ref<T>> entry : refs.entrySet()){
			
			entities.put(entry.getKey(), entry.getValue().get());
		}
		
		return entities;
	}
	
	
	public static boolean isSameDay(Date date1, Date date2){
		
		return format.format(date1).compareTo(format.format(date2)) == 0;
	}
	

}
