# CloudManager [![Circle CI](https://circleci.com/gh/dani-garcia/cloud-manager.svg?style=svg&circle-token=457861990f061840af93d8ea879b5a9021b838e0)](https://circleci.com/gh/dani-garcia/cloud-manager)
TODO: Write a project description

## Installation
Compile the project natives as shown below and install them on your system. These native
files don't require Java to be installed on the target system, as they already include it.

Alternatively, if you wish to run the program in a portable fashion, simply compile the jar file.
This requires Java 8 and JavaFX 8 to be installed on the target computer.

* On Windows and OSX, it's as simple as installing the latest Java 8 JRE
[Download link](http://www.oracle.com/technetwork/java/javase/overview/index.html).

* On Debian/Ubuntu, this requires the `openjdk-8-jre` and `openjfx` packages.
As Java 8 is relatively new, these packages are only available in recent versions
of these distributions. At the time of writing this readme, the packages are available
on Debian 7 `Jessie` on `jessie-backports` and on Ubuntu 15.04 `vivid` and newer.

```sh
# On Debian 8 Jessie:
$ sudo apt-get -t jessie-backports install openjfx

# On Debian 9 Stretch and Ubuntu 15.04 Vivid or later:
$ sudo apt-get install openjfx
```

## Developing

The project is a normal Maven project, to open it simply import the
project in your preferred IDE (IntelliJ has Maven support by default, other
IDEs may need a plugin).

This project requires API keys from Google Drive and Dropbox. As these cannot be distributed publicly,
you will need to get your own. Then copy `apikeys.properties.sample` to `apikeys.properties` and fill the data.

If running the project from the IDE returns an error saying `the API keys are not set`, you might
need to run `maven install` before (This can usually be configured in the IDE's run configuration).


## Compiling and running
To run the application:
```sh
$ mvn clean install
$ cd cm-gui
$ mvn jfx:run
```

To compile a .jar and native program (.exe or .msi installer on Windows, .deb or .rpm on Linux, .dmg on OSX):
```sh
$ mvn clean install
$ cd cm-gui
$ mvn jfx:jar       # To compile the .jar only
$ mvn jfx:native    # To compile the .jar and the natives
```

This will place the .jar file and the required libraries in the `cm-gui/target/jfx/app` folder and the native files in the `cm-gui/target/jfx/native` folder.

## Credits
Daniel García García <UO231763@uniovi.es>

## License
CloudManager
Copyright (C) 2016  Daniel García García

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.