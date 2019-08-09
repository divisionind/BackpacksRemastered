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
google with a translation request for each string we choose to add.__ You could modify this to use googles translation
api but you are then subject to limitations (and you have to pay). Translations are cached to the ".i18nExtractor"
directory. Delete the cache corresponding to a particular language to refresh it. Be careful how many languages you
add/refresh at once because too many requests ~70-150 (depending on the trust factory of the network requesting translation) 
in one go will cause you to get locked out by google for several hours. __If you see any message about a 429 error in 
the console, it means that a string was not translated and the resulting jar should not be used with other languages 
as this WILL result in errors.__

Also, the i18nExtractor plugin is currently licensed as "All rights reserved" to Division Industries LLC. You may not
copy or modify any code from it. However, there is an API for creating custom translators (which you are free to do).
