package space.pankui.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;

import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author pankui
 * @date 15/05/2018
 * <pre>
 *
 * </pre>
 */

//@Endpoint(id = "person")
//@Component
public class PersonEndpoint {
/*
    private final Map<String, Person> people = new ConcurrentHashMap<>();

    PersonEndpoint() {
        this.people.put("mike", new Person("Michael Redlich"));
        this.people.put("rowena", new Person("Rowena Redlich"));
        this.people.put("barry", new Person("Barry Burd"));
    }

    @ReadOperation
    public List<Person> getAll() {
        return new ArrayList<>(this.people.values());
    }

    @ReadOperation
    public Person getPerson(@Selector String person) {
        return this.people.get(person);
    }

    @WriteOperation
    public void addOrUpdatePerson(@Selector String name, String person) {
        this.people.put(name, new Person(person));
    }

    public static class Person {
        private String name;

        Person(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }*/
}