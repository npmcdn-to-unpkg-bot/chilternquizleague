package org.chilternquizleague.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public class Utils {

	private final static DateFormat format = new SimpleDateFormat("yyyyMMdd");

	public static <T extends BaseEntity> Ref<T> entityToRef(final T entity){
		
		if (entity.id == null){
			
			final Key<T> key = ofy().save().entity(entity).now();
			return Ref.create(key);
		}
		else
		{
			return Ref.create(entity);
		}
		
	}
	
	
	public static <T extends BaseEntity> List<Ref<T>> entitiesToRefs(
			List<T> entities) {

		final List<Ref<T>> refs = new ArrayList<>();

		for (T entity : entities) {

			refs.add(entityToRef(entity));
		}

		return refs;
	}

	public static <T extends BaseEntity> List<T> refsToEntities(
			List<Ref<T>> refs) {

		final List<T> entities = new ArrayList<>();

		for (Ref<T> ref : refs) {

			final T entity = ref.get();
			if(entity != null){
				entities.add(entity);
			}
			
		}

		return entities;
	}

	public static <U, T extends BaseEntity> Map<U, List<Ref<T>>> entitiesToRefs(
			Map<U, List<T>> entities) {

		final Map<U, List<Ref<T>>> refs = new HashMap<>();

		for (Map.Entry<U, List<T>> entry : entities.entrySet()) {

			refs.put(entry.getKey(), entitiesToRefs(entry.getValue()));
		}

		return refs;
	}
	


	public static <U, T extends BaseEntity> Map<U, List<T>> refsToEntities(
			Map<U, List<Ref<T>>> refs) {

		final Map<U, List<T>> entities = new HashMap<>();

		for (Map.Entry<U, List<Ref<T>>> entry : refs.entrySet()) {

			entities.put(entry.getKey(), refsToEntities(entry.getValue()));
		}

		return entities;
	}

	public static <U, T extends BaseEntity> Map<U, Ref<T>> entityToRefMap(
			Map<U, T> entities) {

		final Map<U, Ref<T>> refs = new HashMap<>();

		for (Map.Entry<U, T> entry : entities.entrySet()) {

			refs.put(entry.getKey(), Ref.create(entry.getValue()));
		}

		return refs;
	}

	public static <U, T extends BaseEntity> Map<U, T> refToEntityMap(
			Map<U, Ref<T>> refs) {

		final Map<U, T> entities = new HashMap<>();

		for (Map.Entry<U, Ref<T>> entry : refs.entrySet()) {

			entities.put(entry.getKey(), entry.getValue().get());
		}

		return entities;
	}

	public static boolean isSameDay(Date date1, Date date2) {

		return format.format(date1).compareTo(format.format(date2)) == 0;
	}

}
