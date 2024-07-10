package ru.learn.db.interfaces;

import java.net.URI;

public interface ServiceRepository {

	void create(String name, URI uri) throws Exception;

	URI getServiceUri(String name);

}
