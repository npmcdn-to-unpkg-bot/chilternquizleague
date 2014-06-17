package org.chilternquizleague.json;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public class RefTypeAdaptor extends TypeAdapter<Ref<?>> implements InstanceCreator<Ref<?>> {

	@Override
	public Ref<?> read(JsonReader reader) throws IOException {

		reader.beginObject();

		while (reader.hasNext()) {

			long id = reader.nextLong();
			String kind = reader.nextString();

			Key<Object> key;
			
			try {
				key = Key.create(Class.forName(kind), id);

				return Ref.create(key);
			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			}

		}
		
		reader.endObject();

		return null;
	}

	@Override
	public void write(JsonWriter writer, Ref<?> ref) throws IOException {
		writer.beginObject();
		writer.name("id").value(ref.getKey().getId());
		writer.name("kind").value(ref.getKey().getKind());
		writer.endObject();

	}

	@Override
	public Ref<?> createInstance(Type type) {
		return Ref.create(Key.create(""));
	}

}
