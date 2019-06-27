# BackpacksRemastered
[![](https://img.shields.io/travis/divisionind/BackpacksRemastered/master.svg?style=flat-square)](https://travis-ci.org/divisionind/BackpacksRemastered)
![](https://img.shields.io/github/repo-size/divisionind/BackpacksRemastered.svg?style=flat-square)
![](https://img.shields.io/badge/license-GPLv3-green.svg?style=flat-square)
![](https://img.shields.io/badge/dev%20status-active-brightgreen.svg?style=flat-square)

## Info
This is a remastered version of the popular Backpacks plugin. This project is based on the
spigot boilerplate template by drew6017 on GitHub [here](https://github.com/divisionind/BoilerplateSpigot).

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