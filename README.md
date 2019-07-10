# BackpacksRemastered
[![](https://img.shields.io/travis/divisionind/BackpacksRemastered/master.svg?style=flat-square)](https://travis-ci.org/divisionind/BackpacksRemastered)
![](https://img.shields.io/github/repo-size/divisionind/BackpacksRemastered.svg?style=flat-square)
![](https://img.shields.io/badge/license-GPLv3-green.svg?style=flat-square)
![](https://img.shields.io/badge/dev%20status-active-brightgreen.svg?style=flat-square)

## Info
This is a remastered version of the popular Backpacks plugin. This project is based on the
spigot boilerplate template by drew6017 on GitHub [here](https://github.com/divisionind/BoilerplateSpigot).

## Download
Please download the plugin from the Official Bukkit page [here](https://dev.bukkit.org/projects/backpack-item). I do 
not get any ad revenue if you download the plugin from the releases page on GitHub. Those download links are only here 
for convenience and to provide an archive of sources for particular versions if someone wishes to fork the project
in the future.

## Adding languages
This plugin uses the i18nExtractor plugin written by me (drew6017) to automatically extract / translating
strings into other languages. You can specify any language supported by Google Translate in the last line
under the internationalize task.

__NOTE: Google loves throwing 429 errors (too many requests) when you use this because we are essentially spamming 
google with a translation request for each string we choose to add. Be careful how many translations you add and
if you see any message about this in the console, it means some strings in your program are not present for whatever
language it was translating at the time.__

Also, the i18nExtractor plugin is currently licensed as "All rights reserved" to Division Industries LLC. You may not
copy or modify any code from it. However, there is an API for creating custom translators (which you are free to do).


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