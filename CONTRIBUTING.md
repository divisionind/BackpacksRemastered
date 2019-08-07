## Contributing
All contributions must follow 
[Java's standard code conventions.](https://www.oracle.com/technetwork/java/codeconventions-150003.pdf) 
Failure to comply to these conventions will result in the denial of your contribution, regardless 
of the final functioning of the code.

### What does this mean?
Not a lot. Just DO **NOT** do stuff like this:

```java
public void example()
{
    System.out.println("bad code");
}
```

or this

```java
System.out.println( getName( bad_code ) );
```

or this

```java
public void AnotherBadExample() {}
```

These practices make your code harder to read and are very annoying.

* * *

Do **THIS** instead:

```java
public void example() {
    System.out.println("good code");
}
```

or this

```java
System.out.println(getName(goodCode));
```

or this

```java
public void anotherGoodExample() {}
```
