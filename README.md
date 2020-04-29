# BackpacksRemastered <[bukkit.org](https://dev.bukkit.org/projects/backpack-item)>
[![](https://img.shields.io/travis/divisionind/BackpacksRemastered/master.svg?style=flat-square)](https://travis-ci.org/divisionind/BackpacksRemastered)
![](https://img.shields.io/github/repo-size/divisionind/BackpacksRemastered.svg?style=flat-square)
![](https://img.shields.io/badge/license-GPLv3-green.svg?style=flat-square)

![](https://raw.githubusercontent.com/divisionind/BackpacksRemastered/master/screenshots/logo.png)
> Backpacks is a very unique plugin meant to add backpacks to Minecraft without the use of any client side modifications. 
Completely vanilla backpacks! This plugin is the first of its kind due to the fact that it adds backpacks as an item! Yes, 
you can craft backpacks or obtain them through commands. Backpacks can also be dropped on the floor, stored in a chest, or 
even stored in another backpack (configurable) and will always retain their inventory. Backpacks is also one of the only 
plugins (I know of at least) that allows you to view the specific NBT tags on items.

## Table of contents
**[Building](#building)**<br>
**[Adding languages](#adding-languages)**<br>
**[Adaptors and Integration](#adaptors-and-integration)**<br>

## Building
Requirements:
  - Gradle
  - Java 8 **and** 11 (if on windows try AdoptOpenJDK)
  - Git
  
One major note which needs to be made is that the i18nExtractor plugin uses Java 11 features. This means that Gradle must be
run with Java 11. However, most Minecraft servers are still on Java 8, so, the final plugin must be built for Java 8.

#### Setting up Gradle:
##### The Console
1. install both JDKs
2. set Java 11 as your default
3. proceed with the build normally

##### IntelliJ IDEA
1. again, install both JDKs
2. go to `File->Project Structure`
3. change "Project SDK" to Java 11
4. change "Project language level" to Java 8

For other IDEs, try the console method or just otherwise configure it to run Gradle with Java 11. Gradle should figure
out that it needs to build for Java 8 on its own.

#### The steps
*NOTE: These steps will be for building from the console. Most IDEs will simply provide you with a way to run Gradle
tasks, just add an entry for the task "pack".*
1. clone the repo `git clone https://github.com/divisionind/BackpacksRemastered.git`
2. enter repo dir `cd BackpacksRemastered`
3. run the build task `gradlew pack`
4. The final plugin jar will be located in `build/libs/BackpacksRemastered*.jar`

The first build will translate all of the strings from the project using google translate (see *Adding languages*)
this may fail as the project is very large. If it does, wait a few hours to a day and try to build again to get the
rest of the strings. If you don't want this to be an issue, remove some languages.

## Adding languages
This plugin uses the i18nExtractor gradle plugin written by me (drew6017) for automatically extracting / translating
strings into other languages. You can specify any language supported by Google Translate in the last line
under the internationalize task.

__NOTE: Google loves throwing 429 errors (too many requests) when you use this because we are essentially spamming 
google with a translation request for each string we choose to add.__ You could modify this to use googles translation
api but you are then subject to limitations (and you have to pay). Translations are cached to the ".i18nExtractor"
directory. Delete the cache corresponding to a particular language to refresh it. Be careful how many languages you
add/refresh at once because too many requests ~70-150 (depending on the trust factory of the network requesting translation) 
in one go will cause you to get locked out by google for several hours. __If you see any message about a 429 error in 
the console, it means that a string was not translated and the resulting jar should not be used with other languages 
as this WILL result in errors.__

Also, the i18nExtractor plugin is currently licensed as "All rights reserved" to Division Industries LLC. You may not
copy or modify any code from it. However, there is an API for creating custom translators (which you are free to do).

### Steps
1. Find the section of the build file that looks like `langs('es', 'it', 'fr')` (If you are familiar with gradle, 
   it should be under the internationalize task).
2. Add your desired [valid Google translate language code](https://cloud.google.com/translate/docs/languages) to the
   list.
3. Build the plugin.
4. You should now be able to use this same language code in the [config.yml](https://github.com/divisionind/BackpacksRemastered/blob/master/src/main/resources/config.yml) 
   of Backpacks. (minus any -X extensions, e.g. "zh-CH" would be "zh")

##### Notes
- Translations are cached in the `.i18nExtractor` directory, delete the cache file corresponding to your desired language
  to grab the latest translations of that language from google.
- See above notes about 429 errors.

## Adaptors and Integration
Backpacks supports 3rd-party plugin integration through the use of "Adaptors". These adaptors can register custom commands
or other custom functionality through the use of "Abilities" (functions with re-definable meaning used internally).

#### An example adaptor
Below is an example of a plugin adaptor for a plugin named "ExamplePlugin". The name value of the `@PluginAdaptorMeta`
annotation must be the name of your plugin as registered by your plugin.yml.
```java
@PluginAdaptorMeta(name = "ExamplePlugin")
public class AdaptorExamplePlugin extends PluginAdaptor {

    private ExamplePlugin parent;

    @Override
    public void onEnable(Plugin parent) throws Exception {
        this.parent = (ExamplePlugin) parent;

        Backpacks.getInstance().registerCommands(new CExampleCommand());
        getLogger().info("Registered ExamplePlugin adaptor!");
    }

    @AbilityFunction
    public boolean hasAccessToContainer(Player player, Location location) {
        // ... logic
        return true; // or false
    }

    private static class CExampleCommand extends ACommand {
        @Override
        public String alias() {
            return "example";
        }

        @Override
        public String desc() {
            return "an example command registered by an example adaptor";
        }

        @Override
        public String usage() {
            return null;
        }

        @Override
        public String permission() {
            return "backpacks.example";
        }

        @Override
        public void execute(CommandSender sender, String label, String[] args) {
            // need a player instance? Player player = validatePlayer(sender);
            // if the sender is not a player, the command will return and respond accordingly
            respond(sender, "&eHello world!");
        }
    }
}
```

#### Loading the adaptor
```java
public class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        Backpacks.getAdaptorManager().registerAdaptors(AdaptorExamplePlugin.class);
        getLogger().info("ExamplePlugin has been enabled!");
    }
} 
```

Methods tagged with the `@AbilityFunction` annotation inside of a `PluginAdaptor` are automatically registered
as abilities with the system. If no value is specified (e.g. `@AbilityFunction("someName")`) then the method's 
name is used as the ability name. Abilities overwrite each other, so, if multiple adaptors register an ability
with the same name, the last adaptor loaded will have the ability that persists. The following is a list of
current abilities used internally by Backpacks:

#### Current abilities
- `boolean hasAccessToContainer(Player, Location)`: returns true if the player has access to the block at the 
  specified location, otherwise false