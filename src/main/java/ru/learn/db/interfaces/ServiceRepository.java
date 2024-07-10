package ru.learn.db.interfaces;

import java.net.URI;

public interface ServiceRepository {

	void create(String name, URI uri);

	URI getServiceUri(String name);

}
